/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.dto.iti42;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Proprietà JSON allineate al SOAP: URN degli attributi SAML e nomi XDS qualificati. "
        + "I punti nei nomi sono letterali, non oggetti annidati. I valori Header e Body sono indipendenti. "
        + "UPDATE modifica il documento INI esistente: usa solo classCode, healthcareFacilityTypeCode, practiceSettingCode, eventCodeList, "
        + "serviceStartTime, serviceStopTime, description e administrativeRequest per il documento; gli altri campi documento sono conservati. "
        + "In UPDATE sourceId e patientId della sottomissione derivano dal contesto SAML e l'autore è copiato dal documento. "
        + "Costanti e ID tecnici sono interni; nessuna validazione di conformità INI. "
        + "Gli esempi sono sintetici per dry-run, non valori predefiniti né dati qualificati per l'invio a INI.")
public record GuidedPublicationRequest(
        @JsonProperty("urn:oasis:names:tc:xacml:1.0:resource:resource-id")
        @Schema(description = "Identificativo completo della risorsa SAML; non alimenta gli identificativi paziente del Body.",
                example = "RSSMRA80A01H501U^^^&2.16.840.1.113883.2.9.4.3.2&ISO")
        String patientId,
        @JsonProperty("urn:oasis:names:tc:xacml:1.0:subject:subject-id")
        @Schema(description = "Operatore completo: alimenta anche SAML Subject.NameID, senza riscritture.",
                example = "BNCLGU75B15F205X^^^&2.16.840.1.113883.2.9.4.3.2&ISO") String subjectId,
        @JsonProperty("urn:oasis:names:tc:xacml:2.0:subject:role")
        @Schema(description = "Ruolo SAML, indipendente dagli authorRole XDS.", example = "AAS") String role,
        @JsonProperty("urn:oasis:names:tc:xspa:1.0:subject:organization-id")
        @Schema(description = "Organizzazione SAML: alimenta anche Issuer, non il sourceId della sottomissione.", example = "950")
        String organizationId,
        @JsonProperty("urn:oasis:names:tc:xspa:1.0:subject:organization")
        @Schema(description = "Denominazione dell'organizzazione.", example = "Organizzazione sanitaria dimostrativa") String organization,
        @JsonProperty("urn:oasis:names:tc:xspa:1.0:environment:locality")
        @Schema(description = "Località SAML, indipendente dagli authorInstitution XDS.",
                example = "Ospedale dimostrativo^^^^^&2.16.840.1.113883.2.9.4.1.3&ISO^^^^950001") String locality,
        @JsonProperty("urn:oasis:names:tc:xspa:1.0:subject:purposeofuse")
        @Schema(description = "Finalità dichiarata.", example = "TREATMENT") String purposeOfUse,
        @JsonProperty("urn:oasis:names:tc:xspa:1.0:resource:patient:consent")
        @Schema(description = "null omette l'attributo; false emette false.", example = "true") Boolean patientConsent,
        @JsonProperty("urn:oasis:names:tc:xspa:1.0:resource:hl7:type")
        @Schema(description = "Espressione HL7 completa, non derivata dal solo typeCode.",
                example = "('11502-2^^2.16.840.1.113883.6.1')") String resourceHl7Type,
        @JsonProperty("SubjectApplicationId")
        @Schema(description = "Identificativo applicazione SAML, indipendente dallo slot XDS SubjectApplication.", example = "ITI42-DEMO") String applicationId,
        @JsonProperty("SubjectApplicationVendor")
        @Schema(description = "Produttore applicazione SAML.", example = "Fornitore dimostrativo") String applicationVendor,
        @JsonProperty("SubjectApplicationVersion")
        @Schema(description = "Versione applicazione SAML.", example = "1.0.0") String applicationVersion,

        @JsonProperty("XDSDocumentEntry.title")
        @Schema(description = "Titolo opzionale, serializzato come rim:Name.", example = "Referto di laboratorio - esempio dry-run") String title,
        @JsonProperty("XDSDocumentEntry.mimeType")
        @Schema(description = "Tipo MIME del documento, copiato nell'attributo ExtrinsicObject.mimeType senza derivazioni o valore predefinito.",
                example = "application/pdf+text/x-cda-r2+xml") String mimeType,
        @JsonProperty("XDSDocumentEntry.repositoryUniqueId")
        @Schema(description = "Valore dello slot repositoryUniqueId.", example = "2.16.840.1.113883.2.9.2.950.4.5.1") String repositoryUniqueId,
        @JsonProperty("XDSDocumentEntry.patientId")
        @Schema(description = "Identificativo esterno paziente del documento, indipendente da SAML e SubmissionSet.",
                example = "RSSMRA80A01H501U^^^&2.16.840.1.113883.2.9.4.3.2&ISO") String documentPatientId,
        @JsonProperty("XDSDocumentEntry.sourcePatientId")
        @Schema(description = "Valore completo dello slot sourcePatientId, copiato senza derivazioni da patientId o SAML.",
                example = "RSSMRA80A01H501U^^^&2.16.840.1.113883.2.9.4.3.2&ISO") String sourcePatientId,
        @JsonProperty("urn:ihe:iti:xds:2024:SubjectApplication")
        @Schema(description = "Valore completo dello slot applicativo del documento; non composto dagli attributi SAML.",
                example = "ITI42-DOCUMENTI^Fornitore documentale^2.0.0") String documentSubjectApplication,
        @JsonProperty("XDSDocumentEntry.uniqueId")
        @Schema(description = "Identificativo esterno del documento; distinto dal uniqueId della sottomissione. "
                + "Per UPDATE è obbligatorio: identifica il documento da leggere in INI per recuperare lid e versione, anche in dry-run.",
                example = "2.16.840.1.113883.2.9.2.950.4.4.202611060001") String documentUniqueId,
        @JsonProperty("urn:ihe:iti:2007:AssociationType:RPLC.targetObject")
        @Schema(description = "Solo REPLACE: entryUUID del documento da sostituire; non il suo uniqueId. "
                + "Obbligatorio per REPLACE.", example = "urn:uuid:aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeeeeeee")
        String replacementTarget,
        @JsonProperty("XDSDocumentEntry.hash")
        @Schema(description = "Hash fornito del documento; non calcolato. Esempio SHA-1 sintetico, non associato a un documento.",
                example = "0123456789abcdef0123456789abcdef01234567") String hash,
        @JsonProperty("XDSDocumentEntry.size")
        @Schema(description = "Dimensione documento in byte, non dimensione SOAP.", example = "24576") Long size,
        @JsonProperty("XDSDocumentEntry.creationTime")
        @Schema(description = "Data XDS copiata come stringa.", example = "20261105120000") String creationTime,
        @JsonProperty("XDSDocumentEntry.serviceStartTime")
        @Schema(description = "Data inizio prestazione opzionale.", example = "20261105090000") String serviceStartTime,
        @JsonProperty("XDSDocumentEntry.serviceStopTime")
        @Schema(description = "Data fine prestazione opzionale.", example = "20261105100000") String serviceStopTime,
        @JsonProperty("urn:ita:2017:repository-type")
        @Schema(description = "Slot repository-type, ossia conservazioneANorma nel servizio esistente.", example = "CONS") String conservazioneANorma,
        @JsonProperty("urn:ita:2022:description")
        @Schema(description = "Valori dello slot description.", example = "[\"Referto sintetico per anteprima SOAP; nessun documento allegato\"]") List<String> descriptions,
        @JsonProperty("urn:ita:2022:administrativeRequest")
        @Schema(description = "Valori completi dello slot, copiati senza lookup o trasformazioni.", example = "[\"SSN^Regime SSN\"]")
        List<String> administrativeRequests,
        @JsonProperty("urn:ihe:iti:xds:2013:referenceIdList")
        @Schema(description = "Valori dello slot referenceIdList nel formato XDS.",
                example = "[\"2.16.840.1.113883.2.9.2.950.4.4.202611050001^^^^urn:ihe:iti:xdw:2013:workflowInstanceId\"]") List<String> referenceIds,
        @JsonProperty("XDSDocumentEntry.classCode")
        @Schema(description = "Codice e nome espliciti della classificazione; nessun lookup da enum.",
                example = "{\"nodeRepresentation\":\"REF\",\"rim:Name\":\"Referto\"}") Code classCode,
        @JsonProperty("XDSDocumentEntry.confidentialityCode")
        @Schema(description = "Codice e nome della classificazione riservatezza.",
                example = "{\"nodeRepresentation\":\"N\",\"rim:Name\":\"Normal\"}") Code confidentialityCode,
        @JsonProperty("XDSDocumentEntry.formatCode")
        @Schema(description = "Codice e nome della classificazione formato.",
                example = "{\"nodeRepresentation\":\"urn:ihe:iti:xds-sd:pdf:2008\",\"rim:Name\":\"PDF/A\"}") Code formatCode,
        @JsonProperty("XDSDocumentEntry.healthcareFacilityTypeCode")
        @Schema(description = "Codice e nome espliciti del tipo struttura; nessun lookup da enum.",
                example = "{\"nodeRepresentation\":\"Ospedale\",\"rim:Name\":\"Ospedale\"}") Code healthcareFacilityTypeCode,
        @JsonProperty("XDSDocumentEntry.practiceSettingCode")
        @Schema(description = "Codice e nome espliciti dell'assetto organizzativo; nessun lookup da enum.",
                example = "{\"nodeRepresentation\":\"AD_PSC001\",\"rim:Name\":\"Allergologia\"}") Code practiceSettingCode,
        @JsonProperty("XDSDocumentEntry.typeCode")
        @Schema(description = "Codice LOINC e nome del tipo documento.",
                example = "{\"nodeRepresentation\":\"11502-2\",\"rim:Name\":\"Referto di laboratorio\"}") Code typeCode,
        @JsonProperty("XDSDocumentEntry.eventCodeList")
        @Schema(description = "Codice e nome espliciti per ogni classificazione evento; nessun lookup da enum. "
                + "UPDATE deduplica per codice con HashMap: prevale l'ultimo nome fornito e l'ordine non è garantito. "
                + "Esempio illustrativo, da adattare al documento.",
                example = "[{\"nodeRepresentation\":\"J07AC\",\"rim:Name\":\"Anthrax vaccines\"}]") List<Code> eventCodes,
        @JsonProperty("XDSDocumentEntry.author.authorPerson")
        @Schema(description = "Autore clinico completo del documento; indipendente dall'operatore e dall'autore della sottomissione.",
                example = "VRDLRA80A41H501U^Verdi^Laura^^^^^^&2.16.840.1.113883.2.9.4.3.2&ISO") String authorPerson,
        @JsonProperty("XDSDocumentEntry.author.authorRole")
        @Schema(description = "Ruolo dell'autore del documento, indipendente dal ruolo SAML.", example = "APR") String documentAuthorRole,
        @JsonProperty("XDSDocumentEntry.author.authorInstitution")
        @Schema(description = "Istituzione dell'autore del documento, indipendente dalla località SAML.",
                example = "Ospedale documentale^^^^^&2.16.840.1.113883.2.9.4.1.3&ISO^^^^950001") String documentAuthorInstitution,
        @JsonProperty("XDSSubmissionSet.sourceId")
        @Schema(description = "CREATE/REPLACE: identificativo completo della sorgente. UPDATE ignora questo campo e deriva sourceId da organization-id.",
                example = "2.16.840.1.113883.2.9.2.120") String submissionSourceId,
        @JsonProperty("XDSSubmissionSet.patientId")
        @Schema(description = "CREATE/REPLACE: identificativo paziente della sottomissione. UPDATE ignora questo campo e usa resource-id SAML.",
                example = "RSSMRA80A01H501U^^^&2.16.840.1.113883.2.9.4.3.2&ISO") String submissionPatientId,
        @JsonProperty("XDSSubmissionSet.author.authorPerson")
        @Schema(description = "CREATE/REPLACE: autore della sottomissione. UPDATE ignora questo campo e copia l'autore del documento INI.",
                example = "BNCLGU75B15F205X^Bianchi^Luigi^^^^^^&2.16.840.1.113883.2.9.4.3.2&ISO") String submissionAuthorPerson,
        @JsonProperty("XDSSubmissionSet.author.authorRole")
        @Schema(description = "CREATE/REPLACE: ruolo autore sottomissione. UPDATE ignora questo campo e copia l'autore del documento INI.", example = "APR") String submissionAuthorRole,
        @JsonProperty("XDSSubmissionSet.author.authorInstitution")
        @Schema(description = "CREATE/REPLACE: istituzione autore sottomissione. UPDATE ignora questo campo e copia l'autore del documento INI.",
                example = "Ente inviante^^^^^&2.16.840.1.113883.2.9.4.1.3&ISO^^^^950002") String submissionAuthorInstitution,
        @JsonProperty("XDSSubmissionSet.uniqueId")
        @Schema(description = "Identificativo esterno della sottomissione.",
                example = "2.16.840.1.113883.2.9.2.950.4.4.202611060002") String submissionUniqueId,
        @JsonProperty("XDSSubmissionSet.contentTypeCode")
        @Schema(description = "Codice e nome espliciti dell'attività clinica; nessun lookup da enum.",
                example = "{\"nodeRepresentation\":\"CON\",\"rim:Name\":\"Consulto\"}") Code contentTypeCode) {

    public record Code(
            @JsonProperty("nodeRepresentation")
            @Schema(description = "Codice della classificazione, senza lookup o validazione.", example = "REF") String value,
            @JsonProperty("rim:Name")
            @Schema(description = "Nome leggibile, serializzato come Name/LocalizedString.", example = "Referto") String displayName) {}
}
