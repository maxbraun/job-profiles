package com.github.maxbraun.jobprofiles;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;

import hudson.util.XStream2;
import net.oneandone.sushi.fs.Node;

@XStreamAlias("registry")
public class SoftwareIndex {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SoftwareIndex.class);
    @XStreamImplicit
    public List<SoftwareAsset> asset = new ArrayList<SoftwareAsset>();
    public SoftwareIndex() {
    }
    public static SoftwareIndex load(Node index) throws IOException {
        XStream2 xstream;
        InputStream src;
        SoftwareIndex result;

        xstream = new XStream2(new DomDriver());

        xstream.autodetectAnnotations(true);
        xstream.alias("asset", SoftwareAsset.class);
        xstream.alias("registry", SoftwareIndex.class);
        xstream.alias("assets", SoftwareIndex.class);
        xstream.aliasField("name", SoftwareAsset.class, "artifactId");
        xstream.aliasField("artifact-id", SoftwareAsset.class, "artifactId");
        xstream.aliasField("group-id", SoftwareAsset.class, "groupId");
        xstream.aliasField("scm", SoftwareAsset.class, "trunk");
        src = index.createInputStream();
        result = (SoftwareIndex) xstream.fromXML(src);
        src.close();
        return result;
    }
    public List<SoftwareAsset> getAssets() {
        return asset;
    }

    public SoftwareAsset getAsset(String scm) {
        return null;
    }

    public List<SoftwareAsset> getAsset() {
        return this.asset;
    }
    public void setAsset(List<SoftwareAsset> asset) {
        this.asset = asset;
    }
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SoftwareIndex)) {
            return false;
        }
        final SoftwareIndex other = (SoftwareIndex) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$asset = this.getAsset();
        final Object other$asset = other.getAsset();
        if (this$asset == null ? other$asset != null : !this$asset.equals(other$asset)) {
            return false;
        }
        return true;
    }
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $asset = this.getAsset();
        result = result * PRIME + ($asset == null ? 0 : $asset.hashCode());
        return result;
    }
    public boolean canEqual(Object other) {
        return other instanceof SoftwareIndex;
    }
    public String toString() {
        return "org.jenkinsci.plugins.jobprofiles.SoftwareIndex(asset=" + this.getAsset() + ")";
    }
}
