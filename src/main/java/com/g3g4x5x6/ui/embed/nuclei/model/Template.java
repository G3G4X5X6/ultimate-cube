package com.g3g4x5x6.ui.embed.nuclei.model;

public class Template {
    private String id;
    private TemplateInfo info;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TemplateInfo getInfo() {
        return info;
    }

    public void setInfo(TemplateInfo info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Template{" +
                "id='" + id + '\'' +
                ", info=" + info +
                '}';
    }
}
