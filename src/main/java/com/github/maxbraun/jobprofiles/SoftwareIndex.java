package com.github.maxbraun.jobprofiles;

import static com.github.maxbraun.jobprofiles.JobProfilesConfiguration.get;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import net.oneandone.sushi.fs.Node;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.util.Strings;

public class SoftwareIndex implements Iterable<SoftwareAsset> {
    public static SoftwareIndex buildFrom(String forcedSCM,  PrintStream log)
      throws IOException, URISyntaxException, SVNException {
        SoftwareIndex index;

        forcedSCM = forcedSCM == null ? "" : forcedSCM;

        if (!forcedSCM.isEmpty()) {
            SoftwareAsset asset;
            index = new SoftwareIndex();

            if (forcedSCM.equals("system")) {
                asset = new SystemSoftwareAsset(JobProfilesConfiguration.get());
            } else {
                asset = SoftwareAsset.fromSCM(forcedSCM);
            }
            index.add(asset);

        } else {

            log.println("Going to parse " + get().getSoftwareIndexFile());
            index = SoftwareIndex.load(new World().node(Strings.removeRightOpt(JobProfilesConfiguration.get().getSoftwareIndexFile(), "/")));
            log.println("Parsed.");
        }



        return index;

    }

    public static SoftwareIndex load(Node index) throws IOException, URISyntaxException, SVNException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        CollectionType collectionType = typeFactory.constructCollectionType(
          List.class, SoftwareAsset.class);

        SoftwareIndex softwareIndex = new SoftwareIndex();
        softwareIndex.assets =  objectMapper.readValue(index.readString(), collectionType);
        return softwareIndex;
    }

    private List<SoftwareAsset> assets = new ArrayList<SoftwareAsset>();


    private int size() {
        return assets.size();
    }
    public List<SoftwareAsset> assets() {
        return Collections.unmodifiableList(assets);
    }

    public void add(SoftwareAsset asset) {
        assets.add(asset);
    }
    @Override
    public Iterator<SoftwareAsset> iterator() {
        return assets.iterator();
    }

}
