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
import java.util.List;
import java.util.Map;

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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.ShieldException;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.ShieldAssertionEngine;
import it.finanze.sanita.fse2.ms.iniclient.dto.iti42.ForgedIti42Request;
import it.finanze.sanita.fse2.ms.iniclient.dto.iti42.GuidedPublicationRequest;
import it.finanze.sanita.fse2.ms.iniclient.service.ISecuritySRV;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.Iti42SoapForge;

/** Internal ITI-42 endpoint; never uses workflow or mock orchestration. */
@RestController
@ConditionalOnProperty(name = "ini.guided.enabled", havingValue = "true")
@RequestMapping("/internal/iti-42")
@Tag(name = "Operazioni guidate ITI-42", description = "Solo CREATE, senza JWT o orchestrazione del workflow.")
public class Iti42GuidedInjector {

    private static final int MAX_RESPONSE_BYTES = 2 * 1024 * 1024;

    private final Iti42SoapForge forge;
    private final ISecuritySRV security;
    private final GovwayCfg govway;
    private final ShieldAssertionEngine shield;

    public Iti42GuidedInjector(Iti42SoapForge forge, ISecuritySRV security, GovwayCfg govway,
            ShieldAssertionEngine shield) {
        this.forge = forge;
        this.security = security;
        this.govway = govway;
        this.shield = shield;
    }

    @PostMapping(value = "/guided", consumes = "application/json")
    @Operation(summary = "Costruisce SOAP ITI-42 CREATE con SAML firmato", description = """
            Con dryRun=true restituisce direttamente i byte XML (application/soap+xml), senza involucro JSON,
            escape JSON o riformattazione. Con dryRun=false restituisce l'esito JSON e invia il SOAP tramite JAX-WS
            alla destinazione configurata sul server. Gli ID tecnici e IssueInstant sono generati;
            Supporta solo CREATE, con RegisterDocumentSet-b e la destinazione registry v1.
            L'action-id SAML è sempre CREATE. Non esegue letture INI.
            SubmissionSetStatus=Original è costante.
            Parametri piatti orientati alle destinazioni SOAP, senza gruppi CSV/JWT.
            Pazienti, autori, applicazioni, SourceId e MIME sono espliciti e indipendenti.
            submissionTime è generato a runtime per tutte le operazioni, nel formato yyyyMMddHHmmss e fuso orario del server.
            Tempi SAML, contesto X509 e autenticatore seguono i builder esistenti.
            Shield applica controlli parziali, non una verifica completa di conformità INI.
            Forge completa il SOAP. In anteprima Shield ispeziona il messaggio prima della serializzazione;
            nell'invio lo stesso messaggio passa al Dispatch JAX-WS e al SOAP handler Shield, senza ricostruirlo.
            JAX-WS gestisce la serializzazione di trasporto; l'anteprima non garantisce identità byte-per-byte con l'invio.
            Non verifica localmente la firma dopo la serializzazione.
            Non sono previsti controlli di ruolo o un gate separato per l'invio: limitare l'accesso esternamente.
            L'XML contiene dati sensibili e un'asserzione firmata.
            Ogni invocazione costruisce un nuovo SOAP completo: ripetere il JSON genera nuovi ID, tempi e firma.
            """)
    @ApiResponse(responseCode = "200", description = "dryRun=true: XML SOAP diretto; dryRun=false: esito JSON.",
            content = {
                    @Content(mediaType = "application/soap+xml", schema = @Schema(type = "string")),
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PublicationResponse.class))
            })
    @ApiResponse(responseCode = "400", description = "Richiesta JSON o parametri non validi.")
    @ApiResponse(responseCode = "422", description = "Richiesta rifiutata da Shield prima della trasmissione.")
    @ApiResponse(responseCode = "500", description = "Costruzione o firma fallita; nessuna registrazione ITI-42 trasmessa.")
    @ApiResponse(responseCode = "502", description = "Errore di trasporto, SOAP Fault o rifiuto del registro. "
            + "Dopo un tentativo di trasmissione la consegna può essere incerta: non ripetere automaticamente.")
    public ResponseEntity<?> publish(
            @RequestBody GuidedPublicationRequest input,
            @Parameter(description = "true (predefinito): anteprima senza chiamate INI. "
                    + "false: invio effettivo a INI.") @RequestParam(defaultValue = "true") boolean dryRun) {
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

        try {
            // No WSDL fetch, typed-body reconstruction, or WS-Addressing feature:
            // the Forge already supplied the complete envelope and its routing headers.
            QName serviceName = new QName("urn:ihe:iti:xds-b:2007", "GuidedDocumentRegistryService");
            QName portName = new QName("urn:ihe:iti:xds-b:2007", "DocumentRegistry_Port_Soap12");
            Service service = Service.create(serviceName);
            service.addPort(portName, SOAPBinding.SOAP12HTTP_BINDING, request.destination().toString());
            dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
            dispatch.getBinding().setHandlerChain(List.of(new SAMLShieldHandler(shield)));

            var context = dispatch.getRequestContext();
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

            return response(responseStatus, new PublicationResponse(false, true, outcome,
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
                    request.destination().toString(), request.messageId(), request.assertionId(),
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

    @Schema(description = "Risultato della costruzione o dell'invio. I campi null non sono inclusi nel JSON.")
    public record PublicationResponse(
            @Schema(description = "Indica se è stata richiesta la sola anteprima.") boolean dryRun,
            @Schema(description = "Indica un tentativo di trasmissione, non la conferma di pubblicazione.") boolean sent,
            @Schema(description = "Esito locale o del registro.", example = "PREVIEW") String outcome,
            @Schema(description = "Destinazione configurata sul server.") String destination,
            @Schema(description = "MessageID WS-Addressing generato per questa costruzione.") String messageId,
            @Schema(description = "ID SAML generato per questa asserzione.") String assertionId,
            @Schema(description = "Stato HTTP restituito da INI, quando disponibile.") Integer upstreamHttpStatus,
            @Schema(description = "XML della risposta INI serializzato da JAX-WS, quando disponibile; "
                    + "per SOAP Fault può essere ricostruito dall'eccezione. Può contenere dati sensibili.") String upstreamSoapXml) {
    }
}
