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

import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
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
import org.opensaml.ws.wssecurity.Security;
import org.opensaml.ws.wssecurity.WSSecurityConstants;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.schema.impl.XSStringBuilder;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.opensaml.xml.signature.impl.X509CertificateBuilder;
import org.opensaml.xml.signature.impl.X509DataBuilder;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.dto.iti42.ForgedIti42Request;
import it.finanze.sanita.fse2.ms.iniclient.dto.iti42.GuidedPublicationRequest;
import it.finanze.sanita.fse2.ms.iniclient.enums.ClassificationEnum;
import it.finanze.sanita.fse2.ms.iniclient.enums.ExternalIdentifierEnum;
import it.finanze.sanita.fse2.ms.iniclient.singleton.SigningCredentialSingleton;
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
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.DOCUMENT_ENTRY_ID;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.SUBMISSION_ENTRY_ID;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CLASSIFICATION_ID;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CODING_SCHEME;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.URN_UUID;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.IniClientConstants.CREATE_ACTION;

/** CREATE-only ITI-42 construction, without JWT interpretation or metadata lookup. */
@Component
public class Iti42SoapForge {

    // Protocol identifiers without an existing library or legacy constant.
    private static final String APPROVED_STATUS = "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved";
    private static final String DOCUMENT_OBJECT_TYPE = "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";
    private static final String REGISTRY_PACKAGE_OBJECT_TYPE = "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage";
    private static final String HAS_MEMBER_ASSOCIATION_TYPE = "urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember";
    private static final String SUBMISSION_SET_CLASSIFICATION_NODE = "urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd";
    private static final String SUBMISSION_CONTENT_TYPE_SCHEME = "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500";
    private static final String SUBMISSION_CONTENT_TYPE_CODING_SCHEME = "2.16.840.1.113883.2.9.3.3.6.1.4";
    private static final String SUBMISSION_AUTHOR_SCHEME = "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d";
    private static final String SUBMISSION_SOURCE_ID_SCHEME = "urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832";
    private static final String SUBMISSION_UNIQUE_ID_SCHEME = "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8";
    private static final String SUBMISSION_PATIENT_ID_SCHEME = "urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446";

    private static final String SAML_PATIENT_CONSENT = "urn:oasis:names:tc:xspa:1.0:resource:patient:consent";
    private static final String SAML_ROLE = "urn:oasis:names:tc:xacml:2.0:subject:role";
    private static final String SAML_SUBJECT_ID = "urn:oasis:names:tc:xacml:1.0:subject:subject-id";
    private static final String SAML_HL7_TYPE = "urn:oasis:names:tc:xspa:1.0:resource:hl7:type";
    private static final String SAML_LOCALITY = "urn:oasis:names:tc:xspa:1.0:environment:locality";
    private static final String SAML_PURPOSE_OF_USE = "urn:oasis:names:tc:xspa:1.0:subject:purposeofuse";
    private static final String SAML_ORGANIZATION_ID = "urn:oasis:names:tc:xspa:1.0:subject:organization-id";
    private static final String SAML_ORGANIZATION = "urn:oasis:names:tc:xspa:1.0:subject:organization";
    private static final String SAML_RESOURCE_ID = "urn:oasis:names:tc:xacml:1.0:resource:resource-id";
    private static final String SAML_ACTION_ID = "urn:oasis:names:tc:xacml:1.0:action:action-id";

    private static final String REPOSITORY_TYPE_SLOT = "urn:ita:2017:repository-type";
    private static final String DOCUMENT_SIGNED_SLOT = "urn:ita:2022:documentSigned";
    private static final String DESCRIPTION_SLOT = "urn:ita:2022:description";
    private static final String ADMINISTRATIVE_REQUEST_SLOT = "urn:ita:2022:administrativeRequest";
    private static final String REFERENCE_ID_LIST_SLOT = "urn:ihe:iti:xds:2013:referenceIdList";
    private static final String SUBJECT_APPLICATION_SLOT = "urn:ihe:iti:xds:2024:SubjectApplication";

    private static final String MEMBERSHIP_STATUS = "Original";

    private final IniCFG config;
    private final ObjectFactory objects = new ObjectFactory();
    private final JAXBContext binding;

