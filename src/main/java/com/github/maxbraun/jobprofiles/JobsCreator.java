package com.github.maxbraun.jobprofiles;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
public class JobsCreator {

    private final Profiles profiles;
    private final MavenProjectResolver resolver;
    private final String forcedProfile;
    private final PrintStream log;

    public JobsCreator(Profiles profiles, MavenProjectResolver resolver, String forcedProfile, PrintStream log) {
        this.profiles = profiles;
        this.resolver = resolver;
        this.forcedProfile = forcedProfile;
        this.log = log;
    }

    public Jobs createJobs(SoftwareAsset asset) {
        try {
            Profile profile = profileOf(asset);
            log.printf("%s has profile %s with %d templates%n", asset, profile.name(), profile.amountOfTemplates());
            return profile.parse(asset, contextOf(profile, asset));
        } catch (Exception e){
            log.println(e.getMessage());
            e.printStackTrace(log);
        }
        return new Jobs();
    }

    protected Profile profileOf(SoftwareAsset asset) throws IOException {
        List<String> possibleProfiles = new PossibleProfiles(resolver).of(asset, forcedProfile);
        for (String possibleProfile : possibleProfiles) {
            if (profiles.exists(possibleProfile)) {
                return profiles.get(possibleProfile);
            }
        }
        StringBuilder profiles  = new StringBuilder("possibleProfiles {");
        for (String possibleProfile : possibleProfiles) {
            profiles.append(possibleProfile).append(',');
        }
        profiles.append('}');

        throw new IllegalArgumentException("No profile found for" + asset + profiles + " vs. " + profiles.toString());
    }

    private JobContext contextOf(Profile profile, SoftwareAsset asset) {
        JobContextBuilder jobContextBuilder = new JobContextBuilder();
        jobContextBuilder
          .withJenkins()
          .withProfile(profile)
          .withSoftwareAsset(asset);
        if (asset.scmNode() != null && asset.scmNode().getPom() != null) {
            jobContextBuilder.withMaven(asset.scmNode().getPom(), asset.scmNode().world());
        }

        return jobContextBuilder.build();

    }
}
