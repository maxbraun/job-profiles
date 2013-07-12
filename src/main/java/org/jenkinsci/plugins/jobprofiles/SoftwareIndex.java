package org.jenkinsci.plugins.jobprofiles;


import java.util.List;

public interface SoftwareIndex {
    public List<SoftwareAsset> getAssets();
    public SoftwareAsset getAsset(Integer Id);
    public SoftwareAsset getAsset(String Name);

}
