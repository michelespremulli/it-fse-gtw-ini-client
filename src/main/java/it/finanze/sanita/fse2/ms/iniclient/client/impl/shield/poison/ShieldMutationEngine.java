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
package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison;

import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPPart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.XDSFaultEnum;

@Component
public class ShieldMutationEngine {

	@Autowired
    private ApplicationContext applicationContext;

    public List<SOAPPart> mutate(
            SOAPPart originalDocument,
            XDSFaultEnum fault)
            throws Exception {

        List<SOAPPart> result = new ArrayList<>();

        for (Class<? extends AbstractShieldPoison> poisonClass : fault.getMutations()) {
        	SOAPPart clonedDocument = applicationContext.getBean(poisonClass).mutate(originalDocument);
            result.add(clonedDocument);
        }

        return result;
    }
}