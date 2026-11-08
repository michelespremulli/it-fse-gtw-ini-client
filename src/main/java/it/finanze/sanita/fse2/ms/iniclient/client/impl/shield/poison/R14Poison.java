package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison;

import javax.xml.soap.SOAPPart;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.EnvelopeXPathEnum;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.XPathUtility;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class R14Poison extends AbstractShieldPoison {

    private final XPathUtility xPathUtility;

    @Override
    public SOAPPart mutate(SOAPPart document)
            throws XPathExpressionException {

        SOAPPart cloned =
                (SOAPPart) cloneSoapPart(document);

        xPathUtility.injectText(
                cloned,
                EnvelopeXPathEnum.R14_SLOT_VALUE,
                "");

        return cloned;
    }
}
