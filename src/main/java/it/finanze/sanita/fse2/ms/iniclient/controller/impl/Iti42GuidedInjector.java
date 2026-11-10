/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.iniclient.controller.impl;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import com.sun.xml.ws.developer.JAXWSProperties;

import static it.finanze.sanita.fse2.ms.iniclient.enums.EventType.INI_CREATE_SOAP;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.EVENT_DATE;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.EVENT_TYPE;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniAudit.WII;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Element;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.iniclient.config.GovwayCfg;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.SAMLShieldHandler;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.SOAPLoggingHandler;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.ShieldException;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.ShieldAssertionEngine;
import it.finanze.sanita.fse2.ms.iniclient.dto.iti42.ForgedIti42Request;
import it.finanze.sanita.fse2.ms.iniclient.dto.iti42.GuidedPublicationRequest;
import it.finanze.sanita.fse2.ms.iniclient.dto.iti42.GuidedInputValidation;
import it.finanze.sanita.fse2.ms.iniclient.enums.guided.GuidedChoices;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.ISecuritySRV;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.AuditIniSrv;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.Iti42SoapForge;

/** Internal ITI-42 endpoint; never uses workflow or mock orchestration. */
@RestController
@RequestMapping("/internal/iti-42")
@Tag(name = "Operazioni guidate ITI-42", description = "Solo CREATE, senza JWT o orchestrazione del workflow.")
public class Iti42GuidedInjector {

    private static final int MAX_RESPONSE_BYTES = 2 * 1024 * 1024;

    private final Iti42SoapForge forge;
    private final ISecuritySRV security;
    private final GovwayCfg govway;
    private final ShieldAssertionEngine shield;

    private final AuditIniSrv auditIniSrv;
    private final IConfigSRV configSRV;

    public Iti42GuidedInjector(Iti42SoapForge forge, ISecuritySRV security, GovwayCfg govway,
            ShieldAssertionEngine shield, AuditIniSrv auditIniSrv, IConfigSRV configSRV) {

        this.forge = forge;
        this.security = security;
        this.govway = govway;
        this.shield = shield;
        this.auditIniSrv = auditIniSrv;
        this.configSRV = configSRV;
    }

