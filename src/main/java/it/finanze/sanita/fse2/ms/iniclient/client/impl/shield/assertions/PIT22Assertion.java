package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions;

import javax.xml.soap.SOAPPart;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.stereotype.Component;
import org.w3c.dom.NodeList;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.EnvelopeXPathEnum;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PIT22Assertion extends AbstractShieldAssertion {

    private final XPathUtility xPathUtility;
    
    @Override
    public AssertionResultDto evaluate(SOAPPart document) throws XPathExpressionException {

        NodeList nodes = xPathUtility.extractNodes(
                document,
                EnvelopeXPathEnum.PIT22_TYPE_VALUES);

        boolean output = nodes != null
                && nodes.getLength() == 1;

        return AssertionResultDto.builder()
                .output(output)
                .messageParameters(null)
                .build();
    }
}