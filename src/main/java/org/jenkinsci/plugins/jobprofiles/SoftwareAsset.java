package org.jenkinsci.plugins.jobprofiles;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XStreamAlias("asset")
public class SoftwareAsset {
    private String id;
    private String name;
    private String category;
    private String scm;
    private String type;
}
