package com.github.maxbraun.jobprofiles;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;

import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
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
        try {
            database.downloadOpt();
        }catch (IOException e) {
            if (System.getProperty("http.proxyPort")  != null ) {
                throw e;
            }
            ProxyConfiguration proxy = Jenkins.getInstance().proxy;
            log.append("Cannot download Pommes index. Retry with Proxy:"  + proxy.name + ":" + proxy.port);
            System.setProperty("http.proxyHost", proxy.name);
            System.setProperty("http.proxyPort", "" + proxy.port);
            database.downloadOpt();

            System.clearProperty("http.proxyHost");
            System.clearProperty("http.proxyPort");
        }
        index = new SoftwareIndex();


        for (Pom pom : database.search("/trunk/")) {
            if (pom.projectUrl() != null && !"".equals(pom.projectUrl())) {
                if (pom.projectUrl().contains("ssh://git@github.com")) {
                    log.println(pom.toString() + " is currently not supported.");
                    continue;
                }
                Scm scm = Scm.create(pom.projectUrl(), pommesGlobal.getWorld(), log);
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
