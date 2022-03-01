package com.g3g4x5x6.ui.embed.nuclei.model;

import com.g3g4x5x6.utils.ConfigUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SelectedTemplatesConfig {
    private List<String> target = new LinkedList<>();
    private List<String> templates = new LinkedList<>();
    private List<String> workflows = new LinkedList<>();

    public SelectedTemplatesConfig() {

    }

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }

    public List<String> getTemplates() {
        return templates;
    }

    public void setTemplates(List<String> templates) {
        this.templates = templates;
    }

    public List<String> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<String> workflows) {
        this.workflows = workflows;
    }

    public void addTemplate(String templatesPath) {
        templates.add(templatesPath);
    }

    public void addWorkflow(String workflowPath) {
        workflows.add(workflowPath);
    }

    public static void main(String[] args) throws IOException {
        String targetPath = ConfigUtil.getWorkPath() + "/temp/nuclei/" + UUID.randomUUID() + ".yaml";

        List<String> target = new ArrayList<>();
        List<String> templates = new ArrayList<>();
        target.add("http://baidu.com");
        target.add("https://www.baidu.com");
        templates.add("C:\\Users\\18224\\nuclei-templates\\misconfiguration\\springboot\\springboot-env.yaml");
        templates.add("C:\\Users\\18224\\nuclei-templates\\misconfiguration\\springboot\\springboot-httptrace.yaml");
        SelectedTemplatesConfig selectedTemplatesConfig = new SelectedTemplatesConfig();
        selectedTemplatesConfig.setTarget(target);
        selectedTemplatesConfig.setTemplates(templates);
        Yaml yaml = new Yaml();
        yaml.dump(selectedTemplatesConfig, new FileWriter(targetPath));
    }
}
