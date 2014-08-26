package org.jenkinsci.plugins.jobprofiles;

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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import hudson.model.BuildableItem;
import hudson.model.Failure;
import hudson.model.ListView;
import hudson.model.View;
import hudson.util.IOException2;
import jenkins.model.Jenkins;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.util.Strings;

@Data
@Slf4j
public class Job {
    private static final String DELEMITER = "_";
    private static final String PREFIX = "user";

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

}
