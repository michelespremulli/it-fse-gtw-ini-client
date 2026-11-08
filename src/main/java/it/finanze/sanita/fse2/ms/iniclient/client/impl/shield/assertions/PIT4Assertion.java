package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions;

import javax.xml.soap.SOAPPart;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.EnvelopeXPathEnum;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PIT4Assertion extends AbstractShieldAssertion {

    private final XPathUtility xPathUtility;

    @Override
    public AssertionResultDto evaluate(SOAPPart document) throws XPathExpressionException {

    	String subject = xPathUtility.extract(document, EnvelopeXPathEnum.SAML_SUBJECT_EVALUATE);
		String nameId = xPathUtility.extract(document, EnvelopeXPathEnum.SAML_NAME_ID_EVALUATE);

		boolean output = true;

		if (subject == null || subject.isBlank() || nameId == null || nameId.isBlank()) {
            output = false;
        }

        return AssertionResultDto.builder()
                .output(output)
                .messageParameters(null)
                .build();
    }
    
}