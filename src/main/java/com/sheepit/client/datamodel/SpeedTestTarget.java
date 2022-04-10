package com.sheepit.client.datamodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.util.LongSummaryStatistics;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Root(name = "target")
public class SpeedTestTarget {

    @Attribute(name = "url")
    private String url;
    private long speedtest;
    private LongSummaryStatistics ping;

}
