package org.jenkinsci.plugins.jobprofiles;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;
import hudson.util.XStream2;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.oneandone.sushi.fs.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ToString
@NoArgsConstructor
@XStreamAlias("assets")
public class SoftwareIndex {

    public static SoftwareIndex load(Node index) throws IOException {
        XStream2 xstream;
        InputStream src;
        SoftwareIndex result;

        xstream = new XStream2(new DomDriver());

        xstream.autodetectAnnotations(true);
        xstream.alias("asset", SoftwareAsset.class);
        xstream.alias("assets", SoftwareIndex.class);
        src = index.createInputStream();
        result = (SoftwareIndex) xstream.fromXML(src);
        src.close();
        return result;
    }

    @XStreamImplicit
    private List<SoftwareAsset> asset = new ArrayList<SoftwareAsset>();


    public List<SoftwareAsset> getAssets() {
        return asset;
    }

    public SoftwareAsset getAsset(Integer id) {
        return null;
    }

    public SoftwareAsset getAsset(String name) {
        return null;
    }

}
