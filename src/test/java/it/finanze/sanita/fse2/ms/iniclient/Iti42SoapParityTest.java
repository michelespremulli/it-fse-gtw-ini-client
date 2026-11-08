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
package it.finanze.sanita.fse2.ms.iniclient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.finanze.sanita.fse2.ms.iniclient.client.IIniClient;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.*;
import it.finanze.sanita.fse2.ms.iniclient.dto.iti42.GuidedPublicationRequest;
import it.finanze.sanita.fse2.ms.iniclient.enums.*;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.Iti42SoapForge;
import it.finanze.sanita.fse2.ms.iniclient.singleton.SigningCredentialSingleton;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlHeaderBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.create.PublishReplaceBodyBuilderUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.update.UpdateBodyBuilderUtility;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;

/**
 * Offline comparison of the real legacy header/body builders with the public forge.
 * This checks SOAP payloads, not JAX-WS HTTP framing or INI conformance. The metadata
 * lookup is mocked; neither side can contact INI. Only the credential provider is
 * stubbed, using the repository's test key, without changing its singleton cache.
 */
@DisplayName("Offline SOAP parity: forge matches the legacy ITI-42 builders")
class Iti42SoapParityTest {
    private static final String SAML = "urn:oasis:names:tc:SAML:2.0:assertion";
    private static final String WSA = "http://www.w3.org/2005/08/addressing";
    private static final String RIM = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0";
    private static final String PATIENT = "RSSMRA80A01H501U^^^&2.16.840.1.113883.2.9.4.3.2&ISO";
    private static final String SUBJECT = "BNCLGU75B15F205X^^^&2.16.840.1.113883.2.9.4.3.2&ISO";
    private static final String TARGET = "urn:uuid:aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeeeeeee";
    private static final String UNIQUE_ID = "2.16.840.1.113883.2.9.950.1234";
    private static final String SUBMISSION_TIME = "20261108120000";

    private final ObjectMapper json = new ObjectMapper();
    private final IIniClient lookup = mock(IIniClient.class);
    private final IniCFG config = new IniCFG();
    private Iti42SoapForge forge;
    private SamlHeaderBuilderUtility legacyHeaders;
    private BasicX509Credential credential;

    @BeforeEach
    void setUp() throws Exception {
        config.setUrlWsdlDocumentRegistryService("https://example.invalid/registry");
        forge = new Iti42SoapForge(config, lookup);

        legacyHeaders = new SamlHeaderBuilderUtility();
        ReflectionTestUtils.setField(legacyHeaders, "iniCFG", config);

        // Use a real test key for signing, without loading production credentials.
        KeyStore store = KeyStore.getInstance("PKCS12");
        try (var stream = getClass().getResourceAsStream("/sign/firma110_IBM.p12")) {
            assertNotNull(stream);
            store.load(stream, "123456".toCharArray());
        }

        String alias = java.util.Collections.list(store.aliases()).stream()
                .filter(storeAlias -> {
                    try { return store.isKeyEntry(storeAlias); }
                    catch (Exception e) { throw new IllegalStateException(e); }
                }).findFirst().orElseThrow();

        credential = new BasicX509Credential();
        credential.setPrivateKey((PrivateKey) store.getKey(alias, "123456".toCharArray()));
        credential.setEntityCertificate((X509Certificate) store.getCertificate(alias));
    }

    @DisplayName("CREATE and REPLACE produce comparable signed SOAP without contacting INI")
    @ParameterizedTest(name = "{0}: forge matches the legacy publication builders")
    @EnumSource(value = ActionEnumType.class, names = {"CREATE", "REPLACE"})
    void publicationAndReplacementMatchLegacy(ActionEnumType operation) throws Exception {
        var input = input();
        var document = document();

        var submission = new SubmissionSetEntryDTO(SUBMISSION_TIME, "2.16.840.1.113883.2.9.950",
                "CON", "Consulto", "2.16.840.1.113883.2.9.950.5678", "Submission author", "APR",
                "Submission institution", PATIENT);

        var body = PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(document, submission,
                token(operation).getPayload(), operation == ActionEnumType.REPLACE ? TARGET : null);

        // Run both real builders with the same signing credential.
        try (var signing = mockStatic(SigningCredentialSingleton.class)) {
            signing.when(() -> SigningCredentialSingleton.getInstance(config)).thenReturn(credential);
            var legacy = legacySoap(body, operation);
            var actual = forge.forge(input, operation.name());

            assertEquals(config.getUrlWsdlDocumentRegistryService(), actual.destination().toString());
            assertComparable(legacy, actual.message());
        }

        verifyNoInteractions(lookup);
    }

