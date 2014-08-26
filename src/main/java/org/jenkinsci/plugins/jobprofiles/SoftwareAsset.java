package org.jenkinsci.plugins.jobprofiles;

import java.io.IOException;

import org.apache.commons.lang.NotImplementedException;
import org.apache.maven.project.MavenProject;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.oneandone.sushi.fs.World;

@XStreamAlias("asset")
public class SoftwareAsset {
    private String id;
    private String artifactId;
    private String groupId;
    private String category;
    private String trunk;
    private String type;
    public SoftwareAsset() {
    }

    public static SoftwareAsset fromSCM(String scmLocation, World world) throws IOException {
        Scm scm = Scm.create(scmLocation, world);
        SoftwareAsset asset;
        asset = new SoftwareAsset();
        String artifactId;
        //TODO: I need some kind of a Builder with several Impls for each build system.
        if (scm.getPom() == null) {
            throw new NotImplementedException("Can't read something else then maven projects - currently sorry");
        }
        MavenProject mavenProject = ContextBuilder.getMavenProject(scm.getPom(), world);
        artifactId = mavenProject.getArtifactId();
        if (!scmLocation.endsWith("trunk")) {
            artifactId = mavenProject.getArtifact() + " " + scmLocation.substring(scmLocation.lastIndexOf('/'));
        }
        asset.setArtifactId(artifactId);
        asset.setGroupId(mavenProject.getGroupId());
        asset.setTrunk(scmLocation);
        asset.setId("0");
        return asset;
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getArtifactId() {
        return this.artifactId;
    }
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getCategory() {
        return this.category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getTrunk() {
        return this.trunk;
    }
    public void setTrunk(String trunk) {
        this.trunk = trunk;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SoftwareAsset)) {
            return false;
        }
        final SoftwareAsset other = (SoftwareAsset) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$id = this.id;
        final Object other$id = other.id;
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        final Object this$artifactId = this.artifactId;
        final Object other$artifactId = other.artifactId;
        if (this$artifactId == null ? other$artifactId != null : !this$artifactId.equals(other$artifactId)) {
            return false;
        }
        final Object this$groupId = this.groupId;
        final Object other$groupId = other.groupId;
        if (this$groupId == null ? other$groupId != null : !this$groupId.equals(other$groupId)) {
            return false;
        }
        final Object this$category = this.category;
        final Object other$category = other.category;
        if (this$category == null ? other$category != null : !this$category.equals(other$category)) {
            return false;
        }
        final Object this$trunk = this.trunk;
        final Object other$trunk = other.trunk;
        if (this$trunk == null ? other$trunk != null : !this$trunk.equals(other$trunk)) {
            return false;
        }
        final Object this$type = this.type;
        final Object other$type = other.type;
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        return true;
    }
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.id;
        result = result * PRIME + ($id == null ? 0 : $id.hashCode());
        final Object $artifactId = this.artifactId;
        result = result * PRIME + ($artifactId == null ? 0 : $artifactId.hashCode());
        final Object $groupId = this.groupId;
        result = result * PRIME + ($groupId == null ? 0 : $groupId.hashCode());
        final Object $category = this.category;
        result = result * PRIME + ($category == null ? 0 : $category.hashCode());
        final Object $trunk = this.trunk;
        result = result * PRIME + ($trunk == null ? 0 : $trunk.hashCode());
        final Object $type = this.type;
        result = result * PRIME + ($type == null ? 0 : $type.hashCode());
        return result;
    }
    public boolean canEqual(Object other) {
        return other instanceof SoftwareAsset;
    }
    public String toString() {
        return "org.jenkinsci.plugins.jobprofiles.SoftwareAsset(id=" + this.id + ", artifactId=" + this.artifactId + ", groupId=" + this.groupId + ", category=" + this.category + ", trunk=" + this.trunk + ", type=" + this.type + ")";
    }
}