    public Iti42SoapForge(IniCFG config) {
        this.config = config;

        try {
            DefaultBootstrap.bootstrap();
            this.binding = JAXBContext.newInstance(SubmitObjectsRequest.class);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot initialize ITI-42 Forge XML support", exception);
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
            messageId.setValue(UUID.randomUUID().toString());

            SubmitObjectsRequest body = buildBody(input);
            Assertion assertion = buildAssertion(input);

            // Finish all assertion content before computing its signature.
            sign(assertion);

            SOAPMessage message = assemble(action, messageId, assertion, body);
            return new ForgedIti42Request(destination, action.getValue(), messageId.getValue(), assertion.getID(), message);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot construct and sign ITI-42 request", exception);
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

        var securityHeader = header.addChildElement(Security.ELEMENT_LOCAL_NAME,
                WSSecurityConstants.WSSE_PREFIX, WSSecurityConstants.WSSE_NS);
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

        Assertion assertion = new AssertionBuilder().buildObject();
        assertion.setID("_" + UUID.randomUUID());
        assertion.setVersion(SAMLVersion.VERSION_20);
        assertion.setIssueInstant(new DateTime());

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
        conditions.setNotBefore(new DateTime());
        conditions.setNotOnOrAfter(new DateTime().plusYears(1));
        assertion.setConditions(conditions);

        AuthnContextClassRef contextRef = new AuthnContextClassRefBuilder().buildObject();
        contextRef.setAuthnContextClassRef(HEADER_AUTH_CONTEXT);

        AuthnContext context = new AuthnContextBuilder().buildObject();
        context.setAuthnContextClassRef(contextRef);

        AuthnStatement authentication = new AuthnStatementBuilder().buildObject();
        authentication.setAuthnInstant(new DateTime());
        authentication.setAuthnContext(context);
        assertion.getAuthnStatements().add(authentication);

        assertion.getAttributeStatements().add(buildAttributes(input));

        return assertion;
    }

    private AttributeStatement buildAttributes(GuidedPublicationRequest request) {
        var input = request;
        AttributeStatement statement = new AttributeStatementBuilder().buildObject();

        if (input.patientConsent() != null) {
            attribute(statement, SAML_PATIENT_CONSENT, input.patientConsent().toString(), HEADER_ATTRNAME_URI);
        }
        attribute(statement, SAML_ROLE, input.role(), HEADER_ATTRNAME_URI);
        attribute(statement, SAML_SUBJECT_ID, input.subjectId(), HEADER_ATTRNAME_URI);
        attribute(statement, SAML_HL7_TYPE, input.resourceHl7Type(), HEADER_ATTRNAME_URI);
        attribute(statement, SAML_LOCALITY, input.locality(), HEADER_ATTRNAME_URI);
        attribute(statement, SAML_PURPOSE_OF_USE, input.purposeOfUse(), HEADER_ATTRNAME_URI);
        attribute(statement, SAML_ORGANIZATION_ID, input.organizationId(), HEADER_ATTRNAME_URI);
        attribute(statement, SAML_ORGANIZATION, input.organization(), HEADER_ATTRNAME_URI);
        attribute(statement, SAML_RESOURCE_ID, request.patientId(), HEADER_ATTRNAME_URI);
        attribute(statement, SAML_ACTION_ID, CREATE_ACTION, HEADER_ATTRNAME_URI);

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
        ExtrinsicObjectType document = buildNewDocument(input);
        entries.getIdentifiable().add(objects.createExtrinsicObject(document));

        var submissionInput = input;
        RegistryPackageType submission = new RegistryPackageType();
        populate(submission, SUBMISSION_ENTRY_ID, null, REGISTRY_PACKAGE_OBJECT_TYPE);

        // Same format and default timezone as the legacy publication path.
        addSlot(submission, "submissionTime", input.submissionTime() == null || input.submissionTime().isBlank()
                ? new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) : input.submissionTime());
        coded(submission, SUBMISSION_CONTENT_TYPE_SCHEME,
                SUBMISSION_CONTENT_TYPE_CODING_SCHEME, submissionInput.contentTypeCode(), "IdContentTypeCode");
        author(submission, SUBMISSION_AUTHOR_SCHEME, input.submissionAuthorPerson(),
                input.submissionAuthorRole(), input.submissionAuthorInstitution());

        identifier(submission, "XDSSubmissionSet.sourceId", SUBMISSION_SOURCE_ID_SCHEME,
                input.submissionSourceId());
        identifier(submission, "XDSSubmissionSet.uniqueId", SUBMISSION_UNIQUE_ID_SCHEME, input.submissionUniqueId());
        identifier(submission, "XDSSubmissionSet.patientId", SUBMISSION_PATIENT_ID_SCHEME,
                input.submissionPatientId());
        entries.getIdentifiable().add(objects.createRegistryPackage(submission));

        // The association links the submission package to the document entry.
        AssociationType1 membership = new AssociationType1();
        membership.setId(registryId());
        membership.setAssociationType(HAS_MEMBER_ASSOCIATION_TYPE);
        membership.setSourceObject(submission.getId());
        membership.setTargetObject(document.getId());
        addSlot(membership, "SubmissionSetStatus", MEMBERSHIP_STATUS);
        entries.getIdentifiable().add(objects.createAssociation(membership));

        ClassificationType marker = new ClassificationType();
        marker.setId(CLASSIFICATION_ID);
        marker.setClassificationNode(SUBMISSION_SET_CLASSIFICATION_NODE);
        marker.setClassifiedObject(submission.getId());
        marker.setObjectType(CLASSIFICATION_OBJECT_URN);
        entries.getIdentifiable().add(objects.createClassification(marker));

        SubmitObjectsRequest body = new SubmitObjectsRequest();
        body.setRegistryObjectList(entries);
        return body;
    }