    @PostMapping(value = "/guided", consumes = "application/x-www-form-urlencoded")
    @Operation(summary = "Pubblicazione guidata ITI-42 — CREATE", description = """
            ### Modalità
            - **Anteprima** (`dryRun=true`, predefinito): restituisce il SOAP firmato, senza inviarlo.
            - **Invio** (`dryRun=false`): registra su INI alla destinazione configurata e restituisce l'esito JSON.

            ### Compilazione
            - Sostituire gli esempi sintetici con i dati reali; i campi opzionali possono restare vuoti.
            - `patientId` è condiviso da tutti gli identificativi paziente nel SOAP.
            - Selezionare i valori **codice - denominazione**; le date usano `yyyyMMddHHmmss`.

            **Attenzione:** l'invio opera realmente su INI. Proteggere l'accesso e l'XML, che contiene dati sensibili.
            I controlli Shield non garantiscono la completa conformità INI. Non ripetere automaticamente un invio fallito:
            la consegna potrebbe essere già avvenuta.
            """)
    @ApiResponse(responseCode = "200", description = "dryRun=true: XML SOAP diretto; dryRun=false: esito JSON.",
            content = {
                    @Content(mediaType = "application/soap+xml", schema = @Schema(type = "string")),
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PublicationResponse.class))
            })
    @ApiResponse(responseCode = "400", description = "Parametri mancanti o non validi; nessuna firma o trasmissione.")
    @ApiResponse(responseCode = "422", description = "Richiesta rifiutata da Shield prima della trasmissione.")
    @ApiResponse(responseCode = "500", description = "Costruzione o firma fallita; nessuna registrazione ITI-42 trasmessa.")
    @ApiResponse(responseCode = "502", description = "Errore di trasporto, SOAP Fault o rifiuto del registro. "
            + "Dopo un tentativo di trasmissione la consegna può essere incerta: non ripetere automaticamente.")
    public ResponseEntity<?> publish(
            jakarta.servlet.http.HttpServletRequest httpRequest,
            @Parameter(description = "Identificativo paziente CX completo, condiviso da resource-id SAML, "
                    + "DocumentEntry.patientId, sourcePatientId e SubmissionSet.patientId. Formato: identificativo^^^&OID&ISO.",
                    example = "RSSMRA80A01H501U^^^&2.16.840.1.113883.2.9.4.3.2&ISO") @RequestParam String patientId,
            @Parameter(description = "Operatore SAML, distinto dal paziente. Formato CX: identificativo^^^&OID&ISO.",
                    example = "BNCLGU75B15F205X^^^&2.16.840.1.113883.2.9.4.3.2&ISO") @RequestParam String subjectId,
            @Parameter(description = "Selezione codice - nome del ruolo SAML; indipendente dai ruoli degli autori XDS.", example = "AAS - Personale di assistenza ad alta specializzazione") @RequestParam String role,
            @Parameter(description = "Deriva organization-id e Issuer SAML preservando gli zeri (Piemonte: 010). "
                    + "Nel legacy solo il sourceId perde uno zero iniziale; qui submissionSourceId è esplicito e indipendente.",
                    example = "950 - GTW") @RequestParam String organization,
            @Parameter(description = "Località SAML; esempio di istituzione composita XON, copiata senza derivazioni.",
                    example = "Ospedale dimostrativo^^^^^&2.16.840.1.113883.2.9.4.1.3&ISO^^^^950001") @RequestParam String locality,
            @Parameter(description = "Selezione codice - nome della finalità.", example = "TREATMENT - Trattamento di cura ordinario") @RequestParam String purposeOfUse,
            @Parameter(description = "Booleano true/false; TREATMENT richiede true.", example = "true") @RequestParam Boolean patientConsent,
            @Parameter(description = "Espressione HL7 completa: ('codice LOINC^^OID'); per più tipi: ('codice^^OID','codice^^OID').",
                    example = "('11502-2^^2.16.840.1.113883.6.1')") @RequestParam String resourceHl7Type,
            @Parameter(description = "Identificativo applicazione SAML, distinto dallo slot documentSubjectApplication.", example = "ITI42-DEMO") @RequestParam String applicationId,
            @Parameter(description = "Produttore dell'applicazione SAML; testo non vuoto.", example = "Fornitore dimostrativo") @RequestParam String applicationVendor,
            @Parameter(description = "Versione applicazione SAML; testo non vuoto, senza obbligo di versione numerica.", example = "1.0.0") @RequestParam String applicationVersion,

            @Parameter(description = "Titolo del documento; omettere se non disponibile, non inviare vuoto.", example = "Referto di laboratorio - esempio sintetico") @RequestParam(required = false) String title,
            @Parameter(description = "Valore MIME dei metadati, modificabile se necessario. Il valore usuale PDF/CDA è application/pdf+text/x-cda-r2+xml; non derivato dal formato.",
                    example = "application/pdf+text/x-cda-r2+xml") @RequestParam String mimeType,
            @Parameter(description = "OID numerico del repository documentale.", example = "2.16.840.1.113883.2.9.2.950.4.5.1") @RequestParam String repositoryUniqueId,
            @Parameter(description = "Slot XDS completo: identificativoApplicazione^produttore^versione; indipendente dagli attributi SAML.",
                    example = "ITI42-DOCUMENTI^Fornitore documentale^2.0.0") @RequestParam String documentSubjectApplication,
            @Parameter(description = "Identificativo univoco del documento, copiato senza normalizzazione; ammesse estensioni OID^valore.",
                    example = "2.16.840.1.113883.2.9.2.950.4.4.202611080001") @RequestParam String documentUniqueId,
            @Parameter(description = "SHA-1 del documento: esattamente 40 caratteri esadecimali. Esempio sintetico, non calcolato su un documento.",
                    example = "0123456789abcdef0123456789abcdef01234567") @RequestParam String hash,
            @Parameter(description = "Dimensione del documento in byte: intero long non negativo, non dimensione del SOAP.", example = "1234") @RequestParam Long size,
            @Parameter(description = "Creazione: yyyyMMddHHmmss, 14 cifre, data valida senza fuso orario.", example = "20261108080000") @RequestParam String creationTime,
            @Parameter(description = "Inizio prestazione: yyyyMMddHHmmss; opzionale, omettere se assente.", example = "20261108090000") @RequestParam(required = false) String serviceStartTime,
            @Parameter(description = "Fine prestazione: yyyyMMddHHmmss; non precedente all'inizio, se fornito. Opzionale.", example = "20261108100000") @RequestParam(required = false) String serviceStopTime,
            @Parameter(description = "Slot repository-type opzionale. Testo libero: dispatcher non definisce un enum.", example = "CONS")
            @RequestParam(required = false) String conservazioneANorma,
            @Parameter(description = "Descrizioni libere opzionali; ripetere il parametro per più valori.", example = "[\"Referto sintetico per anteprima SOAP\"]") @RequestParam(required = false) List<String> descriptions,
            @Parameter(description = "Selezioni codice - nome; ripetere il parametro per più valori. Deriva code^description nel SOAP.", example = "[\"SSN - Regime SSN\"]") @RequestParam List<String> administrativeRequests,
            @Parameter(description = "Riferimenti opzionali copiati senza normalizzazione; ammessi identificativo^^^^urn:tipo e identificativo^^^&OID&ISO^urn:tipo. Ripetere per più valori.",
                    example = "[\"2.16.840.1.113883.2.9.2.950.4.4.202611080001^^^^urn:ihe:iti:xdw:2013:workflowInstanceId\"]") @RequestParam(required = false) List<String> referenceIds,
            @Parameter(description = "Selezione codice - nome della classe documentale.", example = "REF - Referto") @RequestParam String classCode,
            @Parameter(description = "Selezione codice - nome della riservatezza.", example = "N - Normal") @RequestParam String confidentialityCode,
            @Parameter(description = "Selezione codice - nome del formato; non deriva il MIME.", example = "PDF - PDF") @RequestParam String formatCode,
            @Parameter(description = "Selezione codice - nome del tipo struttura.", example = "Ospedale - Ospedale") @RequestParam String healthcareFacilityTypeCode,
            @Parameter(description = "Selezione codice - nome dell'assetto organizzativo.", example = "AD_PSC001 - Allergologia") @RequestParam String practiceSettingCode,
            @Parameter(description = "Selezione codice LOINC - nome del tipo documento.", example = "11502-2 - Referto di Laboratorio") @RequestParam String typeCode,
            @Parameter(description = "Selezioni codice - nome evento; almeno una, ripetere il parametro per più valori. Tutti i valori sono disponibili; quelli con costante interna prefissata da underscore sono in fondo. Esempio illustrativo da adattare al documento.", example = "[\"J07AC - Anthrax vaccines\"]") @RequestParam List<String> eventCodes,

            @Parameter(description = "Autore clinico del documento; esempio XCN completo, copiato senza derivazioni dall'operatore SAML.",
                    example = "VRDLRA80A41H501U^Verdi^Laura^^^^^^&2.16.840.1.113883.2.9.4.3.2&ISO") @RequestParam String authorPerson,
            @Parameter(description = "Selezione codice - nome del ruolo autore documento; indipendente dal ruolo SAML.", example = "APR - Medico Medicina Generale Pediatra di Libera Scelta") @RequestParam String documentAuthorRole,
            @Parameter(description = "Istituzione autore documento; esempio XON completo, indipendente da locality.",
                    example = "Ospedale documentale^^^^^&2.16.840.1.113883.2.9.4.1.3&ISO^^^^950001") @RequestParam String documentAuthorInstitution,
            @Parameter(description = "OID numerico della sorgente della sottomissione, indipendente dall'organizzazione SAML.", example = "2.16.840.1.113883.2.9.2.950") @RequestParam String submissionSourceId,
            @Parameter(description = "Autore della sottomissione; esempio XCN completo, distinto dall'autore clinico.",
                    example = "BNCLGU75B15F205X^Bianchi^Luigi^^^^^^&2.16.840.1.113883.2.9.4.3.2&ISO") @RequestParam String submissionAuthorPerson,
            @Parameter(description = "Selezione codice - nome del ruolo autore sottomissione.", example = "APR - Medico Medicina Generale Pediatra di Libera Scelta") @RequestParam String submissionAuthorRole,
            @Parameter(description = "Istituzione autore sottomissione; esempio XON completo.",
                    example = "Ente inviante^^^^^&2.16.840.1.113883.2.9.4.1.3&ISO^^^^950002") @RequestParam String submissionAuthorInstitution,
            @Parameter(description = "Identificativo univoco della sottomissione, copiato senza normalizzazione; distinto dal documento.",
                    example = "2.16.840.1.113883.2.9.2.950.4.4.202611080002") @RequestParam String submissionUniqueId,
            @Parameter(description = "Selezione codice - nome dell'attività clinica.", example = "CON - Consulto") @RequestParam String contentTypeCode,
            @Parameter(description = "Data sottomissione yyyyMMddHHmmss; opzionale. Se vuota o assente, generata al momento della costruzione nel fuso del server.",
                    example = "20261108110000") @RequestParam(required = false) String submissionTime,
            @Parameter(description = "true (predefinito): anteprima senza chiamate INI. "
                    + "false: invio effettivo a INI.", example = "true") @RequestParam(defaultValue = "true") boolean dryRun) {
        // Swagger/form clients may submit empty optional controls instead of omitting them.
        title = optionalText(title);
        serviceStartTime = optionalText(serviceStartTime);
        serviceStopTime = optionalText(serviceStopTime);
        submissionTime = optionalText(submissionTime);
        conservazioneANorma = optionalText(conservazioneANorma);
        descriptions = optionalValues(descriptions);
        referenceIds = optionalValues(referenceIds);
        // Labels can contain commas. Use repeated raw form values, not Spring's comma-split List binding.
        administrativeRequests = java.util.Arrays.asList(httpRequest.getParameterValues("administrativeRequests"));
        eventCodes = java.util.Arrays.asList(httpRequest.getParameterValues("eventCodes"));
        for (var selection : List.of(administrativeRequests, eventCodes)) {
            if (selection.isEmpty() || selection.size() > 100 || selection.stream().anyMatch(java.util.Objects::isNull)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid selection list");
            }
        }
        var listParameters = java.util.Set.of("administrativeRequests", "eventCodes", "descriptions", "referenceIds");
        httpRequest.getParameterMap().forEach((name, values) -> {
            if (values.length != 1 && !listParameters.contains(name)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Repeated single-valued parameter");
            }
        });
        try {
            var selectedOrganization = GuidedChoices.resolve("organization", organization);
            var input = new GuidedPublicationRequest(patientId, subjectId, GuidedChoices.resolve("role", role).value(),
                    selectedOrganization.value(), selectedOrganization.displayName(),
                    locality, GuidedChoices.resolve("purposeOfUse", purposeOfUse).value(), patientConsent,
                    resourceHl7Type, applicationId, applicationVendor, applicationVersion,
                    title, mimeType, repositoryUniqueId, patientId, patientId,
                    documentSubjectApplication, documentUniqueId, hash, size, creationTime, serviceStartTime,
                    serviceStopTime, conservazioneANorma, descriptions,
                    administrativeRequests.stream().map(value -> GuidedChoices.resolve("administrativeRequests", value))
                            .map(value -> value.value() + "^" + value.displayName()).toList(),
                    referenceIds, GuidedChoices.resolve("classCode", classCode),
                    GuidedChoices.resolve("confidentialityCode", confidentialityCode),
                    GuidedChoices.resolve("formatCode", formatCode),
                    GuidedChoices.resolve("healthcareFacilityTypeCode", healthcareFacilityTypeCode),
                    GuidedChoices.resolve("practiceSettingCode", practiceSettingCode),
                    GuidedChoices.resolve("typeCode", typeCode),
                    eventCodes.stream().map(value -> GuidedChoices.resolve("eventCodes", value)).toList(),
                    authorPerson, GuidedChoices.resolve("documentAuthorRole", documentAuthorRole).value(),
                    documentAuthorInstitution, submissionSourceId, patientId, submissionAuthorPerson,
                    GuidedChoices.resolve("submissionAuthorRole", submissionAuthorRole).value(),
                    submissionAuthorInstitution, submissionUniqueId, GuidedChoices.resolve("contentTypeCode", contentTypeCode), submissionTime);
            GuidedInputValidation.validate(input);
            return prepare(input, dryRun);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    private static String optionalText(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private static List<String> optionalValues(List<String> values) {
        if (values == null) return null;
        var supplied = values.stream().filter(value -> optionalText(value) != null).toList();
        return supplied.isEmpty() ? null : supplied;
    }

    private ResponseEntity<?> prepare(GuidedPublicationRequest input, boolean dryRun) {
        try {
            ForgedIti42Request request = forge.forge(input);

            if (dryRun) {
                // Preview never invokes transport, so run the handler's shared checks explicitly.
                shield.validate(request.message().getSOAPPart());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                request.message().writeTo(bytes);
                return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                        .contentType(MediaType.parseMediaType("application/soap+xml; charset=UTF-8"))
                        .body(bytes.toByteArray());
            }

            return send(request);
        } catch (ShieldException exception) {
            throw shieldRejection(exception);
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            // Do not expose credential paths, passwords, or request values in error messages.
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "ITI-42 preparation failed; no ITI-42 registration was transmitted");
        }
    }

    private ResponseEntity<PublicationResponse> send(ForgedIti42Request request) {
        Dispatch<SOAPMessage> dispatch = null;
        boolean transmissionStarted = false;

        var startingDate = new Date();
        var wii = "guided_" + UUID.randomUUID().toString();

        try {
            // No WSDL fetch, typed-body reconstruction, or WS-Addressing feature:
            // the Forge already supplied the complete envelope and its routing headers.
            QName serviceName = new QName("urn:ihe:iti:xds-b:2007", "GuidedDocumentRegistryService");
            QName portName = new QName("urn:ihe:iti:xds-b:2007", "DocumentRegistry_Port_Soap12");
            Service service = Service.create(serviceName);
            service.addPort(portName, SOAPBinding.SOAP12HTTP_BINDING, request.destination().toString());

            dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
            dispatch.getBinding().setHandlerChain(List.of(new SAMLShieldHandler(shield),
                        new SOAPLoggingHandler(auditIniSrv, configSRV)));

            var context = dispatch.getRequestContext();

            // Insert loggins informationg
            context.put(WII, wii);
            context.put(EVENT_TYPE, INI_CREATE_SOAP);
            context.put(EVENT_DATE, startingDate);

            context.put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
            context.put(BindingProvider.SOAPACTION_URI_PROPERTY, request.action());
            context.put(JAXWSProperties.CONNECT_TIMEOUT, 10000);
            context.put(JAXWSProperties.REQUEST_TIMEOUT, 30000);

            // Streaming mode prevents HttpURLConnection from automatically replaying
            // the signed POST for a redirect or an authentication challenge.
            context.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 8192);
            if ("https".equalsIgnoreCase(request.destination().getScheme())) {
                context.put(JAXWSProperties.SSL_SOCKET_FACTORY,
                        security.createSslCustomContext().getSocketFactory());
            }

            if (govway.getGovwayUser() != null && !govway.getGovwayUser().isBlank()
                    && govway.getGovwayPass() != null && !govway.getGovwayPass().isBlank()) {
                String credentials = govway.getGovwayUser() + ":" + govway.getGovwayPass();
                context.put(MessageContext.HTTP_REQUEST_HEADERS, Map.of("Authorization", List.of(
                        "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8)))));
            }

            // Once invoked, delivery is conservatively uncertain unless Shield rejected
            // the request in its outbound handler before transport could run.
            transmissionStarted = true;
            SOAPMessage reply;
            try {
                reply = dispatch.invoke(request.message());
            } catch (SOAPFaultException exception) {
                reply = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
                reply.getSOAPBody().appendChild(reply.getSOAPPart().importNode(exception.getFault(), true));
            }

            Integer status = (Integer) dispatch.getResponseContext().get(MessageContext.HTTP_RESPONSE_CODE);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            reply.writeTo(bytes);
            if (bytes.size() > MAX_RESPONSE_BYTES) {
                throw new IllegalStateException("INI response exceeds limit");
            }

            String outcome;
            HttpStatus responseStatus;

            // A SOAP fault and a registry-level rejection are different responses.
            if (reply.getSOAPBody().hasFault()) {
                outcome = "SOAP_FAULT";
                responseStatus = HttpStatus.BAD_GATEWAY;
            } else {
                var responses = reply.getSOAPBody().getElementsByTagNameNS(
                        "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "RegistryResponse");
                if (responses.getLength() != 1) {
                    throw new IllegalStateException("Missing RegistryResponse");
                }

                String registryStatus = ((Element) responses.item(0)).getAttribute("status");
                boolean success = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success".equals(registryStatus);
                outcome = success && status != null && status >= 200 && status < 300 ? "INI_SUCCESS" : "INI_REJECTED";
                responseStatus = "INI_SUCCESS".equals(outcome) ? HttpStatus.OK : HttpStatus.BAD_GATEWAY;
            }

            return response(responseStatus, new PublicationResponse(false, true, outcome, wii,
                    request.destination().toString(), request.messageId(), request.assertionId(),
                    status, bytes.toString(StandardCharsets.UTF_8)));

        } catch (Exception exception) {
            // JAX-WS may wrap an exception thrown by a SOAP handler.
            for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
                if (cause instanceof ShieldException rejection) {
                    throw shieldRejection(rejection);
                }
            }
            return response(HttpStatus.BAD_GATEWAY, new PublicationResponse(false, transmissionStarted,
                    transmissionStarted ? "TRANSPORT_OR_RESPONSE_ERROR_DELIVERY_UNKNOWN" : "TRANSPORT_SETUP_ERROR",
                    wii, request.destination().toString(), request.messageId(), request.assertionId(),
                    null, null));
        } finally {
            if (dispatch instanceof java.io.Closeable closeable) {
                try {
                    closeable.close();
                } catch (java.io.IOException ignored) {
                    // Cleanup must not replace the registration outcome.
                }
            }
        }
    }

    private ResponseStatusException shieldRejection(ShieldException exception) {
        // The detailed Shield message can contain patient IDs; expose only the rule ID.
        return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                "Shield rejected ITI-42 request (" + exception.getFault().name()
                        + "); no ITI-42 registration was transmitted");
    }

    private ResponseEntity<PublicationResponse> response(HttpStatus status, PublicationResponse body) {
        return ResponseEntity.status(status).cacheControl(CacheControl.noStore())
                .contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleStatus(ResponseStatusException exception) {
        var problem = ProblemDetail.forStatusAndDetail(
                exception.getStatusCode(), exception.getReason() == null ? "Request rejected" : exception.getReason());
        return ResponseEntity.status(exception.getStatusCode()).cacheControl(CacheControl.noStore()).body(problem);
    }

    @ExceptionHandler({org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class})
    public ResponseEntity<ProblemDetail> handleInvalidParameter(Exception exception) {
        // Framework conversion messages can include the rejected value (including patient data).
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Missing or invalid guided request parameter; no ITI-42 registration was transmitted");
        return ResponseEntity.badRequest().cacheControl(CacheControl.noStore()).body(problem);
    }

    @Schema(description = "Risultato della costruzione o dell'invio. I campi null non sono inclusi nel JSON.")
    public record PublicationResponse(
            @Schema(description = "Indica se è stata richiesta la sola anteprima.") boolean dryRun,
            @Schema(description = "Indica un tentativo di trasmissione, non la conferma di pubblicazione.") boolean sent,
            @Schema(description = "Esito locale o del registro.", example = "PREVIEW") String outcome,
            @Schema(description = "Workflow Instance Id della transazione", example = "guided_2b101945-cfcc-4095-bea7-ff88f181f415") String workflowInstanceId,
            @Schema(description = "Destinazione configurata sul server.") String destination,
            @Schema(description = "MessageID WS-Addressing generato per questa costruzione.") String messageId,
            @Schema(description = "ID SAML generato per questa asserzione.") String assertionId,
            @Schema(description = "Stato HTTP restituito da INI, quando disponibile.") Integer upstreamHttpStatus,
            @Schema(description = "XML della risposta INI serializzato da JAX-WS, quando disponibile; "
                    + "per SOAP Fault può essere ricostruito dall'eccezione. Può contenere dati sensibili.") String upstreamSoapXml) {
    }
}
