package org.jenkinsci.plugins.jobprofiles;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ToString
@NoArgsConstructor
@XStreamAlias("assets")
public class XmlSoftwareIndex implements SoftwareIndex {

    @XStreamImplicit
    public List<SoftwareAsset> asset = new ArrayList<SoftwareAsset>();


    public List<SoftwareAsset> getAssets() {
        return asset;
    }

    public SoftwareAsset getAsset(Integer Id) {
        return null;
    }

    public SoftwareAsset getAsset(String Name) {
        return null;
    }

}
