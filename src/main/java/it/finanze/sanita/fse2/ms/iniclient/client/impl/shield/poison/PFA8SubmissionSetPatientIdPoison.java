package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison;

import java.util.UUID;

import javax.xml.soap.SOAPPart;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.EnvelopeXPathEnum;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.XPathUtility;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PFA8SubmissionSetPatientIdPoison extends AbstractShieldPoison {

	@Autowired
    private final XPathUtility xPathUtility;

	@Override
	public SOAPPart mutate(SOAPPart document) throws XPathExpressionException {
		SOAPPart cloned = (SOAPPart) cloneSoapPart(document);
	    xPathUtility.injectText(cloned, EnvelopeXPathEnum.SUBMISSION_SET_PATIENT_ID, UUID.randomUUID().toString());
	    return cloned;
	}
		

}
