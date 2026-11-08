package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractShieldPoison {

	public abstract SOAPPart mutate(SOAPPart document) throws XPathExpressionException;
	
	protected SOAPPart cloneSoapPart(SOAPPart original) {

	    try {

	        ByteArrayOutputStream bos = new ByteArrayOutputStream();

	        TransformerFactory.newInstance()
	                .newTransformer()
	                .transform(
	                        new DOMSource(original),
	                        new StreamResult(bos));

	        MessageFactory factory =
	                MessageFactory.newInstance(
	                        SOAPConstants.SOAP_1_2_PROTOCOL);

	        SOAPMessage message =
	                factory.createMessage(
	                        null,
	                        new ByteArrayInputStream(
	                                bos.toByteArray()));

	        return message.getSOAPPart();

	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
}
