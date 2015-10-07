package com.github.maxbraun.jobprofiles;
import com.fasterxml.jackson.annotation.JsonProperty;
public class Coordinates {

    @JsonProperty
    private String artifactId;
    @JsonProperty
    private String groupId;

    public Coordinates() {
    }

    public Coordinates(String artifactId, String groupId) {
        this.artifactId = artifactId;
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
          "artifactId='" + artifactId + '\'' +
          ", groupId='" + groupId + '\'' +
          '}';
    }
}
