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
package it.finanze.sanita.fse2.ms.iniclient.config;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI configuration class
 */
@Configuration
public class OpenApiCFG {

    /** Springdoc represents form fields as request-body properties, not operation parameters. */
    @Bean
    public OpenApiCustomizer guidedFormExamples() {
        return openApi -> {
            if (openApi.getPaths() == null) return;
            var path = openApi.getPaths().get("/internal/iti-42/guided");
            if (path == null || path.getPost() == null) return;
            var body = path.getPost().getRequestBody();
            if (body == null || body.getContent() == null) return;
            var media = body.getContent().get("application/x-www-form-urlencoded");
            if (media == null || media.getSchema() == null) return;
            var form = media.getSchema();
            if (form.get$ref() != null && openApi.getComponents() != null) {
                String name = form.get$ref().substring(form.get$ref().lastIndexOf('/') + 1);
                form = openApi.getComponents().getSchemas().get(name);
            }
            if (form == null) return;

            var method = java.util.Arrays.stream(
                    it.finanze.sanita.fse2.ms.iniclient.controller.impl.Iti42GuidedInjector.class.getDeclaredMethods())
                    .filter(candidate -> candidate.getName().equals("publish")).findFirst().orElseThrow();
            for (var field : method.getParameters()) {
                var request = field.getAnnotation(org.springframework.web.bind.annotation.RequestParam.class);
                var documentation = field.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
                if (request == null || documentation == null) continue;
                String name = request.name().isEmpty() ? field.getName() : request.name();
                boolean list = java.util.List.class.isAssignableFrom(field.getType());
                boolean optional = !request.required();
                var property = form.getProperties() == null ? null
                        : (io.swagger.v3.oas.models.media.Schema<?>) form.getProperties().get(name);
                if (property == null) {
                    if (list) property = new io.swagger.v3.oas.models.media.ArraySchema()
                            .items(new io.swagger.v3.oas.models.media.StringSchema());
                    else if (field.getType() == Boolean.class || field.getType() == boolean.class)
                        property = new io.swagger.v3.oas.models.media.BooleanSchema();
                    else if (field.getType() == Long.class)
                        property = new io.swagger.v3.oas.models.media.IntegerSchema().format("int64");
                    else property = new io.swagger.v3.oas.models.media.StringSchema();
                    form.addProperty(name, property);
                }
                Object example;
                try {
                    if (optional) example = list ? java.util.List.of() : "";
                    else if (list) example = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(documentation.example(), java.util.List.class);
                    else if (field.getType() == Boolean.class || field.getType() == boolean.class)
                        example = Boolean.valueOf(documentation.example());
                    else if (field.getType() == Long.class) example = Long.valueOf(documentation.example());
                    else example = documentation.example();
                } catch (com.fasterxml.jackson.core.JsonProcessingException exception) {
                    throw new IllegalStateException("Invalid guided documentation example: " + name, exception);
                }
                property.setDescription(documentation.description() + (optional
                        ? " Esempio di formato: " + documentation.example() + ". Vuoto: omesso dal SOAP." : ""));
                property.setExample(example);
                // Swagger UI form controls use schema defaults, not always schema examples.
                // These are editable UI prefills only: @RequestParam has no business-data default.
                setDocumentationDefault(property, example);
                var allowed = it.finanze.sanita.fse2.ms.iniclient.enums.guided.GuidedChoices.allowed(name);
                if (!allowed.isEmpty()) {
                    var selection = new io.swagger.v3.oas.models.media.StringSchema();
                    selection.setEnum(allowed);
                    if (list) {
                        property.setItems(selection);
                        media.addEncoding(name, new io.swagger.v3.oas.models.media.Encoding()
                                .style(io.swagger.v3.oas.models.media.Encoding.StyleEnum.FORM).explode(true));
                    } else {
                        setDocumentationEnum(property, allowed);
                    }
                }
            }
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setDocumentationDefault(io.swagger.v3.oas.models.media.Schema schema, Object value) {
        schema.setDefault(value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setDocumentationEnum(io.swagger.v3.oas.models.media.Schema schema, List<String> values) {
        schema.setEnum(values);
    }

    @Bean
    public OpenApiCustomizer readableGuidedChoices() {
        return openApi -> {
            if (openApi.getPaths() == null) return;
            var path = openApi.getPaths().get("/internal/iti-42/guided");
            if (path == null || path.getPost() == null || path.getPost().getParameters() == null) return;
            for (var parameter : path.getPost().getParameters()) {
                var allowed = it.finanze.sanita.fse2.ms.iniclient.enums.guided.GuidedChoices.allowed(parameter.getName());
                if (allowed.isEmpty()) continue;
                var selection = new io.swagger.v3.oas.models.media.StringSchema();
                selection.setEnum(allowed);
                if (java.util.Set.of("eventCodes", "administrativeRequests").contains(parameter.getName())) {
                    parameter.setSchema(new io.swagger.v3.oas.models.media.ArraySchema().items(selection));
                    parameter.setExplode(true);
                } else {
                    parameter.setSchema(selection);
                }
            }
        };
    }

    @Bean
    public OpenApiCustomizer emptyGuidedOptionalParameters() {
        return openApi -> {
            if (openApi.getPaths() == null) return;
            var path = openApi.getPaths().get("/internal/iti-42/guided");
            if (path == null || path.getPost() == null || path.getPost().getParameters() == null) return;
            // Explicit model examples suppress Swagger UI's generated "string" placeholder.
            // Keep the useful nonempty examples in the descriptions, not prefilled controls.
            for (var parameter : path.getPost().getParameters()) {
                if (!java.util.Set.of("title", "serviceStartTime", "serviceStopTime", "submissionTime", "conservazioneANorma",
                        "descriptions", "referenceIds").contains(parameter.getName())) continue;
                Object empty = parameter.getSchema() instanceof io.swagger.v3.oas.models.media.ArraySchema
                        ? java.util.List.of() : "";
                parameter.setDescription(parameter.getDescription() + " Esempio di formato: "
                        + parameter.getExample() + ". Predefinito vuoto: nessun valore SOAP.");
                parameter.setExample(empty);
                if (parameter.getSchema() != null) {
                    parameter.getSchema().setExample(empty);
                }
            }
        };
    }

    @Autowired
	private CustomSwaggerCFG customOpenapi;

    @Bean
    public OpenApiCustomizer disableAdditionalRequestProperties() {

        final List<String> required = new ArrayList<>();
		required.add("file");
        required.add("requestBody");

        return openApi -> {

            // Populating info section.
            openApi.getInfo().setTitle(customOpenapi.getTitle());
            openApi.getInfo().setVersion(customOpenapi.getVersion());
            openApi.getInfo().setDescription(customOpenapi.getDescription());
            openApi.getInfo().setTermsOfService(customOpenapi.getTermsOfService());

            // Adding contact to info section
            final Contact contact = new Contact();
            contact.setName(customOpenapi.getContactName());
            contact.setUrl(customOpenapi.getContactUrl());
            contact.setEmail(customOpenapi.getContactMail());
            openApi.getInfo().setContact(contact);

            // Adding extensions
            openApi.getInfo().addExtension("x-api-id", customOpenapi.getApiId());
            openApi.getInfo().addExtension("x-summary", customOpenapi.getApiSummary());

            for (final Server server : openApi.getServers()) {
                final Pattern pattern = Pattern.compile("^https://.*");
                if (!pattern.matcher(server.getUrl()).matches()) {
                    server.addExtension("x-sandbox", true);
                }
            }

            openApi.getComponents().getSchemas().values().forEach(schema -> {
				schema.setAdditionalProperties(false);
			});
 
        };
    }

    /**
     * Disable additional properties on every response object
     * @return The {@link OpenApiCustomiser} instance
     */
    @Bean
    public OpenApiCustomizer disableAdditionalResponseProperties() {
        return openApi -> openApi.getComponents().
            getSchemas().
            values().
            forEach( s -> s.setAdditionalProperties(false));
    }

}
