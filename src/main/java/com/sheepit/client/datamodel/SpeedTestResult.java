package com.sheepit.client.datamodel;


import lombok.Data;
import lombok.ToString;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false, name = "speedtest") @Data @ToString public class SpeedTestResult {
	
	@ElementList(inline = true) private List<SpeedTestTargetResult> results;
	
	public SpeedTestResult() {
	}
}

