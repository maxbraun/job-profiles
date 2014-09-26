package com.github.maxbraun.jobprofiles;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;

import net.oneandone.pommes.model.Database;
import net.oneandone.pommes.model.Pom;
import net.oneandone.sushi.fs.Node;

public class SoftwareIndex {

    private List<SoftwareAsset> assets;
    public SoftwareIndex() {
        assets = new ArrayList<SoftwareAsset>();
    }
    public static SoftwareIndex load(Node pommesGlobal, PrintStream log) throws IOException, URISyntaxException, SVNException {
        SoftwareIndex index;
        Database database;

        database = new Database(pommesGlobal.getWorld().getTemp().createTempDirectory().join("pommes"), pommesGlobal);
        //database = Database.load(world);
        database.downloadOpt();
        index = new SoftwareIndex();


        for (Pom pom : database.search("")) {
            if (pom.projectUrl() != null && !"".equals(pom.projectUrl())) {
                if (pom.projectUrl().contains("ssh://git@github.com")) {
                    log.println(pom.toString() + " is currently not supported.");
                    continue;
                }
                Scm scm = Scm.create(pom.projectUrl(), pommesGlobal.getWorld());
                if (!scm.exists()) {
                    log.println(pom.toString() + " has non existing scm.");
                    continue;
                }
                index.add(SoftwareAsset.withPom(pom, scm.active(), scm.category()));


            } else {
                log.println(pom.toString() + " has no scm.");
            }

        }

        log.println(index.size() + " Assets");
        return index;
    }

    private int size() {
        return assets.size();
    }
    public List<SoftwareAsset> assets() {
        return Collections.unmodifiableList(assets);
    }

    public void add(SoftwareAsset asset) {
        assets.add(asset);
    }


}
