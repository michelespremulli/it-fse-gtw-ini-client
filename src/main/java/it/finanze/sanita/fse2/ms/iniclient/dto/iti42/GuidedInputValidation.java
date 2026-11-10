/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.dto.iti42;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;

/** Guided API boundary checks; not official INI faults or a complete affinity-domain validator. */
public final class GuidedInputValidation {
    private static final String OID = "[0-2](?:\\.(?:0|[1-9][0-9]*))+";
    private static final String CX = "[^\\s^&]+\\^\\^\\^&" + OID + "&ISO";
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("uuuuMMddHHmmss")
            .withResolverStyle(ResolverStyle.STRICT);

    private GuidedInputValidation() {}

    public static void validate(GuidedPublicationRequest input) {
        // Bound caller-controlled free text and reject characters XML 1.0 cannot represent.
        text(input.patientId(), "patientId");
        text(input.subjectId(), "subjectId");
        text(input.documentPatientId(), "documentPatientId");
        text(input.sourcePatientId(), "sourcePatientId");
        text(input.submissionPatientId(), "submissionPatientId");
        text(input.locality(), "locality");
        text(input.resourceHl7Type(), "resourceHl7Type");
        text(input.applicationId(), "applicationId");
        text(input.applicationVendor(), "applicationVendor");
        text(input.applicationVersion(), "applicationVersion");
        text(input.mimeType(), "mimeType");
        text(input.repositoryUniqueId(), "repositoryUniqueId");
        text(input.documentSubjectApplication(), "documentSubjectApplication");
        text(input.documentUniqueId(), "documentUniqueId");
        text(input.submissionUniqueId(), "submissionUniqueId");
        text(input.submissionSourceId(), "submissionSourceId");
        text(input.hash(), "hash");
        text(input.creationTime(), "creationTime");
        text(input.authorPerson(), "authorPerson");
        text(input.documentAuthorInstitution(), "documentAuthorInstitution");
        text(input.submissionAuthorPerson(), "submissionAuthorPerson");
        text(input.submissionAuthorInstitution(), "submissionAuthorInstitution");
        optionalText(input.title(), "title");
        optionalText(input.conservazioneANorma(), "conservazioneANorma");
        strings(input.descriptions(), "descriptions");
        strings(input.referenceIds(), "referenceIds");
        strings(input.administrativeRequests(), "administrativeRequests");

        require(input.patientId().matches(CX), "patientId");
        require(input.subjectId().matches(CX), "subjectId");
        require(input.documentPatientId().matches(CX), "documentPatientId");
        require(input.sourcePatientId().matches(CX), "sourcePatientId");
        require(input.submissionPatientId().matches(CX), "submissionPatientId");
        // Defensive check on the internal Forge contract; the endpoint fills all four from patientId.
        require(input.patientId().equals(input.documentPatientId())
                && input.patientId().equals(input.sourcePatientId())
                && input.patientId().equals(input.submissionPatientId()), "patient identifiers");
        require(!"TREATMENT".equals(input.purposeOfUse()) || Boolean.TRUE.equals(input.patientConsent()), "patientConsent");
        require(input.hash().matches("[0-9a-fA-F]{40}"), "hash");
        require(input.size() != null && input.size() >= 0, "size");
        // Repository identifiers in the workbook retain zero-padded regional segments (.080.).
        require(input.repositoryUniqueId().matches("[0-2](?:\\.[0-9]+)+"), "repositoryUniqueId");
        require(input.submissionSourceId().matches(OID), "submissionSourceId");
        require(input.resourceHl7Type().matches("\\('(?:[0-9]+-[0-9]+\\^\\^2\\.16\\.840\\.1\\.113883\\.6\\.1)'(?:,'[0-9]+-[0-9]+\\^\\^2\\.16\\.840\\.1\\.113883\\.6\\.1')*\\)"), "resourceHl7Type");
        require(input.documentSubjectApplication().matches("[^\\^]+\\^[^\\^]+\\^[^\\^]+"), "documentSubjectApplication");
        require(input.eventCodes() != null && !input.eventCodes().isEmpty(), "eventCodes");
        require(input.administrativeRequests() != null && !input.administrativeRequests().isEmpty(), "administrativeRequests");

        timestamp(input.creationTime(), "creationTime");
        if (input.submissionTime() != null) timestamp(input.submissionTime(), "submissionTime");
        if (input.serviceStartTime() != null) timestamp(input.serviceStartTime(), "serviceStartTime");
        if (input.serviceStopTime() != null) timestamp(input.serviceStopTime(), "serviceStopTime");
        if (input.serviceStartTime() != null && input.serviceStopTime() != null) {
            require(input.serviceStartTime().compareTo(input.serviceStopTime()) <= 0, "service time range");
        }
    }

    private static void timestamp(String value, String field) {
        text(value, field);
        require(value.matches("[0-9]{14}"), field);
        try {
            LocalDateTime.parse(value, TIME);
        } catch (java.time.DateTimeException exception) {
            throw new IllegalArgumentException("Invalid parameter: " + field);
        }
    }

    private static void text(String value, String field) {
        require(value != null && !value.isBlank() && value.length() <= 1000, field);
        require(value.codePoints().allMatch(c -> c == 9 || c == 10 || c == 13
                || c >= 0x20 && c <= 0xD7FF || c >= 0xE000 && c <= 0xFFFD
                || c >= 0x10000 && c <= 0x10FFFF), field);
    }

    private static void optionalText(String value, String field) {
        if (value != null) text(value, field);
    }

    private static void strings(List<String> values, String field) {
        if (values != null) {
            require(values.size() <= 100, field);
            values.forEach(value -> text(value, field));
        }
    }

    private static void require(boolean valid, String field) {
        if (!valid) throw new IllegalArgumentException("Invalid parameter: " + field);
    }
}
