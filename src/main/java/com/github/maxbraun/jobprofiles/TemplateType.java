package com.github.maxbraun.jobprofiles;
public enum TemplateType {
    XML("xml"), JOBDSL("dsl");

    private final String extension;

    TemplateType (String extension) {
        this.extension = extension;
    }

    public static TemplateType fromExtension(String extension) {
        for (TemplateType templateType : values()) {
            if (templateType.extension.equals(extension)) {
                return templateType;
            }
        }

        throw new IllegalArgumentException( extension + " is unkown");

    }
}
