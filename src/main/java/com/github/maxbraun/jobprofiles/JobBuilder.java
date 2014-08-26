package com.github.maxbraun.jobprofiles;
import static com.github.maxbraun.jobprofiles.JobProfilesConfiguration.get;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;

import freemarker.template.TemplateException;
import net.oneandone.sushi.fs.World;

public class JobBuilder {
    public JobBuilder() {
    }

    public static void buildJobs(String forcedSCM, String forcedProfile, PrintStream log, World world) throws IOException {
        com.github.maxbraun.jobprofiles.SoftwareIndex index;

        forcedSCM = forcedSCM == null ? "" : forcedSCM;
        forcedProfile = forcedProfile == null ? "" : forcedProfile;

        if (!forcedSCM.isEmpty()) {
            SoftwareAsset asset;
            asset = new SoftwareAsset();
            index = new SoftwareIndex();

            if (forcedSCM.equals("system")) {

                asset.setId("system");
                asset.setArtifactId("system");
                asset.setCategory("System");
                asset.setGroupId("system");
                asset.setTrunk("system");

            } else {

                asset = SoftwareAsset.fromSCM(forcedSCM, world);
            }
            index.asset.add(asset);

        } else {

            log.println("Going to parse " + get().getSoftwareIndexFile());
            index = SoftwareIndex.load(world.validNode(get().getSoftwareIndexFile()));
            log.println("Parsed.");
        }

        if (!forcedProfile.isEmpty()) {
            log.println("Using a forced Profile: " + forcedProfile);
        }


        sendJobsToJenkins(log, index, forcedProfile, world);
    }

    private static void sendJobsToJenkins(PrintStream log, SoftwareIndex index, String forcedProfile, World world) throws IOException {
        ProfileManager profileManager;
        Set<Job> jobs;
        profileManager = new ProfileManager(world, log, get().getProfileRootDir());

        jobs = parseAllTemplates(log, index, profileManager, forcedProfile, world);

        try {
            for (Job job : jobs) {
                job.sendJobsToJenkins();
                job.manageViews();
            }
        } catch (ServletException e) {
            throw new JobProfileException(e);
        }
    }

    private static Set<Job> parseAllTemplates(PrintStream log, SoftwareIndex index, ProfileManager profileManager, String forcedProfile, World world)
      throws IOException {
        Set<Job> jobs;
        Job currentJob;
        Profile currentProfile;

        jobs = new HashSet<Job>();

        for (SoftwareAsset asset : index.getAssets()) {
            currentJob = Job.create(asset, world);

            currentProfile = profileManager.getProfileForScm(currentJob.getScm(), forcedProfile);

            currentJob.setProfile(currentProfile);
            try {
                ContextBuilder.add(currentJob, world, asset);
                currentJob.parseProfile(log);
                jobs.add(currentJob);
            } catch (TemplateException e) {
                throw new JobProfileException(e.getMessage(), e);
            }
            jobs.add(currentJob);
        }
        return jobs;
    }

}