/* SPDX-License-Identifier: AGPL-3.0-or-later */
package it.finanze.sanita.fse2.ms.iniclient;

import java.util.Properties;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/** A fresh, complete form for every test; JSON remains only an internal Forge parity fixture. */
final class GuidedTestParameters {
    private GuidedTestParameters() {}

    static MultiValueMap<String, String> valid() {
        var properties = new Properties();
        try (var input = GuidedTestParameters.class.getResourceAsStream("/Files/guided-create-valid.properties")) {
            properties.load(input);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException(exception);
        }
        var parameters = new LinkedMultiValueMap<String, String>();
        properties.forEach((key, value) -> parameters.add(key.toString(), value.toString()));
        return parameters;
    }
}
