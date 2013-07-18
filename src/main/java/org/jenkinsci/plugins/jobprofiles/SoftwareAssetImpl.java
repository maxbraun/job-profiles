package org.jenkinsci.plugins.jobprofiles;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XStreamAlias("asset")
public class SoftwareAssetImpl implements SoftwareAsset {
    public String id;
    public String name;
    public String category;
    public String scm;
}
