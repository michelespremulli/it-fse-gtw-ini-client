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
package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.Configuration;
import org.opensaml.xml.XMLObject;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.impl.SAMLObjectContentReference;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml2.core.impl.AttributeStatementBuilder;
import org.opensaml.saml2.core.impl.AuthnContextBuilder;
import org.opensaml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml2.core.impl.AuthnStatementBuilder;
import org.opensaml.saml2.core.impl.ConditionsBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.SubjectBuilder;
import org.opensaml.ws.wsaddressing.Action;
import org.opensaml.ws.wsaddressing.MessageID;
import org.opensaml.ws.wsaddressing.impl.ActionBuilder;
import org.opensaml.ws.wsaddressing.impl.MessageIDBuilder;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.schema.impl.XSStringBuilder;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.opensaml.xml.signature.impl.X509CertificateBuilder;
import org.opensaml.xml.signature.impl.X509DataBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.iti42.ForgedIti42Request;
import it.finanze.sanita.fse2.ms.iniclient.dto.iti42.GuidedPublicationRequest;
import it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum;
import it.finanze.sanita.fse2.ms.iniclient.utility.FileUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AssociationType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.InternationalStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.LocalizedStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;

import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CLASSIFICATION_OBJECT_URN;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.EXTERNAL_IDENTIFIER_URN;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.HEADER_ATTRNAME_URI;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.HEADER_NAME_FORMAT;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.REGISTER_DOCUMENT_SETB_ACTION;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.HEADER_AUTH_CONTEXT;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.SUBJECT_AUTHENTICATOR;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.LANGUAGE_CODE;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.DOCUMENT_SIGNED;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.SOURCE_ID_PREFIX;

/** Complete publication SOAP construction and signing, without transport or JWT interpretation. */
@Component
@ConditionalOnProperty(name = "ini.guided.enabled", havingValue = "true")
public class Iti42SoapForge {

    // Local XML references, not the clinical document/submission uniqueId values.
    private static final String DOCUMENT_ENTRY_ID = "Document1";
    private static final String SUBMISSION_SET_ID = "SubmissionSet1";
    private static final String PUBLICATION_ACTION = "CREATE";
    private static final String MEMBERSHIP_STATUS = "Original";
    private static final String DOCUMENT_MIME_TYPE = "application/pdf+text/x-cda-r2+xml";
    private static final DateTimeFormatter SUBMISSION_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final IniCFG config;
    private final String keyStoreType;
    private final ObjectFactory objects = new ObjectFactory();
    private final BasicX509Credential credential;
    private final JAXBContext binding;

    public Iti42SoapForge(IniCFG config,
            @Value("${ini.guided.signing-store-type:PKCS12}") String keyStoreType) {
        this.config = config;
        this.keyStoreType = keyStoreType;

        try {
            DefaultBootstrap.bootstrap();
            this.credential = loadCredential();
            this.binding = JAXBContext.newInstance(SubmitObjectsRequest.class);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot initialize ITI-42 Forge signing", exception);
        }
    }

    public ForgedIti42Request forge(GuidedPublicationRequest input) {
        try {
            required(input, "request");
            required(config.getUrlWsdlDocumentRegistryService(), "destination");
            URI destination = URI.create(config.getUrlWsdlDocumentRegistryService());

            // WS-Addressing identifies the transaction and this particular message.
            Action action = new ActionBuilder().buildObject();
            action.setValue(REGISTER_DOCUMENT_SETB_ACTION);

            MessageID messageId = new MessageIDBuilder().buildObject();
            messageId.setValue(registryId());

            SubmitObjectsRequest body = buildBody(input);
            Assertion assertion = buildAssertion(input);

            // Finish all assertion content before computing its signature.
            sign(assertion);

            SOAPMessage message = assemble(action, messageId, assertion, body);
            return new ForgedIti42Request(destination, action.getValue(), messageId.getValue(), assertion.getID(), message);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot construct and sign ITI-42 publication", exception);
        }
    }

