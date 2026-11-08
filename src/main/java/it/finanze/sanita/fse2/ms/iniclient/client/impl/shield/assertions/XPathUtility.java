/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.EnvelopeXPathEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class XPathUtility {

    private static final XPathFactory FACTORY = XPathFactory.newInstance();

    private XPath buildXPath() {

        XPath xpath = FACTORY.newXPath();

        xpath.setNamespaceContext(NAMESPACE_CONTEXT);

        return xpath;
    }

    public String extract(Node node, String exp) throws XPathExpressionException {
        return buildXPath().evaluate(exp, node).trim();
    }

    public String extract(Node node, EnvelopeXPathEnum xPath) throws XPathExpressionException {
        return buildXPath().evaluate(xPath.getExpression(), node).trim();
    }

    private static final NamespaceContext NAMESPACE_CONTEXT =
            new NamespaceContext() {
	
	    	@Override
	    	public String getNamespaceURI(String prefix) {
	    	    return switch (prefix) {
	    	        case "S" ->
	    	            "http://www.w3.org/2003/05/soap-envelope";
	    	        case "wsse" ->
	    	            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	    	        case "saml2" ->
	    	            "urn:oasis:names:tc:SAML:2.0:assertion";
	    	        case "ns3" ->
	    	            "urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0";
	    	        case "rim" ->
	    	            "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0";
	    	        default ->
	    	            XMLConstants.NULL_NS_URI;
	    	    };
	    	}

                @Override
                public String getPrefix(String namespaceURI) {
                    return null;
                }

                @Override
                public Iterator<String> getPrefixes(String namespaceURI) {
                    return Collections.emptyIterator();
                }
            };
	
	public void injectNode(
	        Node node,
	        EnvelopeXPathEnum xpathEnum,
	        String value)
	        throws XPathExpressionException {
	
	    Node target = (Node) buildXPath().evaluate(
	            xpathEnum.getExpression(),
	            node,
	            XPathConstants.NODE);

	    if (target == null) {
	        throw new IllegalArgumentException(
	                "XPath not found: " + xpathEnum.getExpression());
	    }
	
	    target.setNodeValue(value);
	}

	public void injectText(
	        Node node,
	        EnvelopeXPathEnum xpathEnum,
	        String value)
	        throws XPathExpressionException {
	
	    Node target = (Node) buildXPath().evaluate(
	            xpathEnum.getExpression(),
	            node,
	            XPathConstants.NODE);

	    if (target == null) {
	        throw new IllegalArgumentException(
	                "XPath not found: " + xpathEnum.getExpression());
	    }
	
	    target.setTextContent(value);
	}

    
	public void remove(
	        Node document,
	        EnvelopeXPathEnum xpath)
	        throws XPathExpressionException {

	    Node target = (Node) buildXPath().evaluate(
	            xpath.getExpression(),
	            document,
	            XPathConstants.NODE);

	    if (target instanceof Attr attr) {
	        attr.getOwnerElement().removeAttributeNode(attr);
	    } else if (target != null) {
	        target.getParentNode().removeChild(target);
	    }
	}
	
	
	public static void dump(SOAPPart document) {

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

	public NodeList extractNodes(
	        Node node,
	        EnvelopeXPathEnum xpathEnum)
	        throws XPathExpressionException {

	    return (NodeList) buildXPath().evaluate(
	            xpathEnum.getExpression(),
	            node,
	            XPathConstants.NODESET);
	}
	
	public void duplicate(
	        Node node,
	        EnvelopeXPathEnum xpathEnum)
	        throws XPathExpressionException {

	    Node target = (Node) buildXPath().evaluate(
	            xpathEnum.getExpression(),
	            node,
	            XPathConstants.NODE);

	    if (target == null) {
	        throw new IllegalArgumentException(
	                "XPath not found: " + xpathEnum.getExpression());
	    }

	    Node clone = target.cloneNode(true);

	    target.getParentNode().appendChild(clone);
	}
	
}