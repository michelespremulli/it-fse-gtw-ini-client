package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield;

import java.util.List;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.AbstractShieldAssertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PFA15Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PFA8Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT14Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT17Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT19Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT1Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT22Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT25Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT27Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT30Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT32Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT36Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT38Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT3Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT4Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT7Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R102Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R105Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R108Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R10Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R111Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R114Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R14Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R163Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R167Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R170Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R175Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R185Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R186Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R187Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R189Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R191Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R192Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R193Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R194Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R195Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R196Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R197Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R200Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R204Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R205Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R206Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R210Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R23Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R26Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R29Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R31Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R34Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R36Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R39Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R41Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R44Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R46Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R49Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R51Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R54Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R56Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R59Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R69Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R72Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R76Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R79Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R86Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R8Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R90Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R92Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R95Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.R99Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.AbstractShieldPoison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PFA15Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PFA8DocumentEntryPatientIdPoison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PFA8ResourceIdPoison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PFA8SourcePatientIdPoison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PFA8SubmissionSetPatientIdPoison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT14Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT17Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT19Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT1IssuerPoison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT22Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT25Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT27Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT30Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT32Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT36Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT38Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT3ConsentPoison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT4NameIDPoison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT4SubjectPoison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT7Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R102Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R105Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R108Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R10Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R111Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R114Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R14Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R163Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R167Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R170Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R175Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R185Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R186Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R187Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R189Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R191Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R192Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R193Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R194Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R195Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R196Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R197Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R200Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R204Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R205Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R206Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R210Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R23Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R26Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R29Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R31Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R34Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R36Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R39Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R41Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R44Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R46Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R49Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R51Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R54Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R56Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R59Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R69Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R72Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R76Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R79Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R86Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R8Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R90Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R92Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R95Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.R99Poison;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum XDSFaultEnum {

	PFA8(
	    XDSFaultTypeEnum.FAILED_AUTHENTICATION,
	    "Mismatch between patient id in header and body message: $ERROR$",
	    PFA8Assertion.class,
	    List.of(
            PFA8ResourceIdPoison.class,
            PFA8SourcePatientIdPoison.class,
            PFA8DocumentEntryPatientIdPoison.class,
            PFA8SubmissionSetPatientIdPoison.class
		)
	), 		
	PIT1(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "Missing Issuer in assertion",
	    PIT1Assertion.class,
	    List.of(
    		PIT1IssuerPoison.class
	)),
	PIT3(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "Patient consent cannot be false in case of purpose of use TREATMENT",
	    PIT3Assertion.class,
	    List.of(
    		PIT3ConsentPoison.class
	)),
	PIT4(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "Missing Subject or NameID in assertion",
	    PIT4Assertion.class,
	    List.of(
    		PIT4NameIDPoison.class,
    		PIT4SubjectPoison.class
    		)
		),
	R167(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of repositoryUniqueId: it is empty",
	    R167Assertion.class,
	    List.of(
	    	R167Poison.class
	)),
	R175(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of authorPerson: it is empty",
	    R175Assertion.class,
	    List.of(
    		R175Poison.class
	)),
	R185(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of the coding scheme of classCode: it is empty",
	    R185Assertion.class,
	    List.of(
    		R185Poison.class
	)),	
	R186(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of classCode: it is empty",
	    R186Assertion.class,
	    List.of(
    		R186Poison.class
	)),	
	R187(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of the coding scheme of confidentialityCode: it is empty",
	    R187Assertion.class,
	    List.of(
    		R187Poison.class
	)),	
	R189(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of the coding scheme of eventCodeList: it is empty",
	    R189Assertion.class,
	    List.of(
    		R189Poison.class
	)),
	R191(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of the coding scheme of formatCode: it is empty",
	    R191Assertion.class,
	    List.of(
    		R191Poison.class
	)),
	R192(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of formatCode: it is empty",
	    R192Assertion.class,
	    List.of(
    		R192Poison.class
	)),	
	R193(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of the coding scheme of healthcareFacilityCode: it is empty",
	    R193Assertion.class,
	    List.of(
    		R193Poison.class
	)),
	R194(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of healthcareFacilityCode: it is empty",
		R194Assertion.class,
	    List.of(
    		R194Poison.class
	)),
	R195(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of the coding scheme of practiceSettingCode: it is empty",
	    R195Assertion.class,
	    List.of(
    		R195Poison.class
	)),
	R196(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of practiceSettingCode: it is empty",
	    R196Assertion.class,
	    List.of(
    		R196Poison.class
	)),	
	R197(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of DocumentEntry.typeCode: it is empty",
	    R197Assertion.class,
	    List.of(
    		R197Poison.class
	)),
	R200(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of uniqueId: it is empty",
	    R200Assertion.class,
	    List.of(
    		R200Poison.class
	)),
	R205(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of contentTypeCode: it is empty",
	    R205Assertion.class,
	    List.of(
    		R205Poison.class
	)),	
	R206(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of sourceId: it is empty",
	    R206Assertion.class,
	    List.of(
    		R206Poison.class
	)),	
	R210(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of codeList: it is empty",
	    R210Assertion.class,
	    List.of(
    		R210Poison.class
	)),	
	R204(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of the coding scheme of contentTypeCode: it is empty",
	    R204Assertion.class,
	    List.of(
    		R204Poison.class
	)),
	R163(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of hash: it is empty, or length greater than 256 characters",
	    R163Assertion.class,
	    List.of(
    		R163Poison.class
	)),	
	R170(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong value of size: it is empty",
	    R170Assertion.class,
	    List.of(
	    	R170Poison.class
	)),
	PIT14(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "The number of $SUBJECTIDURN$ attribute values must be 1",
	    PIT14Assertion.class,
	    List.of(
	    	PIT14Poison.class
	)),
	PIT17(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "The number of $ACTIONIDURN$ attribute values must be 1",
	    PIT17Assertion.class,
	    List.of(
	    	PIT17Poison.class
	)),
	PIT19(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "The number of $LOCALITYURN$ attribute values must be 1",
	    PIT19Assertion.class,
	    List.of(
	    	PIT19Poison.class
	)),
	PIT22(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "The number of $TYPEURN$ attribute values must be 1",
	    PIT22Assertion.class,
	    List.of(
	    	PIT22Poison.class
	)),
	PIT25(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "The number of $PURPOSEOFUSEURN$ attribute values must be 1",
	    PIT25Assertion.class,
	    List.of(
	    	PIT25Poison.class
	)),
	PIT27(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "The number of $ORGANIZATIONURN$ attribute values must be 1",
	    PIT27Assertion.class,
	    List.of(
	    	PIT27Poison.class
	)),
	PIT30(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "The number of $RESOURCEIDURN$ attribute values must be 1",
	    PIT30Assertion.class,
	    List.of(
	    	PIT30Poison.class
	)),
	PIT32(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "The number of $CONSENTURN$ attribute values must be 1",
	    PIT32Assertion.class,
	    List.of(
	    	PIT32Poison.class
	)),
	PIT36(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "The number of $ROLEURN$ attribute values must be 1",
	    PIT36Assertion.class,
	    List.of(
	    	PIT36Poison.class
	)),
	PIT38(
	    XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
	    "The number of $ORGANIZATIONIDURN$ attribute values must be 1",
	    PIT38Assertion.class,
	    List.of(
	    	PIT38Poison.class
	)),
	R51(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong format data of display name for DocumentEntry.practiceSettingCode",
	    R51Assertion.class,
	    List.of(
	    	R51Poison.class
	)),

	R56(
			XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
			"Wrong format data of display name for DocumentEntry.typeCode",
			R56Assertion.class,
		    List.of(
	    		R56Poison.class
		    )),
	R69(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong format data of display name for DocumentEntry.patientId",
	    R69Assertion.class,
	    List.of(
	    	R69Poison.class
	)),
	R99(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong format data of display name for SubmissionSet.patientId",
	    R99Assertion.class,
	    List.of(
	    	R99Poison.class
	)),
	R31(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong format data of display name for DocumentEntry.confidentialityCode",
	    R31Assertion.class,
	    List.of(
	    	R31Poison.class
	)),
	R26(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong format data of display name for DocumentEntry.classCode",
	    R26Assertion.class,
	    List.of(
	    	R26Poison.class
	)),
	R36(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong format data of display name for DocumentEntry.eventCodeList",
	    R36Assertion.class,
	    List.of(
	    	R36Poison.class
	)),
	R41(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong format data of display name for DocumentEntry.formatCode",
	    R41Assertion.class,
	    List.of(
	    	R41Poison.class
	)),
	R46(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong format data of display name for DocumentEntry.healthcareFacilityCode",
	    R46Assertion.class,
	    List.of(
	    	R46Poison.class
	    		)),
	R76(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong format data of display name for DocumentEntry.uniqueId",
	    R76Assertion.class,
	    List.of(
	    	R76Poison.class
	)),
	R92(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong format data of display name for SubmissionSet.contentTypeCode",
	    R92Assertion.class,
	    List.of(
	    	R92Poison.class
	)),
	R105(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong format data of display name for SubmissionSet.sourceId",
	    R105Assertion.class,
	    List.of(
	    	R105Poison.class
	)),
	R111(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong format data of display name for SubmissionSet.uniqueId",
	    R111Assertion.class,
	    List.of(
    		R111Poison.class
	))
	,
	R49(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification DocumentEntry.healthcareFacilityCode",
	    R49Assertion.class,
	    List.of(
    		R49Poison.class
	))
	,
		R54(
			    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
			    "Wrong object type of classification DocumentEntry.practiceSettingCode",
			    R54Assertion.class,
			    List.of(
			    	R54Poison.class
			)),
	R59(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification DocumentEntry.typeCode",
	    R59Assertion.class,
	    List.of(
	    	R59Poison.class
	))	    ,
	R79(
		    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
		    "Wrong object type of classification DocumentEntry.uniqueId",
		    R79Assertion.class,
		    List.of(
		    	R79Poison.class
		)),
	R86(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of submission set",
	    R86Assertion.class,
	    List.of(
	    	R86Poison.class
	)),
	R95(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification SubmissionSet.contentTypeCode",
	    R95Assertion.class,
	    List.of(
	    	R95Poison.class
	)),
	R34(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification DocumentEntry.confidentialityCode",
	    R34Assertion.class,
	    List.of(
	    	R34Poison.class
	)),
	R29(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification DocumentEntry.classCode",
	    R29Assertion.class,
	    List.of(
	    	R29Poison.class
	)),
	R8(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type value of the classification related to the association: $association_ID$",
	    R8Assertion.class,
	    List.of(
	    	R8Poison.class
	)),
	R10(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of extrinsic object",
	    R10Assertion.class,
	    List.of(
	    	R10Poison.class
	)),
	R23(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification DocumentEntry.author",
	    R23Assertion.class,
	    List.of(
	    	R23Poison.class
	)),
	
	R39(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification DocumentEntry.eventCodeList",
	    R39Assertion.class,
	    List.of(
	    	R39Poison.class
	)),
	R44(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification DocumentEntry.formatCode",
	    R44Assertion.class,
	    List.of(
	    	R44Poison.class
	)),
	R72(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification DocumentEntry.patientId",
	    R72Assertion.class,
	    List.of(
	    	R72Poison.class
		)),
	R90(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification SubmissionSet.author",
	    R90Assertion.class,
	    List.of(
	    	R90Poison.class
	)),
	R102(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification SubmissionSet.patientId",
	    R102Assertion.class,
	    List.of(
	    	R102Poison.class
	)),
	R108(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification SubmissionSet.sourceId",
	    R108Assertion.class,
	    List.of(
	    	R108Poison.class
	)),
	R114(
	    XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
	    "Wrong object type of classification SubmissionSet.uniqueId",
	    R114Assertion.class,
	    List.of(
	    	R114Poison.class
	)),
    R14(
            XDSFaultTypeEnum.XDS_REGISTRY_ERROR_REGISTER,
            "Missing value for slot $SLOT_NAME$",
    	    R14Assertion.class,
    	    List.of(
    	    	R14Poison.class
    	)),
    PIT7(
            XDSFaultTypeEnum.INVALID_SECURITY_TOKEN,
            "Missing AuthnStatement in assertion",
    	    PIT7Assertion.class,
    	    List.of(
    	    	PIT7Poison.class
    	)),
    PFA15(
            XDSFaultTypeEnum.FAILED_AUTHENTICATION,
            "Missing RepositoryUniqueId element",
    	    PFA15Assertion.class,
    	    List.of(
    	    	PFA15Poison.class
    	))
    ;
	

	private final XDSFaultTypeEnum fault;
    private final String message;
    private final Class<? extends AbstractShieldAssertion> assertion;
    private final List<Class<? extends AbstractShieldPoison>> mutations;

}