package com.github.maxbraun.jobprofiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.slf4j.Logger;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import hudson.model.BuildableItem;
import hudson.model.Failure;
import hudson.model.ListView;
import hudson.model.View;
import hudson.util.IOException2;
import jenkins.model.Jenkins;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.util.Strings;

public class Job {
    private static final String DELEMITER = "_";
    private static final String PREFIX = "user";
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Job.class);

    /**
     * softwareindex ID
     */
    private final String indexId;
    /**
     * Name of the artifact
     */
    private final String name;

    private final String category;
    private final Scm scm;
    private final Date now;
    private final String groupId;
    protected Map<String, String> parsedTemplates;
    private Profile profile;
    /**
     * Key i need to identify against jenkins
     */
    private String key;
    private Map<String, Object> templateContextAdditions;
    @java.beans.ConstructorProperties({"indexId", "name", "category", "scm", "now", "groupId"})
    public Job(String indexId, String name, String category, Scm scm, Date now, String groupId) {
        this.indexId = indexId;
        this.name = name;
        this.category = category;
        this.scm = scm;
        this.now = now;
        this.groupId = groupId;
    }
    public static Job create(SoftwareAsset asset, World world) {
        Scm scm = asset.getTrunk().equals("system") ? null : Scm.create(asset.getTrunk(), world);
        return new Job(asset.getId(), asset.getArtifactId(), asset.getCategory(), scm, new Date(), asset.getGroupId());
    }
    private static void removeJobFromViews(String jobId) {
        for (View view : Jenkins.getInstance().getViews()) {
            view.onJobRenamed(null, jobId, null);
        }
    }
    private static void addJobToView(String jobId, String viewName) throws IOException, ServletException {
        if (viewName != null && jobId != null) {
            try {

                if (Jenkins.getInstance().getView(viewName) == null) {
                    View view = new ListView(viewName);
                    Jenkins.getInstance().addView(view);
                }

                ListView view = (ListView) Jenkins.getInstance().getView(viewName);
                view.doAddJobToView(jobId);
            } catch (Failure e) {
                Job.log.error("Something went wrong with asset {} in category {}. {}", jobId, viewName, e);
            }
        } else {
            Job.log.error("Something went wrong with asset {} in category {}", jobId, viewName);
        }
    }
    public void addContext(String key, Object value) {
        if (templateContextAdditions == null) {
            templateContextAdditions = new HashMap<String, Object>();
        }
        templateContextAdditions.put(key, value);
    }
    private Map<String, Object> getTemplateContextAdditions() {
        return templateContextAdditions == null ? new HashMap<String, Object>() : templateContextAdditions;
    }
    public void parseProfile(PrintStream log) throws IOException, TemplateException {
        Map<String, String> xmls;
        Writer writer;
        Reader reader;
        Template template;


        xmls = new HashMap<String, String>();

        log.println(String.format("Creating Jobs for %s | Profile: %s ", name, this.profile.getName()));

        for (Map.Entry<String, String> entry : profile.getXmls().entrySet()) {
            writer = new StringWriter();

            reader = new StringReader(entry.getValue());
            template = new Template(createIdentifier(entry.getKey()), reader, new Configuration());
            template.process(toTemplateContext(), writer);

            if (writer.toString().length() == 0) {
                continue;
            }
            xmls.put(createIdentifier(entry.getKey()), writer.toString());
        }
        parsedTemplates = xmls;
    }
    public void sendJobsToJenkins() throws IOException {
        for (Map.Entry<String, String> template : parsedTemplates.entrySet()) {
            InputStream src;
            BuildableItem job;
            src = new ByteArrayInputStream(template.getValue().getBytes());
            try {
                job = (BuildableItem) Jenkins.getInstance()
                  .createProjectFromXML(template.getKey(), src);
            } catch (IOException2 e) {
                log.info("could not parse because" + e.getMessage());
                log.info(template.getValue());
            }
            src.close();
        }
    }
    public void manageViews() throws IOException, ServletException {
        for (Map.Entry<String, String> template : parsedTemplates.entrySet()) {
            removeJobFromViews(template.getKey());
            addJobToView(template.getKey(), category);
        }
    }
    private String createIdentifier(String templateFileName) {
        String key = String.format("%s%s%s", getGroupId(), DELEMITER, getName());

        if (!templateFileName.equals("build.xml")) {
            return String.format("%s%s%s", key, DELEMITER, Strings.removeRight(templateFileName, ".xml"));
        } else {
            return key;
        }

    }

    private Map<String, Object> toTemplateContext() {
        Map<String, Object> context;
        context = new HashMap<String, Object>();
        context.putAll(getTemplateContextAdditions());
        context.put("name", name);
        context.put("indexId", indexId);
        context.put("now", now.toString());
        context.put("usedProfile", profile.getName());
        context.put("id", createIdentifier("build.xml"));
        context.put("scm", scm != null ? scm.getRemote() : "");
        return context;
    }

    public String getIndexId() {
        return this.indexId;
    }
    public String getName() {
        return this.name;
    }
    public String getCategory() {
        return this.category;
    }
    public Scm getScm() {
        return this.scm;
    }
    public Date getNow() {
        return this.now;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public Map<String, String> getParsedTemplates() {
        return this.parsedTemplates;
    }
    public Profile getProfile() {
        return this.profile;
    }
    public String getKey() {
        return this.key;
    }
    public void setParsedTemplates(Map<String, String> parsedTemplates) {
        this.parsedTemplates = parsedTemplates;
    }
    public void setProfile(Profile profile) {
        this.profile = profile;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public void setTemplateContextAdditions(Map<String, Object> templateContextAdditions) {
        this.templateContextAdditions = templateContextAdditions;
    }
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Job)) {
            return false;
        }
        final Job other = (Job) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$indexId = this.indexId;
        final Object other$indexId = other.indexId;
        if (this$indexId == null ? other$indexId != null : !this$indexId.equals(other$indexId)) {
            return false;
        }
        final Object this$name = this.name;
        final Object other$name = other.name;
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        final Object this$category = this.category;
        final Object other$category = other.category;
        if (this$category == null ? other$category != null : !this$category.equals(other$category)) {
            return false;
        }
        final Object this$scm = this.scm;
        final Object other$scm = other.scm;
        if (this$scm == null ? other$scm != null : !this$scm.equals(other$scm)) {
            return false;
        }
        final Object this$now = this.now;
        final Object other$now = other.now;
        if (this$now == null ? other$now != null : !this$now.equals(other$now)) {
            return false;
        }
        final Object this$groupId = this.groupId;
        final Object other$groupId = other.groupId;
        if (this$groupId == null ? other$groupId != null : !this$groupId.equals(other$groupId)) {
            return false;
        }
        final Object this$parsedTemplates = this.parsedTemplates;
        final Object other$parsedTemplates = other.parsedTemplates;
        if (this$parsedTemplates == null ? other$parsedTemplates != null : !this$parsedTemplates.equals(other$parsedTemplates)) {
            return false;
        }
        final Object this$profile = this.profile;
        final Object other$profile = other.profile;
        if (this$profile == null ? other$profile != null : !this$profile.equals(other$profile)) {
            return false;
        }
        final Object this$key = this.key;
        final Object other$key = other.key;
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) {
            return false;
        }
        final Object this$templateContextAdditions = this.getTemplateContextAdditions();
        final Object other$templateContextAdditions = other.getTemplateContextAdditions();
        if (this$templateContextAdditions == null ? other$templateContextAdditions != null : !this$templateContextAdditions.equals(other$templateContextAdditions)) {
            return false;
        }
        return true;
    }
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $indexId = this.indexId;
        result = result * PRIME + ($indexId == null ? 0 : $indexId.hashCode());
        final Object $name = this.name;
        result = result * PRIME + ($name == null ? 0 : $name.hashCode());
        final Object $category = this.category;
        result = result * PRIME + ($category == null ? 0 : $category.hashCode());
        final Object $scm = this.scm;
        result = result * PRIME + ($scm == null ? 0 : $scm.hashCode());
        final Object $now = this.now;
        result = result * PRIME + ($now == null ? 0 : $now.hashCode());
        final Object $groupId = this.groupId;
        result = result * PRIME + ($groupId == null ? 0 : $groupId.hashCode());
        final Object $parsedTemplates = this.parsedTemplates;
        result = result * PRIME + ($parsedTemplates == null ? 0 : $parsedTemplates.hashCode());
        final Object $profile = this.profile;
        result = result * PRIME + ($profile == null ? 0 : $profile.hashCode());
        final Object $key = this.key;
        result = result * PRIME + ($key == null ? 0 : $key.hashCode());
        final Object $templateContextAdditions = this.getTemplateContextAdditions();
        result = result * PRIME + ($templateContextAdditions == null ? 0 : $templateContextAdditions.hashCode());
        return result;
    }
    public boolean canEqual(Object other) {
        return other instanceof Job;
    }
    public String toString() {
        return "org.jenkinsci.plugins.jobprofiles.Job(indexId=" + this.indexId + ", name=" + this.name + ", category=" + this.category + ", scm=" + this.scm + ", now=" + this.now + ", groupId=" + this.groupId + ", parsedTemplates=" + this.parsedTemplates + ", profile=" + this.profile + ", key=" + this.key + ", templateContextAdditions=" + this.getTemplateContextAdditions() + ")";
    }
}
