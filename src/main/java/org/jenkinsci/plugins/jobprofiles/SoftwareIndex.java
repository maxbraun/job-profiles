package org.jenkinsci.plugins.jobprofiles;


import java.io.Serializable;
import java.util.List;

public interface SoftwareIndex extends Serializable {
    List<SoftwareAsset> getAssets();

    SoftwareAsset getAsset(Integer id);

    SoftwareAsset getAsset(String name);

}
