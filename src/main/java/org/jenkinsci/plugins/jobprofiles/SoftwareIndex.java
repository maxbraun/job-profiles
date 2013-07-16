package org.jenkinsci.plugins.jobprofiles;


import java.io.Serializable;
import java.util.List;

public interface SoftwareIndex extends Serializable {
    public List<SoftwareAsset> getAssets();

    public SoftwareAsset getAsset(Integer Id);

    public SoftwareAsset getAsset(String Name);

}
