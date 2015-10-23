package com.github.maxbraun.jobprofiles;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.TemplateException;
import net.oneandone.sushi.fs.Node;

public class Profile {

    public static Profile fromDirectory(Node directory) throws IOException {
        directory.checkDirectory();
        Profile profile = new Profile(directory.getName());
        for (Node file : directory.list()) {
            if (file.isFile()) {
                Template template = new Template(file.getName(), file.readString());
                profile.addTemplate(template);
            }
        }

        return profile;
    }

    private final String name;

    private final Map<String, Template> templates = new HashMap<String, Template>();

    public Profile(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    private void addTemplate(Template template) {
        templates.put(template.name(), template);
    }

    private Collection<Template> templates() {
        return Collections.unmodifiableCollection(templates.values());
    }

    public int amountOfTemplates(){
        return templates.size();
    }

    public Jobs parse(SoftwareAsset asset, JobContext context) throws IOException, TemplateException {
        Jobs jobs = new Jobs();
        for (Template template : templates()) {
            jobs.add(template.parseWith(asset, context));
        }

        return jobs;
    }
}
