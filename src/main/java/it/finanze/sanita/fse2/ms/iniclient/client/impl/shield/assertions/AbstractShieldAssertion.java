package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions;

import java.io.StringWriter;

import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractShieldAssertion {

	public abstract AssertionResultDto evaluate(SOAPPart document) throws XPathExpressionException;
	
	protected void dump(SOAPPart document) {

	    try {
	        Transformer transformer =
	                TransformerFactory.newInstance().newTransformer();

	        StringWriter writer = new StringWriter();

	        transformer.transform(
	                new DOMSource(document),
	                new StreamResult(writer));

	        log.info("\n{}", writer);

	    } catch (Exception e) {
	        throw new RuntimeException(
	                "Unable to dump SOAP document",
	                e);
	    }
	}
	
}
