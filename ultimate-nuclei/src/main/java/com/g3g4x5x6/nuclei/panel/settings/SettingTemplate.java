package com.g3g4x5x6.nuclei.panel.settings;


import com.g3g4x5x6.nuclei.panel.settings.template.GlobalTemplatePanel;
import com.g3g4x5x6.nuclei.panel.settings.template.GlobalWorkflowPanel;

import javax.swing.*;
import java.awt.*;

public class SettingTemplate extends JPanel {

    private GlobalTemplatePanel globalTemplatePanel;
    private GlobalWorkflowPanel globalWorkflowPanel;

    public SettingTemplate(){
        this.setLayout(new BorderLayout());

        globalTemplatePanel = new GlobalTemplatePanel();
        globalWorkflowPanel = new GlobalWorkflowPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(580);
        splitPane.setLeftComponent(globalTemplatePanel);
        splitPane.setRightComponent(globalWorkflowPanel);

        this.add(splitPane, BorderLayout.CENTER);
    }

}
