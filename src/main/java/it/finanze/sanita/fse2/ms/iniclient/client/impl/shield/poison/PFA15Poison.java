package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison;

import javax.xml.soap.SOAPPart;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.EnvelopeXPathEnum;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.XPathUtility;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PFA15Poison extends AbstractShieldPoison {

    private final XPathUtility xPathUtility;

    @Override
    public SOAPPart mutate(SOAPPart document)
            throws XPathExpressionException {

        SOAPPart cloned =
                (SOAPPart) cloneSoapPart(document);

        xPathUtility.remove(
                cloned,
                EnvelopeXPathEnum.PFA15_REPOSITORY_UNIQUE_ID);

        return cloned;
    }
}
