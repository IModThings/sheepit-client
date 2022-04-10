package com.sheepit.client.datamodel;

import lombok.Data;
import lombok.ToString;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(strict = false, name = "result") @Data @ToString public class SpeedTestTargetResult {
	
	@Attribute private String target;
	
	@Attribute private Long speed;
	
	@Attribute private Integer ping;
	
	public SpeedTestTargetResult() {
	}
}
