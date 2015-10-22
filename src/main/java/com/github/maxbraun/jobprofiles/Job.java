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
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import hudson.model.AbstractItem;
import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.security.ACL;
import hudson.util.IOException2;
import jenkins.model.Jenkins;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.util.Strings;

//TODO…
public class Job {
    private static final String DELEMITER = "_";

    /**
     * Name of the artifact
     */
    private final String name;

    private final String category;
    private final Scm scm;
    private final Date now;
    private final String groupId;
    private final Map<String, Object> templateContextAdditions = new HashMap<String, Object>();
    private final Map<String, String> parsedTemplates = new HashMap<String, String>();
    private Profile profile;


    public Job(String name, String category, Scm scm, Date now, String groupId) {
        this.name = name;
        this.category = category;
        this.scm = scm;
        this.now = now;
        this.groupId = groupId;
    }
    public static Job create(SoftwareAsset asset, World world, PrintStream log) {
        Scm scm;
        if (asset.scm().equals("system")) {
            scm = null;
        } else {
            scm = Scm.create(asset.scm(), world, log);
        }
        return new Job(asset.artifactId(), null, scm, new Date(), asset.groupId());
    }

    public void addContext(String key, Object value) {
        templateContextAdditions.put(key, value);
    }


    public void sendToJenkins(PrintStream log) throws IOException, TemplateException {
        if (this.profile == null) {
            log.println(String.format("Profile for %s not found.", name));
            return;
        }
        log.println(String.format("Creating Jobs for %s | Profile: %s ", name, this.profile.getName()));

        for (Map.Entry<String, String> entry : profile.getXmls().entrySet()) {

            String jobXml = parseTemplate(entry.getKey(), entry.getValue());
            if (jobXml.length() == 0) {
                log.println(String.format("%s of %s has been ignored. Zero length| Profile: %s ",entry.getKey(), name, this.profile.getName()));
                continue;
            }
            sendJobToJenkins(createIdentifier(entry.getKey()), jobXml, log);
        }
    }

    protected String parseTemplate(String name, String template) throws IOException, TemplateException {
        Writer writer;
        Reader reader;
        Template freeMarkertemplate;
        writer = new StringWriter();

        reader = new StringReader(template);
        freeMarkertemplate = new Template(createIdentifier(name), reader, new Configuration());
        freeMarkertemplate.process(toTemplateContext(), writer);
        return writer.toString();
    }
    private void sendJobToJenkins(String name, String xml, PrintStream log) throws IOException {

        InputStream src;
        src = new ByteArrayInputStream(xml.getBytes());
        TopLevelItem item = Jenkins.getInstance().getItem(name);
        if (item == null) {

            try {
                Jenkins.getInstance()
                  .createProjectFromXML(name, src);
                log.println(String.format("Job %s created", name));
            } catch (IOException2 e) {
                log.println("could not parse because" + e.getMessage());
                log.println(xml);
            } finally {
                src.close();
            }
        } else {
            Source source = new StreamSource(src);
            if (!((AbstractItem) item).getConfigFile().toString().equals(xml)) {
                ((AbstractItem)item).updateByXml(source);
                log.println(String.format("Job %s updated", name));
            }
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
        context = new HashMap<String, Object>(templateContextAdditions);
        context.put("name", name);
        context.put("now", now.toString());
        context.put("usedProfile", profile.getName());
        context.put("id", createIdentifier("build.xml"));
        context.put("scm", Strings.removeLeftOpt((scm != null ? scm.uri() : ""), "svn:"));
        context.put("scmCredentials", credentialsForScm());
        return context;
    }

    private String credentialsForScm() {
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

    public void setProfile(Profile profile) {
        this.profile = profile;
    }


}
