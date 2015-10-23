package com.github.maxbraun.jobprofiles;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import net.oneandone.sushi.util.Strings;
public class Template {
    private static final String DELEMITER = "_";

    private final String name;
    private final String content;

    public Template(String name, String content) {
        this.name = name;
        this.content = content;
    }
    public String name() {
        return name;
    }

    public Job parseWith(SoftwareAsset asset, JobContext jobContext) throws IOException, TemplateException {
        Map<String, Object> context = new HashMap<String, Object>();
        if (jobContext != null) {
            context.putAll(jobContext.contextMap());
        }
        String identifier = identifierFor(asset);
        context.put("id", identifier);

        return parseTemplate(identifier, context);

    }

    private String identifierFor(SoftwareAsset asset) {
        String key = String.format("%s%s%s", asset.groupId(), DELEMITER, asset.artifactId());

        if (!name.equals("build.xml")) {
            return String.format("%s%s%s", key, DELEMITER, Strings.removeRight(name, ".xml"));
        }
        return key;
    }

    protected Job parseTemplate(String identifier, Map<String, Object> context) throws IOException, TemplateException {
        Writer writer;
        Reader reader;
        freemarker.template.Template freeMarkertemplate;
        writer = new StringWriter();

        reader = new StringReader(content);
        freeMarkertemplate = new freemarker.template.Template(identifier, reader, new Configuration());
        freeMarkertemplate.process(context, writer);
        return new Job(identifier, writer.toString());
    }
}
