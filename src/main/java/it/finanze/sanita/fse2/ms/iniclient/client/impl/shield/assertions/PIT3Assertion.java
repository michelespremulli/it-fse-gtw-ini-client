package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions;

import javax.xml.soap.SOAPPart;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.EnvelopeXPathEnum;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PIT3Assertion extends AbstractShieldAssertion {

    private final XPathUtility xPathUtility;

    @Override
    public AssertionResultDto evaluate(SOAPPart document)
            throws XPathExpressionException {

        String consent = xPathUtility.extract(
                document,
                EnvelopeXPathEnum.SAML_CONSENT);

        String purposeOfUse = xPathUtility.extract(
                document,
                EnvelopeXPathEnum.SAML_PURPOSE_OF_USE);

        boolean output = true;

        if ("TREATMENT".equalsIgnoreCase(purposeOfUse)
                && "false".equalsIgnoreCase(consent)) {

            output = false;
        }

        return AssertionResultDto.builder()
                .output(output)
                .messageParameters(null)
                .build();
    }
}