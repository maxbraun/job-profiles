package com.github.maxbraun.jobprofiles;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.oneandone.pommes.model.Database;
import net.oneandone.pommes.model.Pom;
import net.oneandone.sushi.fs.World;

public class SoftwareIndex {

    private List<SoftwareAsset> assets;
    public SoftwareIndex() {
        assets = new ArrayList<SoftwareAsset>();
    }
    public static SoftwareIndex load(World world, PrintStream log) throws IOException, URISyntaxException {
        SoftwareIndex index;
        Database database;

        database = Database.load(world);
        index = new SoftwareIndex();


        for (Pom pom : database.search("")) {
            if (pom.scm != null) {
                index.add(SoftwareAsset.withPom(pom));
            } else {
                log.println(pom.toString() + " has no scm");
            }

        }


        return index;
    }
    public List<SoftwareAsset> assets() {
        return Collections.unmodifiableList(assets);
    }

    public void add(SoftwareAsset asset) {
        assets.add(asset);
    }

}
