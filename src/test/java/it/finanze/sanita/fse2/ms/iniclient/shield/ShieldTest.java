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
package it.finanze.sanita.fse2.ms.iniclient.shield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.List;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mongodb.client.MongoClient;

import it.finanze.sanita.fse2.ms.iniclient.AbstractTest;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.IniClient;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.ShieldException;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.XDSFaultEnum;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.XDSFaultTypeEnum;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT14Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT17Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT19Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT22Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT25Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT27Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT30Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT32Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT36Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions.PIT38Assertion;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT14Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT17Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT19Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT22Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT25Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT27Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT30Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT32Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT36Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.PIT38Poison;
import it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.poison.ShieldMutationEngine;
import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.config.GovwayCfg;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.config.mongo.MongoDatabaseCFG;
import it.finanze.sanita.fse2.ms.iniclient.config.mongo.MongoPropertiesCFG;
import it.finanze.sanita.fse2.ms.iniclient.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.ISecuritySRV;
import it.finanze.sanita.fse2.ms.iniclient.service.impl.AuditIniSrv;
import it.finanze.sanita.fse2.ms.iniclient.utility.common.SamlHeaderBuilderUtility;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(
	    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	    properties = {
	        "spring.autoconfigure.exclude=" +
	        "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration," +
	        "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
	        "spring.data.mongodb.uri=mongodb://test:test@localhost:27017/test",
	        "ini.client.document-registry-service=https://fseservicetest.sanita.finanze.it/FSEInteroperabilitaWeb/DocumentRegistry_Service",
	        "audit-ini-enabled=false",
	        "otel.sdk.disabled=true",
	        "otel.traces.exporter=none",
	        "otel.metrics.exporter=none",
	        "otel.logs.exporter=none"
	    }
	)
	@ActiveProfiles(Constants.Profile.TEST)
	class ShieldTest extends AbstractTest {

	    @MockitoBean
	    private MongoClient mongoClient;

	    @MockitoBean
	    private MongoTemplate mongoTemplate;

	    @MockitoBean
	    private MongoDatabaseCFG mongoDatabaseCFG;

	    @MockitoBean
	    private MongoPropertiesCFG mongoPropertiesCFG;

	    @MockitoBean
	    private IniCFG iniCFG;

	    @MockitoBean
	    private SamlHeaderBuilderUtility samlHeaderBuilderUtility;

	    @MockitoBean
	    private ISecuritySRV securitySRV;

	    @MockitoBean
	    private AuditIniSrv auditIniSrv;

	    @MockitoBean
	    private IConfigSRV configSRV;

	    @MockitoBean
	    private GovwayCfg govwayCfg;

	    @Autowired
	    private ShieldMutationEngine sme;

	    @Autowired
	    private IniClient client;

	    @BeforeEach
	    void setup() {
	        Mockito.when(iniCFG.isEnableSSL()).thenReturn(Boolean.FALSE);
	        Mockito.when(govwayCfg.getGovwayUrl()).thenReturn(null);
	    }
	    
	    @Test
	    @DisplayName("happyPath")
	    void happyPath() throws JsonMappingException, JsonProcessingException {

	        SOAPPart doc = getOriginalDocument();

	        RuntimeException ex = assertThrows(
	                RuntimeException.class,
	                () -> client.sendRawData(doc)
	        );

	        assertTrue(
	                ExceptionUtils.indexOfThrowable(
	                        ex,
	                        java.net.ConnectException.class) >= 0
	        );
	    }
	    


    @Test
    @DisplayName("PFA15 Attack")
    void pfa15Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PFA15);
        checkShield(poisonedClones, XDSFaultEnum.PFA15);
    }
	    
    @Test
    @DisplayName("PFA8 Attack")
    void pfa8Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PFA8);
        checkShield(poisonedClones, XDSFaultEnum.PFA8);
    }

    @Test
    @DisplayName("PIT1 Attack")
    void pit1Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT1);
        checkShield(poisonedClones, XDSFaultEnum.PIT1);
    }

    @Test
    @DisplayName("PIT3 Attack")
    void pit3Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT3);
        checkShield(poisonedClones, XDSFaultEnum.PIT3);
    }

    @Test
    @DisplayName("PIT4 Attack")
    void pit4Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT4);
        checkShield(poisonedClones, XDSFaultEnum.PIT4);
    }
    
    @Test
    @DisplayName("R163 Attack")
    void r163Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R163);
        checkShield(poisonedClones, XDSFaultEnum.R163);
    }

    @Test
    @DisplayName("R167 Attack")
    void r167Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R167);
        checkShield(poisonedClones, XDSFaultEnum.R167);
    }
    
    @Test
    @DisplayName("R170 Attack")
    void r170Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R170);
        checkShield(poisonedClones, XDSFaultEnum.R170);
    }
    
    @Test
    @DisplayName("R175 Attack")
    void r175Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R175);
        checkShield(poisonedClones, XDSFaultEnum.R175);
    }

    @Test
    @DisplayName("R185 Attack")
    void r185Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R185);
        checkShield(poisonedClones, XDSFaultEnum.R185);
    }
    
    @Test
    @DisplayName("R186 Attack")
    void r186Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R186);
        checkShield(poisonedClones, XDSFaultEnum.R186);
    }
    
    @Test
    @DisplayName("R187 Attack")
    void r187Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R187);
        checkShield(poisonedClones, XDSFaultEnum.R187);
    }
    
    @Test
    @DisplayName("R189 Attack")
    void r189Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R189);
        checkShield(poisonedClones, XDSFaultEnum.R189);
    }

    @Test
    @DisplayName("R191 Attack")
    void r191Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R191);
        checkShield(poisonedClones, XDSFaultEnum.R191);
    }
    
    @Test
    @DisplayName("R192 Attack")
    void r192Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R192);
        checkShield(poisonedClones, XDSFaultEnum.R192);
    }
    
    @Test
    @DisplayName("R193 Attack")
    void r193Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT4);
        checkShield(poisonedClones, XDSFaultEnum.PIT4);
    }
    
    @Test
    @DisplayName("R194 Attack")
    void r194Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R193);
        checkShield(poisonedClones, XDSFaultEnum.R193);
    }
    
    @Test
    @DisplayName("R195 Attack")
    void r195Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R195);
        checkShield(poisonedClones, XDSFaultEnum.R195);
    }
    @Test
    @DisplayName("R196 Attack")
    void r196Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R196);
        checkShield(poisonedClones, XDSFaultEnum.R196);
    }
    @Test
    @DisplayName("R197 Attack")
    void r197Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R197);
        checkShield(poisonedClones, XDSFaultEnum.R197);
    }
    @Test
    @DisplayName("R200 Attack")
    void r200Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R200);
        checkShield(poisonedClones, XDSFaultEnum.R200);
    }
    @Test
    @DisplayName("R204 Attack")
    void r204Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R204);
        checkShield(poisonedClones, XDSFaultEnum.R204);
    }
    
    @Test
    @DisplayName("R205 Attack")
    void r205Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R205);
        checkShield(poisonedClones, XDSFaultEnum.R205);
    }
    @Test
    @DisplayName("R206 Attack")
    void r206Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R206);
        checkShield(poisonedClones, XDSFaultEnum.R206);
    }

    @Test
    @DisplayName("R210 Attack")
    void r210Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R210);
        checkShield(poisonedClones, XDSFaultEnum.R210);
    }
	
    @Test
    @DisplayName("PIT14 Attack")
    void pit14Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT14);
        checkShield(poisonedClones, XDSFaultEnum.PIT14);
    }

    @Test
    @DisplayName("PIT17 Attack")
    void pit17Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT17);
        checkShield(poisonedClones, XDSFaultEnum.PIT17);
    }

    @Test
    @DisplayName("PIT19 Attack")
    void pit19Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT19);
        checkShield(poisonedClones, XDSFaultEnum.PIT19);
    }

    @Test
    @DisplayName("PIT22 Attack")
    void pit22Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT22);
        checkShield(poisonedClones, XDSFaultEnum.PIT22);
    }

    @Test
    @DisplayName("PIT25 Attack")
    void pit25Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT25);
        checkShield(poisonedClones, XDSFaultEnum.PIT25);
    }

    @Test
    @DisplayName("PIT27 Attack")
    void pit27Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT27);
        checkShield(poisonedClones, XDSFaultEnum.PIT27);
    }

    @Test
    @DisplayName("PIT30 Attack")
    void pit30Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT30);
        checkShield(poisonedClones, XDSFaultEnum.PIT30);
    }

    @Test
    @DisplayName("PIT32 Attack")
    void pit32Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT32);
        checkShield(poisonedClones, XDSFaultEnum.PIT32);
    }

    @Test
    @DisplayName("PIT36 Attack")
    void pit36Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT36);
        checkShield(poisonedClones, XDSFaultEnum.PIT36);
    }

    @Test
    @DisplayName("PIT38 Attack")
    void pit38Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.PIT38);
        checkShield(poisonedClones, XDSFaultEnum.PIT38);
    }
    
    @Test
    @DisplayName("PIT49 Attack")
    void r49Attack() throws Exception {
        SOAPPart original = getOriginalDocument();
        List<SOAPPart> poisonedClones = sme.mutate(original, XDSFaultEnum.R49);
        checkShield(poisonedClones, XDSFaultEnum.R49);
    }
    
    @Test
    @DisplayName("Open Fire")
    void openfire() throws Exception {

    	Integer counter = 0;
        SOAPPart original = getOriginalDocument();
        for (XDSFaultEnum fault : XDSFaultEnum.values()) {

            List<SOAPPart> poisonedClones = sme.mutate(original, fault);

        	counter += checkShield(poisonedClones, fault);

        }
        
        log.info("FAULT FIRED: " + XDSFaultEnum.values().length + " FAULT REFLECTED: " + counter);
    }

	private Integer checkShield(List<SOAPPart> poisonedClones, XDSFaultEnum fault) {
        Integer counter = 0;

		for (SOAPPart poisonedClone : poisonedClones) {
			counter++;
            log.info("=============================");
            log.info("Checking response to attack: " + fault);
            Exception ex = assertThrows(
                    Exception.class,
                    () -> client.sendRawData(poisonedClone)
            );

            for (Object o : ExceptionUtils.getThrowableList(ex)) {
            	Throwable t = (Throwable) o;
            	log.info(
                        t.getClass().getName()
                        + " -> "
                        + t.getMessage());
            }
            ShieldException shieldException = (ShieldException)
                    ExceptionUtils.getThrowableList(ex)
                            .stream()
                            .filter(ShieldException.class::isInstance)
                            .map(ShieldException.class::cast)
                            .findFirst()
                            .orElseThrow();

            assertEquals(fault, shieldException.getFault());
            log.info("Shield block attack: " + shieldException.getFault());
            log.info("=============================");
        }
		return counter;
	}
    
    private SOAPPart getOriginalDocument() {

        try (InputStream is =
                     getClass().getResourceAsStream("/Files/soap-request.xml")) {

            MessageFactory factory =
                    MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

            SOAPMessage soapMessage =
                    factory.createMessage(null, is);

            return soapMessage.getSOAPPart();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