    enum UpdateCase { POPULATED, DUPLICATE_EVENTS, NULL_OPTIONAL_VALUES, EMPTY_ADMINISTRATIVE_REQUESTS, NO_LID, NO_AUTHOR }

    @DisplayName("UPDATE preserves legacy merge behavior and produces comparable signed SOAP")
    @ParameterizedTest(name = "{0}: forge matches the legacy UPDATE merge")
    @EnumSource(UpdateCase.class)
    void updateMatchesLegacyMerge(UpdateCase scenario) throws Exception {
        var fields = inputFields();

        // Equivalent legacy input: enums and DTO fields instead of guided JSON fields.
        var metadata = new PublicationMetadataReqDTO();
        metadata.setTipologiaStruttura(HealthcareFacilityEnum.Ospedale);
        metadata.setTipoDocumentoLivAlto(TipoDocAltoLivEnum.REF);
        metadata.setAssettoOrganizzativo(PracticeSettingCodeEnum.AD_PSC001);
        metadata.setTipoAttivitaClinica(AttivitaClinicaEnum.CON);
        metadata.setIdentificativoSottomissione("2.16.840.1.113883.2.9.950.5678");
        metadata.setAttiCliniciRegoleAccesso(List.of("J07AC", "J07AD"));
        metadata.setDataInizioPrestazione("20261108090000");
        metadata.setDataFinePrestazione("20261108100000");
        metadata.setDescriptions(List.of("First description", "Ignored second description"));
        metadata.setAdministrativeRequest(List.of(AdministrativeReqEnum.SSN));
        fields.put("urn:ita:2022:description", metadata.getDescriptions());

        // Apply the same scenario to both input formats.
        if (scenario == UpdateCase.DUPLICATE_EVENTS) {
            metadata.setAttiCliniciRegoleAccesso(List.of("J07AC", "J07AD", "J07AC"));
            fields.put("XDSDocumentEntry.eventCodeList", List.of(code("J07AC", "Anthrax vaccines"),
                    code("J07AD", "Brucellosis vaccines"), code("J07AC", "Anthrax vaccines")));
        }

        if (scenario == UpdateCase.NULL_OPTIONAL_VALUES) {
            metadata.setDataInizioPrestazione(null);
            metadata.setDataFinePrestazione(null);
            metadata.setDescriptions(null);
            metadata.setAdministrativeRequest(null);
            for (String key : List.of("XDSDocumentEntry.serviceStartTime", "XDSDocumentEntry.serviceStopTime",
                    "urn:ita:2022:description", "urn:ita:2022:administrativeRequest")) fields.remove(key);
        }

        if (scenario == UpdateCase.EMPTY_ADMINISTRATIVE_REQUESTS) {
            metadata.setAdministrativeRequest(List.of());
            fields.put("urn:ita:2022:administrativeRequest", List.of());
        }

        // Separate object graphs: both implementations mutate the metadata they receive.
        var legacyExisting = existing(scenario);
        var forgeExisting = existing(scenario);

        var old = new RegistryObjectListType();
        old.getIdentifiable().add(new ObjectFactory().createExtrinsicObject(legacyExisting));

        // Stub only the INI lookup; the forge still performs its real merge and signing.
        var response = new AdhocQueryResponse();
        response.setRegistryObjectList(new RegistryObjectListType());
        response.getRegistryObjectList().getIdentifiable().add(new ObjectFactory().createExtrinsicObject(forgeExisting));
        when(lookup.getReferenceMetadata(eq(UNIQUE_ID), eq(SearchTypeEnum.LEAF_CLASS.getSearchKey()),
                any(JWTTokenDTO.class), eq(ActionEnumType.READ_METADATA), anyString(), any()))
                .thenReturn(response);

        String lid = legacyExisting.getLid() == null ? legacyExisting.getId() : legacyExisting.getLid();
        var body = UpdateBodyBuilderUtility.buildSubmitObjectRequest(old,
                new MergedMetadatiRequestDTO(UNIQUE_ID, token(ActionEnumType.UPDATE).getPayload(), metadata, "workflow"),
                lid, token(ActionEnumType.UPDATE), UNIQUE_ID);

        try (var signing = mockStatic(SigningCredentialSingleton.class)) {
            signing.when(() -> SigningCredentialSingleton.getInstance(config)).thenReturn(credential);
            var legacy = legacySoap(body, ActionEnumType.UPDATE);
            var actual = forge.forge(json.convertValue(fields, GuidedPublicationRequest.class), "UPDATE");

            assertComparable(legacy, actual.message());
        }

        verify(lookup).getReferenceMetadata(eq(UNIQUE_ID), eq(SearchTypeEnum.LEAF_CLASS.getSearchKey()),
                any(JWTTokenDTO.class), eq(ActionEnumType.READ_METADATA), anyString(), any());
        verifyNoMoreInteractions(lookup);
    }

