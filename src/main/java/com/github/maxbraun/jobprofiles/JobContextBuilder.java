package com.github.maxbraun.jobprofiles;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.MavenProject;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;

import hudson.model.Item;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import net.oneandone.sushi.fs.World;

public class JobContextBuilder {

    private final JobContext jobContext = new JobContext();


    public JobContextBuilder withSoftwareAsset(SoftwareAsset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("asset cannot be null");
        }
        jobContext.add("name", asset.artifactId());
        jobContext.add("identifier", asset.groupId());
        jobContext.add("disabled", false);
        jobContext.add("scm", asset.origin());
        jobContext.add("scmCredentials", credentialsForScm(asset.scmNode()));

        return this;
    }

    public JobContextBuilder withJenkins()  {
        jobContext.add("max_executors", Jenkins.getInstance().getNumExecutors());
        return this;
    }

    public JobContextBuilder withMaven(String pomContent, World world)  {
        MavenProject project;

        if (pomContent == null) {
            throw new IllegalArgumentException("pomContent cannot be null");
        }
        project = new MavenProjectResolver(world).resolveFrom(pomContent);


        jobContext.add("mavenproject", project);

        for (Map.Entry entry : project.getProperties().entrySet()) {
            jobContext.add(entry.getKey().toString().replace(".", "_"), entry.getValue());
        }
        return this;
    }

    public JobContextBuilder withProfile(Profile profile) {
        jobContext.add("usedProfile", profile.name());
        return this;
    }

    private String credentialsForScm(ScmNode scm) {
        String url;
        if (scm == null) {
            url = JobProfilesConfiguration.get().getProfileRootDir();
        } else {
            url = scm.uri();
        }
        List<DomainRequirement> domainRequirements = URIRequirementBuilder.fromUri(url).build();
        Item item = null;
        StandardUsernamePasswordCredentials credentials = CredentialsMatchers.firstOrNull(
          CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class, item, ACL.SYSTEM, domainRequirements),
          CredentialsMatchers.always());
        if (credentials != null) {
            return credentials.getId();
        }
        return "";
    }

    public JobContext build() {
        jobContext.add("now", new Date().toString());
        return jobContext;
    }
}
