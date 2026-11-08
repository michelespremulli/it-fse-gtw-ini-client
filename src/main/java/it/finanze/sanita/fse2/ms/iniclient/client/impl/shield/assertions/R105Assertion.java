package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions;

import javax.xml.soap.SOAPPart;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.EnvelopeXPathEnum;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class R105Assertion extends AbstractShieldAssertion {

    private final XPathUtility xPathUtility;

    @Override
    public AssertionResultDto evaluate(SOAPPart document) throws XPathExpressionException {
        String value = xPathUtility.extract(
                document, EnvelopeXPathEnum.R105_SUBMISSIONSET_SOURCEID_DISPLAY_NAME);

        boolean output = "XDSSubmissionSet.sourceId".equals(value);

        return AssertionResultDto.builder()
                .output(output)
                .messageParameters(null)
                .build();
    }
    
}