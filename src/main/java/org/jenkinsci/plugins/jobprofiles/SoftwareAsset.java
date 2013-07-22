package org.jenkinsci.plugins.jobprofiles;

import java.io.Serializable;

public interface SoftwareAsset extends Serializable {
    String getId();

    String getName();

    String getCategory();

    String getScm();
}
