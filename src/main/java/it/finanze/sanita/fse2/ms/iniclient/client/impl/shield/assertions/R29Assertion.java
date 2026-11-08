package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions;

import javax.xml.soap.SOAPPart;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.EnvelopeXPathEnum;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class R29Assertion extends AbstractShieldAssertion {

    private final XPathUtility xPathUtility;

    @Override
    public AssertionResultDto evaluate(SOAPPart document) throws XPathExpressionException {
        String value = xPathUtility.extract(
                document, EnvelopeXPathEnum.R29_DOCUMENTENTRY_CLASSCODE_OBJECTTYPE);

        boolean output = "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification".equals(value);

        return AssertionResultDto.builder()
                .output(output)
                .messageParameters(null)
                .build();
    }
    
}