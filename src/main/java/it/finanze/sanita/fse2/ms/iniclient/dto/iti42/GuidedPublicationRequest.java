/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.dto.iti42;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Proprietà JSON allineate al SOAP: URN degli attributi SAML e nomi XDS qualificati. "
        + "I punti nei nomi sono letterali, non oggetti annidati. Ogni dato condiviso compare una sola volta. "
        + "Costanti e ID tecnici sono interni; nessuna validazione di conformità INI.")
public record GuidedPublicationRequest(
        @JsonProperty("urn:oasis:names:tc:xacml:1.0:resource:resource-id")
        @Schema(description = "Paziente completo: alimenta anche DocumentEntry.patientId, SubmissionSet.patientId e sourcePatientId.")
        String patientId,
        @JsonProperty("urn:oasis:names:tc:xacml:1.0:subject:subject-id")
        @Schema(description = "Operatore completo: alimenta anche SAML Subject.NameID, senza riscritture.") String subjectId,
        @JsonProperty("urn:oasis:names:tc:xacml:2.0:subject:role")
        @Schema(description = "Ruolo SAML, riutilizzato nei due authorRole XDS.") String role,
        @JsonProperty("urn:oasis:names:tc:xspa:1.0:subject:organization-id")
        @Schema(description = "Organizzazione: alimenta anche Issuer e il sourceId derivato della sottomissione.", example = "950")
        String organizationId,
        @JsonProperty("urn:oasis:names:tc:xspa:1.0:subject:organization")
        @Schema(description = "Denominazione dell'organizzazione.") String organization,
        @JsonProperty("urn:oasis:names:tc:xspa:1.0:environment:locality")
        @Schema(description = "Località SAML, riutilizzata nei due authorInstitution XDS.") String locality,
        @JsonProperty("urn:oasis:names:tc:xspa:1.0:subject:purposeofuse")
        @Schema(description = "Finalità dichiarata.", example = "TREATMENT") String purposeOfUse,
        @JsonProperty("urn:oasis:names:tc:xspa:1.0:resource:patient:consent")
        @Schema(description = "null omette l'attributo; false emette false.") Boolean patientConsent,
        @JsonProperty("urn:oasis:names:tc:xspa:1.0:resource:hl7:type")
        @Schema(description = "Espressione HL7 completa, non derivata dal solo typeCode.") String resourceHl7Type,
        @JsonProperty("SubjectApplicationId")
        @Schema(description = "Applicazione: alimenta anche lo slot XDS SubjectApplication.") String applicationId,
        @JsonProperty("SubjectApplicationVendor")
        @Schema(description = "Produttore applicazione; condiviso con lo slot XDS.") String applicationVendor,
        @JsonProperty("SubjectApplicationVersion")
        @Schema(description = "Versione applicazione; condivisa con lo slot XDS.") String applicationVersion,

        @JsonProperty("XDSDocumentEntry.title")
        @Schema(description = "Titolo opzionale, serializzato come rim:Name.") String title,
        @JsonProperty("XDSDocumentEntry.repositoryUniqueId")
        @Schema(description = "Valore dello slot repositoryUniqueId.") String repositoryUniqueId,
        @JsonProperty("XDSDocumentEntry.uniqueId")
        @Schema(description = "Identificativo esterno del documento; distinto dal uniqueId della sottomissione.") String documentUniqueId,
        @JsonProperty("XDSDocumentEntry.hash")
        @Schema(description = "Hash fornito del documento; non calcolato.") String hash,
        @JsonProperty("XDSDocumentEntry.size")
        @Schema(description = "Dimensione documento in byte, non dimensione SOAP.") Long size,
        @JsonProperty("XDSDocumentEntry.creationTime")
        @Schema(description = "Data XDS copiata come stringa.", example = "20261105120000") String creationTime,
        @JsonProperty("XDSDocumentEntry.serviceStartTime")
        @Schema(description = "Data inizio prestazione opzionale.") String serviceStartTime,
        @JsonProperty("XDSDocumentEntry.serviceStopTime")
        @Schema(description = "Data fine prestazione opzionale.") String serviceStopTime,
        @JsonProperty("urn:ita:2017:repository-type")
        @Schema(description = "Slot repository-type, ossia conservazioneANorma nel servizio esistente.") String conservazioneANorma,
        @JsonProperty("urn:ita:2022:description")
        @Schema(description = "Valori dello slot description.") List<String> descriptions,
        @JsonProperty("urn:ita:2022:administrativeRequest")
        @Schema(description = "Valori completi dello slot, copiati senza lookup o trasformazioni.", example = "[\"SSN^Regime SSN\"]")
        List<String> administrativeRequests,
        @JsonProperty("urn:ihe:iti:xds:2013:referenceIdList")
        @Schema(description = "Valori dello slot referenceIdList nel formato XDS.") List<String> referenceIds,
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
                + "Esempio illustrativo, da adattare al documento.",
                example = "[{\"nodeRepresentation\":\"J07AC\",\"rim:Name\":\"Anthrax vaccines\"}]") List<Code> eventCodes,
        @JsonProperty("XDSDocumentEntry.author.authorPerson")
        @Schema(description = "Autore clinico completo XDS, riutilizzato nella sottomissione; distinto dall'operatore.") String authorPerson,
        @JsonProperty("XDSSubmissionSet.uniqueId")
        @Schema(description = "Identificativo esterno della sottomissione.") String submissionUniqueId,
        @JsonProperty("XDSSubmissionSet.contentTypeCode")
        @Schema(description = "Codice e nome espliciti dell'attività clinica; nessun lookup da enum.",
                example = "{\"nodeRepresentation\":\"CON\",\"rim:Name\":\"Consulto\"}") Code contentTypeCode) {

    public record Code(
            @JsonProperty("nodeRepresentation")
            @Schema(description = "Codice della classificazione, senza lookup o validazione.", example = "REF") String value,
            @JsonProperty("rim:Name")
            @Schema(description = "Nome leggibile, serializzato come Name/LocalizedString.", example = "Referto") String displayName) {}
}