    private ExtrinsicObjectType buildNewDocument(GuidedPublicationRequest input) {
        var documentInput = input;
        ExtrinsicObjectType document = new ExtrinsicObjectType();
        document.setMimeType(documentInput.mimeType());
        document.setIsOpaque(false);
        populate(document, DOCUMENT_ENTRY_ID, documentInput.title(), DOCUMENT_OBJECT_TYPE);

        documentSlots(document, input);
        coded(document, ClassificationEnum.CLASS_CODE, documentInput.classCode());
        coded(document, ClassificationEnum.CONFIDENTIALITY_CODE, documentInput.confidentialityCode());
        coded(document, ClassificationEnum.FORMAT_CODE, documentInput.formatCode());

        int eventIndex = 0;
        for (GuidedPublicationRequest.Code event : supplied(documentInput.eventCodes())) {
            if (event != null) {
                coded(document, ClassificationEnum.EVENT_CODE.getClassificationScheme(),
                        ClassificationEnum.EVENT_CODE.getCodingScheme(), event,
                        ClassificationEnum.EVENT_CODE.getId() + "_" + eventIndex++);
            }
        }

        coded(document, ClassificationEnum.HEALTH_CARE_FACILITY_TYPE_CODE, documentInput.healthcareFacilityTypeCode());
        coded(document, ClassificationEnum.PRACTICE_SETTING_CODE, documentInput.practiceSettingCode());
        coded(document, ClassificationEnum.TYPE_CODE, documentInput.typeCode());

        author(document, ClassificationEnum.AUTHOR.getClassificationScheme(), input.authorPerson(),
                input.documentAuthorRole(), input.documentAuthorInstitution());

        identifier(document, "XDSDocumentEntry.patientId", ExternalIdentifierEnum.PATIENT.getClassificationScheme(), input.documentPatientId());
        identifier(document, "XDSDocumentEntry.uniqueId", ExternalIdentifierEnum.UnIQUE_ID.getClassificationScheme(), input.documentUniqueId());
        return document;
    }

    private void populate(RegistryObjectType output, String id, String name, String objectType) {
        output.setId(id);
        output.setObjectType(objectType);
        output.setStatus(APPROVED_STATUS);

        if (name != null) {
            output.setName(name(name));
        }

    }

