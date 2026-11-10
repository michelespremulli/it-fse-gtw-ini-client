/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient.enums.guided;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import it.finanze.sanita.fse2.ms.iniclient.dto.iti42.GuidedPublicationRequest.Code;
import it.finanze.sanita.fse2.ms.iniclient.enums.AdministrativeReqEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.PracticeSettingCodeEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.guided.GuidedVocabulary.*;

/** One source for the exact strings accepted by the API and advertised in OpenAPI. */
public final class GuidedChoices {
    private static final Map<String, List<Code>> CHOICES = Map.ofEntries(
            Map.entry("role", choices(Role.values())),
            Map.entry("documentAuthorRole", choices(Role.values())),
            Map.entry("submissionAuthorRole", choices(Role.values())),
            Map.entry("organization", choices(Organization.values())),
            Map.entry("purposeOfUse", choices(Purpose.values())),
            Map.entry("classCode", choices(DocumentClass.values())),
            Map.entry("confidentialityCode", choices(Confidentiality.values())),
            Map.entry("formatCode", choices(Format.values())),
            Map.entry("healthcareFacilityTypeCode", choices(Facility.values())),
            Map.entry("typeCode", choices(DocumentType.values())),
            Map.entry("contentTypeCode", choices(ClinicalActivity.values())),
            Map.entry("eventCodes", Arrays.stream(GuidedEvent.values())
                    // Stable sort: preserve dispatcher order inside each group, without dropping values.
                    .sorted(Comparator.comparing(value -> value.name().startsWith("_")))
                    .map(Selection::classification).toList()),
            Map.entry("administrativeRequests", Arrays.stream(AdministrativeReqEnum.values())
                    .map(value -> new Code(value.getCode(), value.getDescription())).toList()),
            Map.entry("practiceSettingCode", Arrays.stream(PracticeSettingCodeEnum.values())
                    .map(value -> new Code(value.getCode(), value == PracticeSettingCodeEnum.AD_PSC004
                            ? "Osservazione breve intensiva (OBI) e Pronto Soccorso" : value.getDescription())).toList()));

    private GuidedChoices() {}

    private static List<Code> choices(Selection[] values) {
        return Stream.of(values).map(Selection::classification).toList();
    }

    public static List<String> allowed(String parameter) {
        return CHOICES.getOrDefault(parameter, List.of()).stream().map(GuidedChoices::label).toList();
    }

    private static String label(Code value) {
        return value.value() + " - " + value.displayName();
    }

    public static Code resolve(String parameter, String selection) {
        return CHOICES.getOrDefault(parameter, List.of()).stream()
                .filter(value -> label(value).equals(selection)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid selection: " + parameter));
    }
}
