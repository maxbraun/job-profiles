package org.jenkinsci.plugins.jobprofiles;


import net.oneandone.sushi.fs.NodeInstantiationException;
import net.oneandone.sushi.fs.World;

import java.util.List;

public class XmlSoftwareIndex implements SoftwareIndex {

    public XmlSoftwareIndex(String database) throws NodeInstantiationException {
        World world;

        world = new World();

        world.validNode(database);

    }

    private List<SoftwareAsset> softwareAssetList;

    public List<SoftwareAsset> getAssets() {
        return softwareAssetList;
    }

    public SoftwareAsset getAsset(Integer Id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public SoftwareAsset getAsset(String Name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
