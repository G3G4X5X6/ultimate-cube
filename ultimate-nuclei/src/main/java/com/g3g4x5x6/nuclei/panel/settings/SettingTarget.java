package com.g3g4x5x6.nuclei.panel.settings;

import com.g3g4x5x6.nuclei.panel.settings.target.StringTargetPanel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;


@Slf4j
public class SettingTarget extends JPanel {
    public static StringTargetPanel stringTargetPanel;

    private static SettingTarget settingTarget;

    public static SettingTarget getInstance(){
        if (settingTarget == null){
            settingTarget = new SettingTarget();
        }
        return settingTarget;
    }

    public SettingTarget() {
        this.setLayout(new BorderLayout());

        stringTargetPanel = new StringTargetPanel();

        this.add(stringTargetPanel, BorderLayout.CENTER);
    }

    public String getTargets(){
        String targets;
        // TODO

        return "";
    }

    public void clearTargets(){
        stringTargetPanel.getTextArea().setText("");
    }
}
