package org.jenkinsci.plugins.jobprofiles;

import freemarker.template.TemplateException;
import net.oneandone.sushi.fs.World;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class Jobs {
    public Jobs() {
    }

    void Jobs(PrintStream log, SoftwareIndex index, String forcedProfile) throws IOException {
        World world;
        ProfileManager profileManager;
        Set<Job> jobs;
        world = new World();
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
}