    private SOAPMessage legacySoap(SubmitObjectsRequest body, ActionEnumType operation) throws Exception {
        // Assemble the real legacy builder outputs locally, without invoking its transport.
        SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        for (var header : legacyHeaders.buildHeader(token(operation), operation)) header.writeTo(message);

        JAXBContext.newInstance(SubmitObjectsRequest.class).createMarshaller().marshal(body, message.getSOAPBody());
        message.saveChanges();
        return message;
    }

    private JWTTokenDTO token(ActionEnumType operation) {
        return new JWTTokenDTO(JWTPayloadDTO.builder().sub(SUBJECT).person_id(PATIENT).subject_role("AAS")
                .subject_organization_id("950").subject_organization("Demo organization").locality("Demo locality")
                .purpose_of_use("TREATMENT").patient_consent(false).resource_hl7_type("('11502-2^^2.16.840.1.113883.6.1')")
                .subject_application_id("APP").subject_application_vendor("Vendor").subject_application_version("1.0")
                .action_id(operation.getActionId()).build());
    }

    private GuidedPublicationRequest input() { return json.convertValue(inputFields(), GuidedPublicationRequest.class); }

    private Map<String, Object> inputFields() {
        Map<String, Object> fields = new TreeMap<>();
        var payload = token(ActionEnumType.CREATE).getPayload();

        // Explicit SAML context for the guided input.
        fields.put("urn:oasis:names:tc:xacml:1.0:resource:resource-id", PATIENT);
        fields.put("urn:oasis:names:tc:xacml:1.0:subject:subject-id", SUBJECT);
        fields.put("urn:oasis:names:tc:xacml:2.0:subject:role", "AAS");
        fields.put("urn:oasis:names:tc:xspa:1.0:subject:organization-id", "950");
        fields.put("urn:oasis:names:tc:xspa:1.0:subject:organization", "Demo organization");
        fields.put("urn:oasis:names:tc:xspa:1.0:environment:locality", "Demo locality");
        fields.put("urn:oasis:names:tc:xspa:1.0:subject:purposeofuse", "TREATMENT");
        fields.put("urn:oasis:names:tc:xspa:1.0:resource:patient:consent", false);
        fields.put("urn:oasis:names:tc:xspa:1.0:resource:hl7:type", payload.getResource_hl7_type());
        fields.put("SubjectApplicationId", "APP"); fields.put("SubjectApplicationVendor", "Vendor");
        fields.put("SubjectApplicationVersion", "1.0");

        // Document metadata, including classifications and clinical author.
        fields.put("XDSDocumentEntry.title", "Synthetic parity test & metadata");
        fields.put("XDSDocumentEntry.mimeType", "application/pdf");
        fields.put("XDSDocumentEntry.repositoryUniqueId", "2.16.840.1.113883.2.9.950.1");
        fields.put("XDSDocumentEntry.patientId", PATIENT); fields.put("XDSDocumentEntry.sourcePatientId", PATIENT);
        fields.put("XDSDocumentEntry.uniqueId", UNIQUE_ID);
        fields.put("XDSDocumentEntry.hash", "0123456789abcdef0123456789abcdef01234567");
        fields.put("XDSDocumentEntry.size", 1234L); fields.put("XDSDocumentEntry.creationTime", "20261108080000");
        fields.put("XDSDocumentEntry.serviceStartTime", "20261108090000");
        fields.put("XDSDocumentEntry.serviceStopTime", "20261108100000");
        fields.put("urn:ita:2017:repository-type", "CONS");
        fields.put("urn:ita:2022:description", List.of("First description"));
        fields.put("urn:ita:2022:administrativeRequest", List.of("SSN^Regime SSN"));
        fields.put("urn:ihe:iti:xds:2013:referenceIdList", List.of("synthetic-reference"));
        fields.put("urn:ihe:iti:xds:2024:SubjectApplication", "APP^Vendor^1.0");
        fields.put("XDSDocumentEntry.classCode", code("REF", "Referto"));
        fields.put("XDSDocumentEntry.confidentialityCode", code("N", "Normal"));
        fields.put("XDSDocumentEntry.formatCode", code("urn:ihe:iti:xds-sd:pdf:2008", "PDF/A"));
        fields.put("XDSDocumentEntry.healthcareFacilityTypeCode", code("Ospedale", "Ospedale"));
        fields.put("XDSDocumentEntry.practiceSettingCode", code("AD_PSC001", "Allergologia"));
        fields.put("XDSDocumentEntry.typeCode", code("11502-2", "Laboratory report"));
        fields.put("XDSDocumentEntry.eventCodeList", List.of(code("J07AC", "Anthrax vaccines"), code("J07AD", "Brucellosis vaccines")));
        fields.put("XDSDocumentEntry.author.authorPerson", "Document author");
        fields.put("XDSDocumentEntry.author.authorRole", "APR");
        fields.put("XDSDocumentEntry.author.authorInstitution", "Document institution");

        // Submission metadata is independent for CREATE/REPLACE, derived for UPDATE.
        fields.put("XDSSubmissionSet.sourceId", "2.16.840.1.113883.2.9.950");
        fields.put("XDSSubmissionSet.uniqueId", "2.16.840.1.113883.2.9.950.5678");
        fields.put("XDSSubmissionSet.patientId", PATIENT);
        fields.put("XDSSubmissionSet.contentTypeCode", code("CON", "Consulto"));
        fields.put("XDSSubmissionSet.author.authorPerson", "Submission author");
        fields.put("XDSSubmissionSet.author.authorRole", "APR");
        fields.put("XDSSubmissionSet.author.authorInstitution", "Submission institution");

        fields.put("urn:ihe:iti:2007:AssociationType:RPLC.targetObject", TARGET);
        return fields;
    }