    private SOAPMessage assemble(Action action, MessageID messageId, Assertion assertion,
            SubmitObjectsRequest body) throws Exception {
        SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, StandardCharsets.UTF_8.name());

        // Routing and the already signed assertion belong in the SOAP header.
        var header = message.getSOAPHeader();
        header.appendChild(message.getSOAPPart().importNode(element(action), true));
        header.appendChild(message.getSOAPPart().importNode(element(messageId), true));
        var securityHeader = header.addChildElement("Security", "wsse",
                "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        securityHeader.appendChild(message.getSOAPPart().importNode(assertion.getDOM(), true));

        // Create a marshaller per request; only JAXBContext is shared.
        binding.createMarshaller().marshal(body, message.getSOAPBody());
        message.saveChanges();

        return message;
    }

    private Element element(XMLObject object) throws Exception {
        return Configuration.getMarshallerFactory().getMarshaller(object).marshall(object);
    }

    private Assertion buildAssertion(GuidedPublicationRequest input) {
        Instant now = Instant.now();

        Assertion assertion = new AssertionBuilder().buildObject();
        assertion.setID("_" + UUID.randomUUID());
        assertion.setVersion(SAMLVersion.VERSION_20);
        assertion.setIssueInstant(time(now));

        Issuer issuer = new IssuerBuilder().buildObject();
        issuer.setValue(input.organizationId());
        assertion.setIssuer(issuer);

        NameID nameId = new NameIDBuilder().buildObject();
        nameId.setValue(input.subjectId());

        Subject subject = new SubjectBuilder().buildObject();
        subject.setNameID(nameId);
        assertion.setSubject(subject);

        // NotOnOrAfter is an exclusive expiry boundary, not an inclusive end time.
        Conditions conditions = new ConditionsBuilder().buildObject();
        // Preserve the existing builder's one-calendar-year validity interval.
        conditions.setNotBefore(time(now));
        conditions.setNotOnOrAfter(time(now).plusYears(1));
        assertion.setConditions(conditions);

        AuthnContextClassRef contextRef = new AuthnContextClassRefBuilder().buildObject();
        contextRef.setAuthnContextClassRef(HEADER_AUTH_CONTEXT);

        AuthnContext context = new AuthnContextBuilder().buildObject();
        context.setAuthnContextClassRef(contextRef);

        AuthnStatement authentication = new AuthnStatementBuilder().buildObject();
        authentication.setAuthnInstant(time(now));
        authentication.setAuthnContext(context);
        assertion.getAuthnStatements().add(authentication);

        assertion.getAttributeStatements().add(buildAttributes(input));

        return assertion;
    }

    private AttributeStatement buildAttributes(GuidedPublicationRequest request) {
        var input = request;
        AttributeStatement statement = new AttributeStatementBuilder().buildObject();

        attribute(statement, "urn:oasis:names:tc:xacml:1.0:subject:subject-id", input.subjectId(), HEADER_ATTRNAME_URI);
        attribute(statement, "urn:oasis:names:tc:xacml:2.0:subject:role", input.role(), HEADER_ATTRNAME_URI);
        attribute(statement, "urn:oasis:names:tc:xspa:1.0:subject:organization-id", input.organizationId(), HEADER_ATTRNAME_URI);
        attribute(statement, "urn:oasis:names:tc:xspa:1.0:subject:organization", input.organization(), HEADER_ATTRNAME_URI);
        attribute(statement, "urn:oasis:names:tc:xspa:1.0:environment:locality", input.locality(), HEADER_ATTRNAME_URI);
        attribute(statement, "urn:oasis:names:tc:xspa:1.0:subject:purposeofuse", input.purposeOfUse(), HEADER_ATTRNAME_URI);
        attribute(statement, "urn:oasis:names:tc:xacml:1.0:resource:resource-id", request.patientId(), HEADER_ATTRNAME_URI);

        // An absent consent attribute is different from an explicit false value.
        if (input.patientConsent() != null) {
            attribute(statement, "urn:oasis:names:tc:xspa:1.0:resource:patient:consent", input.patientConsent().toString(), HEADER_ATTRNAME_URI);
        }

        attribute(statement, "urn:oasis:names:tc:xspa:1.0:resource:hl7:type", input.resourceHl7Type(), HEADER_ATTRNAME_URI);
        attribute(statement, "urn:oasis:names:tc:xacml:1.0:action:action-id", PUBLICATION_ACTION, HEADER_ATTRNAME_URI);

        attribute(statement, "SubjectApplicationId", input.applicationId(), HEADER_NAME_FORMAT);
        attribute(statement, "SubjectApplicationVendor", input.applicationVendor(), HEADER_NAME_FORMAT);
        attribute(statement, "SubjectApplicationVersion", input.applicationVersion(), HEADER_NAME_FORMAT);
        attribute(statement, "SubjectAuthenticator", SUBJECT_AUTHENTICATOR, HEADER_NAME_FORMAT);

        return statement;
    }

