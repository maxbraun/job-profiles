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
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import hudson.model.AbstractItem;
import hudson.model.Failure;
import hudson.model.ListView;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.util.IOException2;
import jenkins.model.Jenkins;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.util.Strings;

public class Job {
    private static final String DELEMITER = "_";
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Job.class);

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

    public Job(String name, String category, Scm scm, Date now, String groupId) {
        this.name = name;
        this.category = category;
        this.scm = scm;
        this.now = now;
        this.groupId = groupId;
    }
    public static Job create(SoftwareAsset asset, World world) {
        Scm scm = asset.scm().equals("system") ? null : Scm.create(asset.scm(), world);
        return new Job(asset.artifactId(), asset.category(), scm, new Date(), asset.groupId());
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
        if (this.profile == null) {
            log.println(String.format("Profile for %s not found.", name));
            return;
        }
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
            src = new ByteArrayInputStream(template.getValue().getBytes());
            TopLevelItem item = Jenkins.getInstance().getItem(template.getKey());
            if (item == null) {

                try {
                    Jenkins.getInstance()
                      .createProjectFromXML(template.getKey(), src);
                } catch (IOException2 e) {
                    log.info("could not parse because" + e.getMessage());
                    log.info(template.getValue());
                } finally {
                    src.close();
                }
            } else {
                Source source = new StreamSource(src);
                ((AbstractItem) item).updateByXml(source);
            }
        }
    }
    public void manageViews() throws IOException, ServletException {
        for (Map.Entry<String, String> template : parsedTemplates.entrySet()) {
            removeJobFromViews(template.getKey());
            addJobToView(template.getKey(), category);
        }
    }
    private String createIdentifier(String templateFileName) {
        String key = String.format("%s%s%s", groupId(), DELEMITER, name());

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
        context.put("now", now.toString());
        context.put("usedProfile", profile.getName());
        context.put("id", createIdentifier("build.xml"));
        context.put("scm", Strings.removeLeftOpt((scm != null ? scm.uri() : ""), "svn:"));
        return context;
    }

    public String name() {
        return this.name;
    }
    public String category() {
        return this.category;
    }
    public Scm scm() {
        return this.scm;
    }
    public Date now() {
        return this.now;
    }
    public String groupId() {
        return this.groupId;
    }
    public Map<String, String> parsedTemplates() {
        return this.parsedTemplates;
    }
    public Profile profile() {
        return this.profile;
    }
    public String key() {
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


}
