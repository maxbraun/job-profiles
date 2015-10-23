package com.github.maxbraun.jobprofiles;
public class SystemSoftwareAsset extends SoftwareAsset {
    public SystemSoftwareAsset(JobProfilesConfiguration jobProfilesConfiguration) {
        super(jobProfilesConfiguration.getProfileRootDir(), new Coordinates("system", "system"), null);
    }

    @Override
    public ScmNode scmNode() {
        return null;
    }
}