    private void documentSlots(RegistryObjectType document, GuidedPublicationRequest request) {

        var input = request;
        addSlot(document, "languageCode", LANGUAGE_CODE);
        addSlot(document, "repositoryUniqueId", input.repositoryUniqueId());
        addSlot(document, "sourcePatientId", input.sourcePatientId());
        addSlot(document, REPOSITORY_TYPE_SLOT, input.conservazioneANorma());
        addSlot(document, DOCUMENT_SIGNED_SLOT, DOCUMENT_SIGNED);
        addSlotValues(document, DESCRIPTION_SLOT, input.descriptions());
        addSlotValues(document, ADMINISTRATIVE_REQUEST_SLOT, input.administrativeRequests());
        addSlot(document, "size", input.size() == null ? null : input.size().toString());
        addSlot(document, "hash", input.hash());
        addSlot(document, "creationTime", input.creationTime());
        addSlot(document, "serviceStartTime", input.serviceStartTime());
        addSlot(document, "serviceStopTime", input.serviceStopTime());
        addSlotValues(document, REFERENCE_ID_LIST_SLOT, input.referenceIds());

        addSlot(document, SUBJECT_APPLICATION_SLOT, input.documentSubjectApplication());
    }

    private void coded(RegistryObjectType owner, ClassificationEnum category, GuidedPublicationRequest.Code code) {
        coded(owner, category.getClassificationScheme(), category.getCodingScheme(), code, category.getId());
    }

    private void coded(RegistryObjectType owner, String scheme, String codingSystem,
            GuidedPublicationRequest.Code code, String id) {

        if (code == null) {
            return;
        }

        ClassificationType classification = classification(owner, scheme, code.value());
        classification.setId(id);
        if (code.displayName() != null) {
            classification.setName(name(code.displayName()));
        }
        addSlot(classification, CODING_SCHEME, codingSystem);
    }

    private ClassificationType classification(RegistryObjectType owner, String scheme, String value) {
        ClassificationType classification = new ClassificationType();
        classification.setObjectType(CLASSIFICATION_OBJECT_URN);
        classification.setClassifiedObject(owner.getId());
        classification.setClassificationScheme(scheme);
        classification.setNodeRepresentation(value);
        owner.getClassification().add(classification);
        return classification;
    }

    private void author(RegistryObjectType owner, String scheme, String person, String role, String institution) {
        ClassificationType classification = classification(owner, scheme, "");
        classification.setId(DOCUMENT_ENTRY_ID.equals(owner.getId())
                ? ClassificationEnum.AUTHOR.getId() : "SubmissionSet1_ClassificationAuthor");
        addSlot(classification, "authorRole", role);
        addSlot(classification, "authorInstitution", institution);
        addSlot(classification, "authorPerson", person);
    }

    private void identifier(RegistryObjectType owner, String label, String scheme, String value) {
        ExternalIdentifierType identifier = new ExternalIdentifierType();
        identifier.setId(switch (label) {
            case "XDSDocumentEntry.patientId" -> ExternalIdentifierEnum.PATIENT.getId();
            case "XDSDocumentEntry.uniqueId" -> ExternalIdentifierEnum.UnIQUE_ID.getId();
            case "XDSSubmissionSet.sourceId" -> "SubmissionSet1_SourceId";
            case "XDSSubmissionSet.uniqueId" -> "SubmissionSet1_UniqueId";
            case "XDSSubmissionSet.patientId" -> "SubmissionSet1_PatientId";
            default -> throw new IllegalArgumentException("Unsupported external identifier");
        });
        identifier.setRegistryObject(owner.getId());
        identifier.setObjectType(EXTERNAL_IDENTIFIER_URN);
        identifier.setIdentificationScheme(scheme);
        identifier.setName(name(label));
        identifier.setValue(value);
        owner.getExternalIdentifier().add(identifier);
    }

    private void addSlot(RegistryObjectType owner, String name, String value) {
        if (value != null && !value.isEmpty()) {
            addSlotValues(owner, name, List.of(value));
        }
    }

    private void addSlotValues(RegistryObjectType owner, String name, List<String> input) {
        List<String> values = supplied(input).stream().filter(value -> value != null).toList();
        if (!values.isEmpty()) {
            owner.getSlot().add(slot(name, values));
        }
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

    private void sign(Assertion assertion) throws Exception {

        // Use exactly the same credential selection and cache as the legacy SAML builder.
        var credential = SigningCredentialSingleton.getInstance(config);
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
        // Preserve the legacy reference digest, independently of RSA-SHA256 signing.
        ((SAMLObjectContentReference) signature.getContentReferences().getFirst())
                .setDigestAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA1);

        // Signing needs a DOM representation of the completed assertion.
        Configuration.getMarshallerFactory().getMarshaller(assertion).marshall(assertion);
        Signer.signObject(signature);
    }

    private String registryId() {
        return URN_UUID + UUID.randomUUID();
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
