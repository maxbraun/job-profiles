package org.jenkinsci.plugins.jobprofiles;

import freemarker.template.TemplateException;
import net.oneandone.sushi.fs.World;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import static org.jenkinsci.plugins.jobprofiles.JobProfilesConfiguration.get;

public class Jobs {
    public Jobs() {
    }

    public static void Jobs(PrintStream log, SoftwareIndex index, String forcedProfile, World world) throws IOException {
        ProfileManager profileManager;
        Set<Job> jobs;
        profileManager = new ProfileManager(world, log, JobProfilesConfiguration.get().getProfileRootDir());

        jobs = createJobs(log, index, profileManager, forcedProfile, world);

        try {
            for (Job job : jobs) {
                job.sendParsedTemplatesToInstance();
                job.manageViews();
            }
        } catch (ServletException e) {
            throw new JobProfileException(e);
        }
    }

    public static Set<Job> createJobs(PrintStream log, SoftwareIndex index, ProfileManager profileManager, String forcedProfile, World world)
            throws IOException {
        Set<Job> jobs;
        Job currentJob;

        jobs = new HashSet<Job>();

        for (SoftwareAsset asset : index.getAssets()) {
            currentJob = Job.Job(asset, world);

            profileManager.discover(currentJob.getScm(), forcedProfile).getProfile();

            currentJob.setUsedProfile(profileManager.profile);
            try {
                Context.add(currentJob, world);
                currentJob.createParsedTemplates(log, profileManager.getProfile());
                jobs.add(currentJob);
            } catch (TemplateException e) {
                throw new JobProfileException(e.getMessage(), e);
            }
            jobs.add(currentJob);
        }
        return jobs;
    }

    public static void buildJobs(String forcedSCM, String forcedProfile, PrintStream log, World world) throws IOException {
        SoftwareIndex index;

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

                asset.setTrunk(forcedSCM);
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


        Jobs(log, index, forcedProfile, world);
    }
}