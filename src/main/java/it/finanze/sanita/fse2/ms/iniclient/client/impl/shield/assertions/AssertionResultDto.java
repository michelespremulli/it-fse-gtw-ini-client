package it.finanze.sanita.fse2.ms.iniclient.client.impl.shield.assertions;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssertionResultDto {

	private Map<String, String> messageParameters;
	private Boolean output;
	
}