    private void attribute(AttributeStatement statement, String name, String value, String format) {
        Attribute attribute = new AttributeBuilder().buildObject();
        attribute.setName(name);
        attribute.setNameFormat(format);

        // AttributeValue is XML content with an explicit xs:string type.
        XSString text = new XSStringBuilder().buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        text.setValue(value);

        attribute.getAttributeValues().add(text);
        statement.getAttributes().add(attribute);
    }

    private SubmitObjectsRequest buildBody(GuidedPublicationRequest input) {

        RegistryObjectListType entries = new RegistryObjectListType();

        // ITI-42 registers document metadata; it does not carry the PDF/CDA bytes.
        var documentInput = input;
        ExtrinsicObjectType document = new ExtrinsicObjectType();
        document.setMimeType(DOCUMENT_MIME_TYPE);
        document.setIsOpaque(false);
        populate(document, DOCUMENT_ENTRY_ID, documentInput.title(),
                "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
        documentSlots(document, input);
        coded(document, ClassificationEnum.CLASS_CODE, documentInput.classCode());
        coded(document, ClassificationEnum.CONFIDENTIALITY_CODE, documentInput.confidentialityCode());
        coded(document, ClassificationEnum.FORMAT_CODE, documentInput.formatCode());
        coded(document, ClassificationEnum.HEALTH_CARE_FACILITY_TYPE_CODE, documentInput.healthcareFacilityTypeCode());
        coded(document, ClassificationEnum.PRACTICE_SETTING_CODE, documentInput.practiceSettingCode());
        coded(document, ClassificationEnum.TYPE_CODE, documentInput.typeCode());

        for (GuidedPublicationRequest.Code event : supplied(documentInput.eventCodes())) {
            coded(document, ClassificationEnum.EVENT_CODE, event);
        }

        author(document, ClassificationEnum.AUTHOR.getClassificationScheme(), input);
        identifier(document, "XDSDocumentEntry.patientId", "urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427", input.patientId());
        identifier(document, "XDSDocumentEntry.uniqueId", "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab", input.documentUniqueId());
        entries.getIdentifiable().add(objects.createExtrinsicObject(document));

        var submissionInput = input;
        RegistryPackageType submission = new RegistryPackageType();
        populate(submission, SUBMISSION_SET_ID, null,
                "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage");
        // Match Dispatcher's SimpleDateFormat behavior using the server's timezone.
        addSlot(submission, "submissionTime", SUBMISSION_TIME_FORMAT.format(Instant.now().atZone(ZoneId.systemDefault())));
        coded(submission, "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500",
                "2.16.840.1.113883.2.9.3.3.6.1.4", submissionInput.contentTypeCode());
        author(submission, "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d", input);
        identifier(submission, "XDSSubmissionSet.patientId", "urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446", input.patientId());
        identifier(submission, "XDSSubmissionSet.uniqueId", "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8", input.submissionUniqueId());
        required(input.organizationId(), "urn:oasis:names:tc:xspa:1.0:subject:organization-id");
        identifier(submission, "XDSSubmissionSet.sourceId", "urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832",
                SOURCE_ID_PREFIX + StringUtility.sanitizeSourceId(input.organizationId()));
        entries.getIdentifiable().add(objects.createRegistryPackage(submission));

        // The association links the submission package to the document entry.
        AssociationType1 membership = new AssociationType1();
        membership.setId(registryId());
        membership.setAssociationType("urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember");
        membership.setSourceObject(submission.getId());
        membership.setTargetObject(document.getId());
        addSlot(membership, "SubmissionSetStatus", MEMBERSHIP_STATUS);
        entries.getIdentifiable().add(objects.createAssociation(membership));

        // This standard marker identifies the RegistryPackage as an XDS SubmissionSet.
        ClassificationType marker = new ClassificationType();
        marker.setId(registryId());
        marker.setClassificationNode("urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd");
        marker.setClassifiedObject(submission.getId());
        marker.setObjectType(CLASSIFICATION_OBJECT_URN);
        entries.getIdentifiable().add(objects.createClassification(marker));

        SubmitObjectsRequest body = new SubmitObjectsRequest();
        body.setRegistryObjectList(entries);

        return body;
    }

    private void populate(RegistryObjectType output, String id, String name, String objectType) {
        output.setId(id);
        output.setObjectType(objectType);
        output.setStatus("urn:oasis:names:tc:ebxml-regrep:StatusType:Approved");

        if (name != null) {
            output.setName(name(name));
        }

    }

    private void documentSlots(RegistryObjectType document, GuidedPublicationRequest request) {
        var input = request;
        addSlot(document, "languageCode", LANGUAGE_CODE);
        addSlot(document, "repositoryUniqueId", input.repositoryUniqueId());
        addSlot(document, "sourcePatientId", request.patientId());
        addSlot(document, "urn:ita:2017:repository-type", input.conservazioneANorma());
        addSlot(document, "urn:ita:2022:documentSigned", DOCUMENT_SIGNED);
        document.getSlot().add(slot("urn:ita:2022:description", input.descriptions()));
        document.getSlot().add(slot("urn:ita:2022:administrativeRequest", input.administrativeRequests()));
        addSlot(document, "size", input.size() == null ? null : input.size().toString());
        addSlot(document, "hash", input.hash());
        addSlot(document, "creationTime", input.creationTime());
        addSlot(document, "serviceStartTime", input.serviceStartTime());
        addSlot(document, "serviceStopTime", input.serviceStopTime());
        document.getSlot().add(slot("urn:ihe:iti:xds:2013:referenceIdList", input.referenceIds()));

        required(request.applicationId(), "SubjectApplicationId");
        required(request.applicationVendor(), "SubjectApplicationVendor");
        required(request.applicationVersion(), "SubjectApplicationVersion");
        addSlot(document, "urn:ihe:iti:xds:2024:SubjectApplication",
                String.join("^", request.applicationId(), request.applicationVendor(), request.applicationVersion()));
    }

    private void coded(RegistryObjectType owner, ClassificationEnum category, GuidedPublicationRequest.Code code) {
        coded(owner, category.getClassificationScheme(), category.getCodingScheme(), code);
    }

    private void coded(RegistryObjectType owner, String scheme, String codingSystem, GuidedPublicationRequest.Code code) {
        if (code == null) {
            return;
        }

        ClassificationType classification = classification(owner, scheme, code.value());
        if (code.displayName() != null) {
            classification.setName(name(code.displayName()));
        }
        addSlot(classification, "codingScheme", codingSystem);
    }

    private ClassificationType classification(RegistryObjectType owner, String scheme, String value) {
        ClassificationType classification = new ClassificationType();
        classification.setId(registryId());
        classification.setObjectType(CLASSIFICATION_OBJECT_URN);
        classification.setClassifiedObject(owner.getId());
        classification.setClassificationScheme(scheme);
        classification.setNodeRepresentation(value);
        owner.getClassification().add(classification);
        return classification;
    }

    private void author(RegistryObjectType owner, String scheme, GuidedPublicationRequest request) {
        ClassificationType classification = classification(owner, scheme, "");
        addSlot(classification, "authorRole", request.role());
        addSlot(classification, "authorInstitution", request.locality());
        addSlot(classification, "authorPerson", request.authorPerson());
    }

    private void identifier(RegistryObjectType owner, String label, String scheme, String value) {
        ExternalIdentifierType identifier = new ExternalIdentifierType();
        identifier.setId(registryId());
        identifier.setRegistryObject(owner.getId());
        identifier.setObjectType(EXTERNAL_IDENTIFIER_URN);
        identifier.setIdentificationScheme(scheme);
        identifier.setName(name(label));
        identifier.setValue(value);
        owner.getExternalIdentifier().add(identifier);
    }

    private void addSlot(RegistryObjectType owner, String name, String value) {
        owner.getSlot().add(slot(name, value == null ? List.of() : List.of(value)));
    }

    private SlotType1 slot(String name, List<String> input) {
        SlotType1 output = new SlotType1();
        output.setName(name);

        ValueListType values = new ValueListType();
        supplied(input).stream().filter(value -> value != null).forEach(values.getValue()::add);

        output.setValueList(values);

        return output;
    }

    private InternationalStringType name(String value) {
        LocalizedStringType text = new LocalizedStringType();
        text.setValue(value);

        InternationalStringType name = new InternationalStringType();
        name.getLocalizedString().add(text);

        return name;
    }

    private BasicX509Credential loadCredential() throws Exception {
        required(config.getKeyStorePassword(), "signing keystore password");
        required(config.getKeyStoreLocation(), "signing keystore location");

        KeyStore store = KeyStore.getInstance(keyStoreType);
        char[] password = config.getKeyStorePassword().toCharArray();

        try (InputStream stream = FileUtility.getFileFromAbsoluteOrResourceInputStream(config.getKeyStoreLocation())) {
            store.load(stream, password);
        }

        String alias = config.getKeyStoreAlias();
        if (alias == null || alias.isBlank()) {
            List<String> keys = Collections.list(store.aliases()).stream().filter(value -> {
                try {
                    return store.isKeyEntry(value);
                } catch (Exception exception) {
                    throw new IllegalStateException(exception);
                }
            }).toList();

            if (keys.size() != 1) {
                throw new IllegalStateException("Configure an unambiguous signing alias");
            }

            alias = keys.getFirst();
        }

        if (!(store.getKey(alias, password) instanceof PrivateKey key)
                || !(store.getCertificate(alias) instanceof X509Certificate certificate)
                || !"RSA".equals(key.getAlgorithm())) {
            throw new IllegalStateException("Signing alias must contain an RSA private key and X509 certificate");
        }

        BasicX509Credential result = new BasicX509Credential();
        result.setPrivateKey(key);
        result.setEntityCertificate(certificate);

        return result;
    }

    private void sign(Assertion assertion) throws Exception {
        Signature signature = new SignatureBuilder().buildObject();
        signature.setSigningCredential(credential);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

        // KeyInfo carries the public certificate, never the private signing key.
        var keyInfo = new KeyInfoBuilder().buildObject();
        var data = new X509DataBuilder().buildObject();
        var certificate = new X509CertificateBuilder().buildObject();
        certificate.setValue(Base64.getEncoder().encodeToString(credential.getEntityCertificate().getEncoded()));

        data.getX509Certificates().add(certificate);
        keyInfo.getX509Datas().add(data);
        signature.setKeyInfo(keyInfo);

        // Attaching Signature creates the reference to this assertion's ID.
        assertion.setSignature(signature);
        ((SAMLObjectContentReference) signature.getContentReferences().getFirst())
                .setDigestAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA256);

        // Signing needs a DOM representation of the completed assertion.
        Configuration.getMarshallerFactory().getMarshaller(assertion).marshall(assertion);
        Signer.signObject(signature);
    }

    private DateTime time(Instant value) {
        return value == null ? null : new DateTime(value.toEpochMilli(), DateTimeZone.UTC);
    }

    private String registryId() {
        return "urn:uuid:" + UUID.randomUUID();
    }

    private <T> List<T> supplied(List<T> values) {
        return values == null ? List.of() : values;
    }

    private void required(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is needed to construct the SOAP request");
        }
    }
}