    private Map<String, String> code(String value, String name) { return Map.of("nodeRepresentation", value, "rim:Name", name); }

    private DocumentEntryDTO document() {
        var d = new DocumentEntryDTO();
        d.setTitle("Synthetic parity test & metadata"); d.setMimeType("application/pdf");
        d.setRepositoryUniqueId("2.16.840.1.113883.2.9.950.1"); d.setPatientId(PATIENT); d.setUniqueId(UNIQUE_ID);
        d.setHash("0123456789abcdef0123456789abcdef01234567"); d.setSize(1234);
        d.setCreationTime("20261108080000"); d.setServiceStartTime("20261108090000"); d.setServiceStopTime("20261108100000");
        d.setConservazioneANorma("CONS"); d.setDescription(List.of("First description"));
        d.setAdministrativeRequest(List.of("SSN^Regime SSN")); d.setReferenceIdList(List.of("synthetic-reference"));
        d.setClassCode("REF"); d.setClassCodeName("Referto"); d.setConfidentialityCode("N"); d.setConfidentialityCodeDisplayName("Normal");
        d.setFormatCode("urn:ihe:iti:xds-sd:pdf:2008"); d.setFormatCodeName("PDF/A");
        d.setHealthcareFacilityTypeCode("Ospedale"); d.setHealthcareFacilityTypeCodeName("Ospedale");
        d.setPracticeSettingCode("AD_PSC001"); d.setPracticeSettingCodeName("Allergologia");
        d.setTypeCode("11502-2"); d.setTypeCodeName("Laboratory report"); d.setEventCodeList(List.of("J07AC", "J07AD"));
        d.setAuthor("Document author"); d.setAuthorRole("APR"); d.setAuthorInstitution("Document institution");
        return d;
    }

