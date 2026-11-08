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
package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield;

import java.util.Date;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.ShieldAssertionEngine;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.XPathUtility;
import lombok.extern.slf4j.Slf4j;

/*
 * This simple SOAPHandler will check the contents of outgoing message to search for XDS violation.
 */
@Slf4j
public class SAMLShieldHandler implements SOAPHandler<SOAPMessageContext> {

    private ShieldAssertionEngine shieldAssertionEngine;
    
    public SAMLShieldHandler(ShieldAssertionEngine shieldAssertionEngine) {
    	this.shieldAssertionEngine = shieldAssertionEngine;
	}
    
	public Set<QName> getHeaders() { 
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		executeShieldChecks(smc);
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		executeShieldChecks(smc);
		return true;
	}
	
	public void close(MessageContext messageContext) {
	}

	private void executeShieldChecks(SOAPMessageContext smc) {

	    Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (Boolean.TRUE.equals(outboundProperty)) {
        	SOAPPart document = smc.getMessage().getSOAPPart();
        	Long delta = new Date().getTime();
            for (XDSFaultEnum fault : XDSFaultEnum.values()) {
            	shieldAssertionEngine.evaluate(document, fault);
            }
            delta = new Date().getTime() - delta;
            log.info("SHIELD PROCESSING TIME (ms): {}", delta);
        }
        
	}

}