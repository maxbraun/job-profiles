package org.jenkinsci.plugins.jobprofiles;

public interface SoftwareAsset {
    public String getId();
    public String getName();
    public String getArtifactId();
    public String getGroupId();
    public SoftwareCategory getCategory();
}