    private ExtrinsicObjectType existing(UpdateCase scenario) {
        var body = PublishReplaceBodyBuilderUtility.buildSubmitObjectRequest(document(), new SubmissionSetEntryDTO(),
                token(ActionEnumType.UPDATE).getPayload(), null);
        var d = (ExtrinsicObjectType) body.getRegistryObjectList().getIdentifiable().getFirst().getValue();

        d.setId(TARGET); d.setLid(scenario == UpdateCase.NO_LID ? null : "urn:uuid:bbbbbbbb-bbbb-4ccc-8ddd-eeeeeeeeeeee");
        var version = new VersionInfoType(); version.setVersionName("3"); d.setVersionInfo(version);

        // Give merged fields old values: comparing two unchanged snapshots would not
        // catch a regression that accidentally stops applying the requested changes.
        for (var classification : d.getClassification()) {
            if (List.of(ClassificationEnum.CLASS_CODE, ClassificationEnum.HEALTH_CARE_FACILITY_TYPE_CODE,
                    ClassificationEnum.PRACTICE_SETTING_CODE, ClassificationEnum.EVENT_CODE).stream()
                    .anyMatch(category -> category.getClassificationScheme().equals(classification.getClassificationScheme()))) {
                classification.setNodeRepresentation("old-code");
                classification.getName().getLocalizedString().getFirst().setValue("Old display name");
            }
        }

        for (var existingSlot : d.getSlot()) {
            if (List.of("serviceStartTime", "serviceStopTime", "urn:ita:2022:description",
                    "urn:ita:2022:administrativeRequest").contains(existingSlot.getName())) {
                existingSlot.getValueList().getValue().clear();
                existingSlot.getValueList().getValue().add("old-value");
            }
        }

        if (scenario == UpdateCase.NO_AUTHOR) d.getClassification().removeIf(c -> ClassificationEnum.AUTHOR.getClassificationScheme().equals(c.getClassificationScheme()));

        // Unknown metadata must survive the merge, rather than being silently rebuilt away.
        var slot = new SlotType1(); slot.setName("custom-preserved-slot"); slot.setValueList(new ValueListType());
        slot.getValueList().getValue().add("preserved value"); d.getSlot().add(slot);
        return d;
    }

    private void assertComparable(SOAPMessage legacy, SOAPMessage actual) throws Exception {
        var expectedXml = serialized(legacy);
        var actualXml = serialized(actual);

        // Validate the original serialized signatures before normalizing dynamic values.
        verifySignature(expectedXml);
        verifySignature(actualXml);

        var expected = normalized(expectedXml.getDocumentElement()).lines().toList();
        var observed = normalized(actualXml.getDocumentElement()).lines().toList();

        // Report the first differing line with nearby context rather than dumping both SOAPs.
        for (int i = 0; i < Math.min(expected.size(), observed.size()); i++) {
            final int line = i;
            assertEquals(expected.get(i), observed.get(i), () -> "Normalized SOAP differs at line " + (line + 1)
                    + "; preceding context:\n" + String.join("\n", expected.subList(Math.max(0, line - 4), line)));
        }

        assertEquals(expected.size(), observed.size(), "Normalized SOAP line count");
    }

    private org.w3c.dom.Document serialized(SOAPMessage message) throws Exception {
        var bytes = new ByteArrayOutputStream(); message.writeTo(bytes);

        var factory = DocumentBuilderFactory.newInstance(); factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return factory.newDocumentBuilder().parse(new ByteArrayInputStream(bytes.toByteArray()));
    }

