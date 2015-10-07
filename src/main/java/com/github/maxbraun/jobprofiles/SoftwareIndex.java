package com.github.maxbraun.jobprofiles;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import net.oneandone.sushi.fs.Node;

public class SoftwareIndex {

    private List<SoftwareAsset> assets;
    public SoftwareIndex() {
        assets = new ArrayList<SoftwareAsset>();
    }
    public static SoftwareIndex load(Node index, PrintStream log) throws IOException, URISyntaxException, SVNException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        CollectionType collectionType = typeFactory.constructCollectionType(
          List.class, SoftwareAsset.class);

        SoftwareIndex softwareIndex = new SoftwareIndex();
        softwareIndex.assets =  objectMapper.readValue(index.readString(), collectionType);
        return softwareIndex;
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

    /*
        database = new Database(pommesGlobal.getWorld().getTemp().createTempDirectory().join("pommes"), pommesGlobal);
        //database = Database.load(world);

        database.downloadOpt();

        index = new SoftwareIndex();

        TermQuery termQuery = new TermQuery(new Term(Database.ORIGIN, "/trunk"));

        for (Pom pom : database.query(termQuery)) {
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
        return index;*/

}
