package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.EnvelopeXPathEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PFA8Assertion extends AbstractShieldAssertion {

	@Autowired
    private final XPathUtility xPathUtility;

	@Override
	public AssertionResultDto evaluate(SOAPPart document) throws XPathExpressionException {

	    String resourceId = xPathUtility.extract(document, EnvelopeXPathEnum.SAML_RESOURCE_ID_EVALUATE);
	    String sourcePatientId = xPathUtility.extract(document, EnvelopeXPathEnum.SOURCE_PATIENT_ID);
	    String dePatientId = xPathUtility.extract(document, EnvelopeXPathEnum.DOCUMENT_ENTRY_PATIENT_ID);
	    String ssPatientId = xPathUtility.extract(document, EnvelopeXPathEnum.SUBMISSION_SET_PATIENT_ID);

	    boolean output = true;
	    String errorMsg = null;

	    Set<String> values = new HashSet<>(
    	    Arrays.asList(
    	        resourceId,
    	        sourcePatientId,
    	        dePatientId,
    	        ssPatientId
    	    )
    	);

	    if (values.size() != 1) {
	        output = false;
	        errorMsg = String.format(
	                "resourceId=%s, sourcePatientId=%s, documentEntryPatientId=%s, submissionSetPatientId=%s",
	                resourceId,
	                sourcePatientId,
	                dePatientId,
	                ssPatientId
	        );
	    }

	    Map<String, String> msgs = new HashMap<>();

	    if (errorMsg != null) {
	        msgs.put("$ERROR$", errorMsg);
	    }

	    return AssertionResultDto.builder()
	            .messageParameters(msgs)
	            .output(output)
	            .build();
	}
		

}
