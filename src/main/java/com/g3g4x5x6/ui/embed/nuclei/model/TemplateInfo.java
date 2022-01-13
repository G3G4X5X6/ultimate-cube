package com.g3g4x5x6.ui.embed.nuclei.model;

public class TemplateInfo {
    private String name;
    private String author;
    private String severity;
    private String description;
    private String reference;
    private String tags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "TemplateInfo{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", severity='" + severity + '\'' +
                ", description='" + description + '\'' +
                ", reference='" + reference + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
