package com.g3g4x5x6.nuclei.panel.settings;

import com.g3g4x5x6.nuclei.panel.targetpanel.FileTargetPanel;
import com.g3g4x5x6.nuclei.panel.targetpanel.ResumeTargetPanel;
import com.g3g4x5x6.nuclei.panel.targetpanel.StringTargetPanel;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;


@Slf4j
public class SettingTarget extends JPanel implements ChangeListener {
    public static StringTargetPanel stringTargetPanel;
    public static FileTargetPanel fileTargetPanel;
    public static ResumeTargetPanel resumeTargetPanel;

    public static JRadioButton targetBtn = new JRadioButton("-target", true);
    public static JRadioButton listBtn = new JRadioButton("-list");
    public static JRadioButton resumeBtn = new JRadioButton("-resume");


    public SettingTarget() {
        this.setLayout(new BorderLayout());

        initOptionsComponents();

        JSplitPane lrPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane tbPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        lrPanel.setDividerLocation(550);
        tbPanel.setDividerLocation(300);

        stringTargetPanel = new StringTargetPanel();

        fileTargetPanel = new FileTargetPanel();
        resumeTargetPanel = new ResumeTargetPanel();

        tbPanel.setTopComponent(fileTargetPanel);
        tbPanel.setBottomComponent(resumeTargetPanel);

        lrPanel.setLeftComponent(stringTargetPanel);
        lrPanel.setRightComponent(tbPanel);

        this.add(lrPanel, BorderLayout.CENTER);
    }

    private void initOptionsComponents() {
        // 创建一个按钮组
        ButtonGroup btnGroup = new ButtonGroup();

        // 添加单选按钮到按钮组
        btnGroup.add(targetBtn);
        btnGroup.add(listBtn);
        btnGroup.add(resumeBtn);

        // 添加监听器
        targetBtn.addChangeListener(this);
        listBtn.addChangeListener(this);
        resumeBtn.addChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        log.debug("Something changed!");
    }
}
