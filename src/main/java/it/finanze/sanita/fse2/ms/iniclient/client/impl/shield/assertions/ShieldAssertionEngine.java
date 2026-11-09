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

import java.util.Map;

import javax.xml.soap.SOAPPart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.ShieldException;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.XDSFaultEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ShieldAssertionEngine {

	@Autowired
    private ApplicationContext applicationContext;

    /** Read-only checks shared by previews and the outbound SOAP handler. */
    public void validate(SOAPPart document) {
        for (XDSFaultEnum fault : XDSFaultEnum.values()) {
            evaluate(document, fault);
        }
    }

    public void evaluate(SOAPPart document, XDSFaultEnum fault) {
	    try {
	        String message = null;
	    	AssertionResultDto result = applicationContext.getBean(fault.getAssertion()).evaluate(document);
	    	if (result.getOutput()!=null && !result.getOutput()) {
	            message = fault.getMessage();
	            if (result.getMessageParameters()!=null) {
		            for (Map.Entry<String, String> entry : result.getMessageParameters().entrySet()) {
		                message = message.replace(
		                        entry.getKey(),
		                        entry.getValue()
		                );
		            }
	            }
	            throw new ShieldException(fault, message);
	    	}
		} catch (ShieldException e) {
		    log.warn("SHIELD ACTIVATED: {}", e.getFault() + ":" + e.getMessage());
		    throw e;
		} catch (Exception e) {
		    log.error("SHIELD MALFUNCTION", e);
		    throw new RuntimeException("Shield malfunction", e);
		}
    }
    
}
