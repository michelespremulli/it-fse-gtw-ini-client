package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions;

import javax.xml.soap.SOAPPart;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.EnvelopeXPathEnum;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class R26Assertion extends AbstractShieldAssertion {

    private final XPathUtility xPathUtility;

    @Override
    public AssertionResultDto evaluate(SOAPPart document)
            throws XPathExpressionException {

        String value = xPathUtility.extract(
                document,
                EnvelopeXPathEnum.R26_CLASSCODE_DISPLAY_NAME);

        return AssertionResultDto.builder()
                .output(
                        value != null
                        && !value.isBlank())
                .messageParameters(null)
                .build();
    }
    
}