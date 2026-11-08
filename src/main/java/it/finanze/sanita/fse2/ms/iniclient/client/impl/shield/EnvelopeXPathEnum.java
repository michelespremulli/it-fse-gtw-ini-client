package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnvelopeXPathEnum {
	
		QMP1_ADHOC_QUERY(
		    "//*[local-name()='AdhocQuery']"
		),

		PIT7_AUTHNSTATEMENT(
		    "//*[local-name()='AuthnStatement']"
		),

		PFA15_REPOSITORY_UNIQUE_ID(
				"//*[local-name()='Slot' and @name='repositoryUniqueId']"
			),


		R74_DOCUMENTENTRY_PATIENTID_NAME(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427']"
		    + "/*[local-name()='Name']"
		),

		R81_DOCUMENTENTRY_UNIQUEID_NAME(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab']"
		    + "/*[local-name()='Name']"
		),

		R104_SUBMISSIONSET_PATIENTID_NAME(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832']"
		    + "/*[local-name()='Name']"
		),

		R110_SUBMISSIONSET_SOURCEID_NAME(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8']"
		    + "/*[local-name()='Name']"
		),

		R116_SUBMISSIONSET_UNIQUEID_NAME(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8']"
		    + "/*[local-name()='Name']"
		),

	R14_SLOT_VALUE(
		    "(//*[local-name()='Slot']/*[local-name()='ValueList']/*[local-name()='Value'])[1]"
		),

		R16_DOCUMENTENTRY_HASH(
		    "//*[local-name()='Slot' and @name='hash']/*[local-name()='ValueList']/*[local-name()='Value']"
		),

		R18_DOCUMENTENTRY_REPOSITORYUNIQUEID(
		    "//*[local-name()='Slot' and @name='repositoryUniqueId']/*[local-name()='ValueList']/*[local-name()='Value']"
		),

		R19_DOCUMENTENTRY_SIZE(
		    "//*[local-name()='Slot' and @name='size']/*[local-name()='ValueList']/*[local-name()='Value']"
		),

		R20_DOCUMENTENTRY_SOURCEPATIENTID(
		    "//*[local-name()='Slot' and @name='sourcePatientId']/*[local-name()='ValueList']/*[local-name()='Value']"
		),

		R63_DOCUMENTENTRY_CLASSCODE(
		    "//*[local-name()='Classification' and @classificationScheme='urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a']"
		),

		R64_DOCUMENTENTRY_CONFIDENTIALITYCODE(
		    "//*[local-name()='Classification' and @classificationScheme='urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f']"
		),

		R65_DOCUMENTENTRY_FORMATCODE(
		    "//*[local-name()='Classification' and @classificationScheme='urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d']"
		),

		R66_DOCUMENTENTRY_HEALTHCAREFACILITYCODE(
		    "//*[local-name()='Classification' and @classificationScheme='urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1']"
		),

		R67_DOCUMENTENTRY_PRACTICESETTINGCODE(
		    "//*[local-name()='Classification' and @classificationScheme='urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead']"
		),

		R68_DOCUMENTENTRY_TYPECODE(
		    "//*[local-name()='Classification' and @classificationScheme='urn:uuid:f0306f51-975f-434e-a61c-c59651d33983']"
		),

		R83_DOCUMENTENTRY_PATIENTID(
		    "//*[local-name()='ExternalIdentifier' and @identificationScheme='urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427']"
		),

		R84_DOCUMENTENTRY_UNIQUEID(
		    "//*[local-name()='ExternalIdentifier' and @identificationScheme='urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab']"
		),
	R26_CLASSCODE_DISPLAY_NAME(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),

		R31_CONFIDENTIALITYCODE_DISPLAY_NAME(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),

		R36_EVENTCODELIST_DISPLAY_NAME(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),

		R41_FORMATCODE_DISPLAY_NAME(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),

		R46_HEALTHCAREFACILITYCODE_DISPLAY_NAME(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),

		R51_PRACTICESETTINGCODE_DISPLAY_NAME(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),

		R56_TYPECODE_DISPLAY_NAME(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:f0306f51-975f-434e-a61c-c59651d33983']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),

		R69_DOCUMENTENTRY_PATIENTID_DISPLAY_NAME(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),

		R76_DOCUMENTENTRY_UNIQUEID_DISPLAY_NAME(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),

		R92_SUBMISSIONSET_CONTENTTYPECODE_DISPLAY_NAME(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:aa543740-bdda-424e-8c96-df4873be8500']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),

		R99_SUBMISSIONSET_PATIENTID_DISPLAY_NAME(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),

		R105_SUBMISSIONSET_SOURCEID_DISPLAY_NAME(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),

		R111_SUBMISSIONSET_UNIQUEID_DISPLAY_NAME(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8']"
		    + "/*[local-name()='Name']/*[local-name()='LocalizedString']/@value"
		),
	R8_ASSOCIATION_CLASSIFICATION_OBJECTTYPE(
		    "//*[local-name()='Classification'"
		    + " and @classificationNode='urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd']"
		    + "/@objectType"
		),
	R23_DOCUMENTENTRY_AUTHOR_OBJECTTYPE(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d']"
		    + "/@objectType"
		),

		R29_DOCUMENTENTRY_CLASSCODE_OBJECTTYPE(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a']"
		    + "/@objectType"
		),

		R34_DOCUMENTENTRY_CONFIDENTIALITYCODE_OBJECTTYPE(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f']"
		    + "/@objectType"
		),

		R39_DOCUMENTENTRY_EVENTCODELIST_OBJECTTYPE(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4']"
		    + "/@objectType"
		),

		R44_DOCUMENTENTRY_FORMATCODE_OBJECTTYPE(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d']"
		    + "/@objectType"
		),

		R49_DOCUMENTENTRY_HEALTHCAREFACILITYCODE_OBJECTTYPE(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1']"
		    + "/@objectType"
		),

		R54_DOCUMENTENTRY_PRACTICESETTINGCODE_OBJECTTYPE(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead']"
		    + "/@objectType"
		),

		R59_DOCUMENTENTRY_TYPECODE_OBJECTTYPE(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:f0306f51-975f-434e-a61c-c59651d33983']"
		    + "/@objectType"
		),

		R72_DOCUMENTENTRY_PATIENTID_OBJECTTYPE(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427']"
		    + "/@objectType"
		),

		R79_DOCUMENTENTRY_UNIQUEID_OBJECTTYPE(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab']"
		    + "/@objectType"
		),

		R86_SUBMISSIONSET_OBJECTTYPE(
		    "//*[local-name()='RegistryPackage']"
		    + "/@objectType"
		),

		R90_SUBMISSIONSET_AUTHOR_OBJECTTYPE(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d']"
		    + "/@objectType"
		),

		R95_SUBMISSIONSET_CONTENTTYPECODE_OBJECTTYPE(
		    "//*[local-name()='Classification'"
		    + " and @classificationScheme='urn:uuid:aa543740-bdda-424e-8c96-df4873be8500']"
		    + "/@objectType"
		),

		R102_SUBMISSIONSET_PATIENTID_OBJECTTYPE(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446']"
		    + "/@objectType"
		),

		R108_SUBMISSIONSET_SOURCEID_OBJECTTYPE(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832']"
		    + "/@objectType"
		),

		R114_SUBMISSIONSET_UNIQUEID_OBJECTTYPE(
		    "//*[local-name()='ExternalIdentifier'"
		    + " and @identificationScheme='urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8']"
		    + "/@objectType"
		),

		R10_EXTRINSICOBJECT_OBJECTTYPE(
		    "//*[local-name()='ExtrinsicObject']"
		    + "/@objectType"
		),
		
	PIT14_SUBJECT_ID_VALUES(
	    "//*[local-name()='Attribute'"
	    + " and @Name='urn:oasis:names:tc:xacml:1.0:subject:subject-id']"
	    + "/*[local-name()='AttributeValue']"
	),

	PIT17_ACTION_ID_VALUES(
	    "//*[local-name()='Attribute'"
	    + " and @Name='urn:oasis:names:tc:xacml:1.0:action:action-id']"
	    + "/*[local-name()='AttributeValue']"
	),

	PIT19_LOCALITY_VALUES(
	    "//*[local-name()='Attribute'"
	    + " and @Name='urn:oasis:names:tc:xspa:1.0:environment:locality']"
	    + "/*[local-name()='AttributeValue']"
	),

	PIT22_TYPE_VALUES(
	    "//*[local-name()='Attribute'"
	    + " and @Name='urn:oasis:names:tc:xspa:1.0:resource:hl7:type']"
	    + "/*[local-name()='AttributeValue']"
	),

	PIT25_PURPOSE_OF_USE_VALUES(
	    "//*[local-name()='Attribute'"
	    + " and @Name='urn:oasis:names:tc:xspa:1.0:subject:purposeofuse']"
	    + "/*[local-name()='AttributeValue']"
	),

	PIT27_ORGANIZATION_VALUES(
	    "//*[local-name()='Attribute'"
	    + " and @Name='urn:oasis:names:tc:xspa:1.0:subject:organization']"
	    + "/*[local-name()='AttributeValue']"
	),

	PIT30_RESOURCE_ID_VALUES(
	    "//*[local-name()='Attribute'"
	    + " and @Name='urn:oasis:names:tc:xacml:1.0:resource:resource-id']"
	    + "/*[local-name()='AttributeValue']"
	),

	PIT32_CONSENT_VALUES(
	    "//*[local-name()='Attribute'"
	    + " and @Name='urn:oasis:names:tc:xspa:1.0:resource:patient:consent']"
	    + "/*[local-name()='AttributeValue']"
	),

	PIT36_ROLE_VALUES(
	    "//*[local-name()='Attribute'"
	    + " and @Name='urn:oasis:names:tc:xacml:2.0:subject:role']"
	    + "/*[local-name()='AttributeValue']"
	),

	PIT38_ORGANIZATION_ID_VALUES(
	    "//*[local-name()='Attribute'"
	    + " and @Name='urn:oasis:names:tc:xspa:1.0:subject:organization-id']"
	    + "/*[local-name()='AttributeValue']"
	),
	REPOSITORY_UNIQUE_ID(
	    "//*[local-name()='Slot' and @name='repositoryUniqueId']"
	    + "/*[local-name()='ValueList']"
	    + "/*[local-name()='Value']"
	),

	AUTHOR_PERSON(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d']"
	    + "/*[local-name()='Slot' and @name='authorPerson']"
	    + "/*[local-name()='ValueList']"
	    + "/*[local-name()='Value']"
	),

	CLASSCODE_CODING_SCHEME(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a']"
	    + "/*[local-name()='Slot' and @name='codingScheme']"
	    + "/*[local-name()='ValueList']"
	    + "/*[local-name()='Value']"
	),

	CLASSCODE(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a']"
	    + "/@nodeRepresentation"
	),

	CONFIDENTIALITY_CODING_SCHEME(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f']"
	    + "/*[local-name()='Slot' and @name='codingScheme']"
	    + "/*[local-name()='ValueList']"
	    + "/*[local-name()='Value']"
	),

	EVENTCODELIST_CODING_SCHEME(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4']"
	    + "/*[local-name()='Slot' and @name='codingScheme']"
	    + "/*[local-name()='ValueList']"
	    + "/*[local-name()='Value']"
	),

	FORMATCODE_CODING_SCHEME(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d']"
	    + "/*[local-name()='Slot' and @name='codingScheme']"
	    + "/*[local-name()='ValueList']"
	    + "/*[local-name()='Value']"
	),

	FORMATCODE(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d']"
	    + "/@nodeRepresentation"
	),

	HEALTHCAREFACILITY_CODING_SCHEME(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1']"
	    + "/*[local-name()='Slot' and @name='codingScheme']"
	    + "/*[local-name()='ValueList']"
	    + "/*[local-name()='Value']"
	),

	HEALTHCAREFACILITY_CODE(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1']"
	    + "/@nodeRepresentation"
	),

	PRACTICESETTING_CODING_SCHEME(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead']"
	    + "/*[local-name()='Slot' and @name='codingScheme']"
	    + "/*[local-name()='ValueList']"
	    + "/*[local-name()='Value']"
	),

	PRACTICESETTING_CODE(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead']"
	    + "/@nodeRepresentation"
	),

	TYPECODE(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:f0306f51-975f-434e-a61c-c59651d33983']"
	    + "/@nodeRepresentation"
	),

	UNIQUE_ID(
	    "//*[local-name()='ExternalIdentifier'"
	    + " and @identificationScheme='urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab']"
	    + "/@value"
	),

	CONTENTTYPE_CODING_SCHEME(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:aa543740-bdda-424e-8c96-df4873be8500']"
	    + "/*[local-name()='Slot' and @name='codingScheme']"
	    + "/*[local-name()='ValueList']"
	    + "/*[local-name()='Value']"
	),

	CONTENTTYPE_CODE(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:aa543740-bdda-424e-8c96-df4873be8500']"
	    + "/@nodeRepresentation"
	),

	SOURCE_ID(
	    "//*[local-name()='ExternalIdentifier'"
	    + " and @identificationScheme='urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832']"
	    + "/@value"
	),

	CODELIST(
	    "//*[local-name()='Classification'"
	    + " and @classificationScheme='urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4']"
	    + "/@nodeRepresentation"
	),

	HASH(
	    "//*[local-name()='Slot' and @name='hash']"
	    + "/*[local-name()='ValueList']"
	    + "/*[local-name()='Value']"
	),

	SIZE(
	    "//*[local-name()='Slot' and @name='size']"
	    + "/*[local-name()='ValueList']"
	    + "/*[local-name()='Value']"
	),
	SAML_SUBJECT("//*[local-name()='Subject']"),
	SAML_NAME_ID("//*[local-name()='NameID']"),
	SAML_SUBJECT_EVALUATE("string(//*[local-name()='Subject'])"),
		SAML_NAME_ID_EVALUATE(
		    "string(//*[local-name()='NameID'])"
		),
	SAML_ATTRIBUTE_STATEMENT(
		    "/S:Envelope/S:Header"
		    + "/wsse:Security"
		    + "/saml2:Assertion"
		    + "/saml2:AttributeStatement"
		),
	SAML_CONSENT(
		    "//*[local-name()='Attribute' "
		    + "and @Name='urn:oasis:names:tc:xspa:1.0:resource:patient:consent']"
		    + "/*[local-name()='AttributeValue']"
		),
	SAML_PURPOSE_OF_USE(
	    "//*[local-name()='Attribute' "
	    + "and @Name='urn:oasis:names:tc:xspa:1.0:subject:purposeofuse']"
	    + "/*[local-name()='AttributeValue']"
	),
	SOURCE_PATIENT_ID(
		    "/S:Envelope/S:Body" +
		    "/ns3:SubmitObjectsRequest" +
		    "/rim:RegistryObjectList" +
		    "/rim:ExtrinsicObject" +
		    "/rim:Slot[@name='sourcePatientId']" +
		    "/rim:ValueList" +
		    "/rim:Value"
		),
	SAML_RESOURCE_ID_EVALUATE(
		    "//*[local-name()='Attribute' "
		    + "and @Name='urn:oasis:names:tc:xacml:1.0:resource:resource-id']"
		    + "/*[local-name()='AttributeValue']"
		),
	SAML_RESOURCE_ID_1(
		    "//*[local-name()='Attribute' "
		    + "and @Name='urn:oasis:names:tc:xacml:1.0:resource:resource-id']"
		),

		SAML_RESOURCE_ID_2(
		    "//*[local-name()='Attribute' "
		    + "and @Name='urn:oasis:names:tc:xacml:1.0:resource:resource-id']"
		    + "/*"
		),

		SAML_RESOURCE_ID_3(
		    "//*[local-name()='Attribute' "
		    + "and @Name='urn:oasis:names:tc:xacml:1.0:resource:resource-id']"
		    + "/*[local-name()='AttributeValue']"
		),

		SAML_RESOURCE_ID_4(
		    "//saml2:Attribute[@Name='urn:oasis:names:tc:xacml:1.0:resource:resource-id']"
		    + "/saml2:AttributeValue"
		),

		SAML_RESOURCE_ID_5(
		    "//*[contains(@Name,'resource-id')]"
		    + "/*[local-name()='AttributeValue']"
		),

		SAML_RESOURCE_ID_6(
		    "//*[contains(@Name,'resource-id')]"
		),

		SAML_RESOURCE_ID_7(
		    "//*[local-name()='AttributeValue'"
		    + " and contains(text(),'ISO')]"
		),

		SAML_RESOURCE_ID_8(
		    "//*[local-name()='Attribute']"
		    + "[@Name='urn:oasis:names:tc:xacml:1.0:resource:resource-id']"
		    + "/node()"
		),

		SAML_RESOURCE_ID_9(
		    "//*[local-name()='AttributeValue'"
		    + " and ancestor::*[@Name='urn:oasis:names:tc:xacml:1.0:resource:resource-id']]"
		),

		SAML_RESOURCE_ID_10(
		    "//*[@Name='urn:oasis:names:tc:xacml:1.0:resource:resource-id']"
		    + "/*[local-name()='AttributeValue']"
		),

	SAML_RESOURCE_ID_MUTATE(
		    "//*[local-name()='Attribute' "
		    + "and @Name='urn:oasis:names:tc:xacml:1.0:resource:resource-id']"
		    + "/*[local-name()='AttributeValue']"
		),
	DOCUMENT_ENTRY_PATIENT_ID(
		    "/S:Envelope/S:Body" +
		    "/ns3:SubmitObjectsRequest" +
		    "/rim:RegistryObjectList" +
		    "/rim:ExtrinsicObject" +
		    "/rim:ExternalIdentifier[@id='patientId_1']/@value"
	),
	SUBMISSION_SET_PATIENT_ID(
	    "/S:Envelope/S:Body" +
	    "/ns3:SubmitObjectsRequest" +
	    "/rim:RegistryObjectList" +
	    "/rim:RegistryPackage" +
	    "/rim:ExternalIdentifier[@id='SubmissionSet1_PatientId']/@value"
	),
	SAML_ISSUER(
	    "/S:Envelope/S:Header" +
	    "/wsse:Security" +
	    "/saml2:Assertion" +
	    "/saml2:Issuer"
	);
    private final String expression;
}