    private void verifySignature(org.w3c.dom.Document xml) throws Exception {
        var assertion = (Element) xml.getElementsByTagNameNS(SAML, "Assertion").item(0);
        assertNotNull(assertion); assertFalse(assertion.getAttribute("ID").isBlank());
        assertion.setIdAttribute("ID", true);

        // Timestamps differ between builds, but the legacy validity interval must match.
        var conditions = (Element) xml.getElementsByTagNameNS(SAML, "Conditions").item(0);
        var notBefore = java.time.OffsetDateTime.parse(conditions.getAttribute("NotBefore"));
        var expiry = java.time.OffsetDateTime.parse(conditions.getAttribute("NotOnOrAfter"));
        assertTrue(java.time.Duration.between(notBefore.plusYears(1), expiry).abs().toSeconds() < 2,
                "Preserve the legacy one-year validity interval (not a security endorsement)");

        var context = new DOMValidateContext(credential.getEntityCertificate().getPublicKey(),
                xml.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature").item(0));

        // Compatibility check: legacy uses a SHA-1 reference digest. Not a recommendation for new protocols.
        context.setProperty("org.jcp.xml.dsig.secureValidation", false);
        assertTrue(XMLSignatureFactory.getInstance("DOM").unmarshalXMLSignature(context).validate(context),
                "SAML signature must validate after final SOAP serialization");
    }

    /** Expanded names ignore prefixes; sibling order is ignored except ordered slot values. */
    private String normalized(Element element) {
        // Compare expanded XML names, not namespace-prefix spelling or attribute order.
        Map<String, String> attributes = new TreeMap<>();
        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            var a = element.getAttributes().item(i);
            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(a.getNamespaceURI())) continue;
            String value = a.getNodeValue();
            if (SAML.equals(element.getNamespaceURI()) && "Assertion".equals(element.getLocalName()) && "ID".equals(a.getNodeName())) value = "ASSERTION_ID";
            if (SAML.equals(element.getNamespaceURI()) && List.of("IssueInstant", "NotBefore", "NotOnOrAfter", "AuthnInstant").contains(a.getNodeName())) {
                assertDoesNotThrow(() -> java.time.Instant.parse(a.getNodeValue())); value = "GENERATED_TIME";
            }
            if (XMLSignature.XMLNS.equals(element.getNamespaceURI()) && "Reference".equals(element.getLocalName()) && "URI".equals(a.getNodeName())) {
                assertEquals("#" + ((Element) element.getOwnerDocument().getElementsByTagNameNS(SAML, "Assertion").item(0)).getAttribute("ID"), value);
                value = "#ASSERTION_ID";
            }
            if (RIM.equals(element.getNamespaceURI()) && "Association".equals(element.getLocalName())
                    && "urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember".equals(element.getAttribute("associationType")) && "id".equals(a.getNodeName())) {
                assertDoesNotThrow(() -> java.util.UUID.fromString(a.getNodeValue().substring("urn:uuid:".length()))); value = "MEMBERSHIP_ID";
            }
            if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(a.getNamespaceURI()) && "type".equals(a.getLocalName())) {
                String[] parts = value.split(":", 2);
                value = "{" + element.lookupNamespaceURI(parts.length == 2 ? parts[0] : null) + "}" + parts[parts.length - 1];
            }
            attributes.put(expanded(a), value);
        }

        // Preserve slot-value order while comparing other sibling elements without ordering.
        List<String> children = new ArrayList<>();
        StringBuilder text = new StringBuilder();
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element e) children.add(normalized(e));
            else if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) text.append(child.getNodeValue());
        }
        if (!"ValueList".equals(element.getLocalName())) children.sort(String::compareTo);

        // Dynamic values are checked for shape/reference integrity, then replaced for comparison.
        String value = text.toString();
        if (WSA.equals(element.getNamespaceURI()) && "MessageID".equals(element.getLocalName())) {
            assertDoesNotThrow(() -> java.util.UUID.fromString(text.toString())); value = "MESSAGE_ID";
        }
        if (XMLSignature.XMLNS.equals(element.getNamespaceURI()) && List.of("DigestValue", "SignatureValue").contains(element.getLocalName())) value = "VERIFIED_SIGNATURE_BYTES";
        if (RIM.equals(element.getNamespaceURI()) && "Value".equals(element.getLocalName())
                && element.getParentNode().getParentNode() instanceof Element slot && "submissionTime".equals(slot.getAttribute("name"))) {
            assertTrue(value.matches("[0-9]{14}")); value = "SUBMISSION_TIME";
        }

        return expanded(element) + attributes + (value.isBlank() ? "" : "=" + value) + "[\n"
                + children.stream().collect(Collectors.joining("\n")) + "\n]";
    }

    private String expanded(Node node) { return "{" + node.getNamespaceURI() + "}" + node.getLocalName(); }

    @Test
    @DisplayName("SOAP comparison detects changed metadata and distinguishes empty slots from missing slots")
    void comparisonDoesNotHideMetadataChangesOrMissingEmptySlots() throws Exception {
        var factory = DocumentBuilderFactory.newInstance(); factory.setNamespaceAware(true);
        var xml = factory.newDocumentBuilder().newDocument();
        var object = xml.createElementNS(RIM, "rim:ExtrinsicObject"); xml.appendChild(object);
        object.setAttribute("mimeType", "application/pdf");
        String original = normalized(object);

        // A metadata value change must not disappear during normalization.
        object.setAttribute("mimeType", "text/xml");
        assertNotEquals(original, normalized(object));

        // An explicit empty ValueList must remain different from an omitted slot.
        object.setAttribute("mimeType", "application/pdf");
        var slot = xml.createElementNS(RIM, "rim:Slot"); slot.setAttribute("name", "serviceStartTime");
        slot.appendChild(xml.createElementNS(RIM, "rim:ValueList")); object.appendChild(slot);
        assertNotEquals(original, normalized(object), "An empty slot is not equivalent to a missing slot");
    }
}
