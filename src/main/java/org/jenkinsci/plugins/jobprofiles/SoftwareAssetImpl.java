package org.jenkinsci.plugins.jobprofiles;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("asset")
public class SoftwareAssetImpl implements SoftwareAsset {
    public String id;
    public String artifactId;
    public String groupId;
    public String category;
    public String trunk;

    public SoftwareAssetImpl() {

    }

    public String getName() {
        return artifactId;
    }
}
