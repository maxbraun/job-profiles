package org.jenkinsci.plugins.jobprofiles;

import java.io.Serializable;

public interface SoftwareAsset extends Serializable {
    public String getId();

    public String getName();

    public String getArtifactId();

    public String getGroupId();

    public String getCategory();
}
