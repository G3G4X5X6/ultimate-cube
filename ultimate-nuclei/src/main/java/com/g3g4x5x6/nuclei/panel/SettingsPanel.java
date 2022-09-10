package com.g3g4x5x6.nuclei.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.nuclei.panel.settings.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SettingsPanel extends JPanel {
    // TODO 搞个全局配置对象

    private final JButton newBtn = new JButton(new FlatSVGIcon("icons/addFile.svg"));
    private final JButton openBtn = new JButton(new FlatSVGIcon("icons/menu-open.svg"));
    private final JButton saveBtn = new JButton(new FlatSVGIcon("icons/menu-saveall.svg"));
    private final JButton terminalBtn = new JButton(new FlatSVGIcon("icons/changeView.svg"));

    public static JTabbedPane tabbedPane;
    SettingTarget targetSetting = new SettingTarget();
    SettingTemplate templateSetting = new SettingTemplate();
    SettingFiltering filteringSetting = new SettingFiltering();
    SettingOutput outputSetting = new SettingOutput();
    SettingConfiguration configurationSetting = new SettingConfiguration();
    SettingInteractsh interactshSetting = new SettingInteractsh();
    SettingRateLimit rateLimitSetting = new SettingRateLimit();
    SettingOptimization optimizationSetting = new SettingOptimization();
    SettingHeadless headlessSetting = new SettingHeadless();
    SettingDebug debugSetting = new SettingDebug();
    SettingUpdate updateSetting = new SettingUpdate();
    SettingStatistics statisticsSetting = new SettingStatistics();

    public SettingsPanel() {
        this.setLayout(new BorderLayout());

        initToolBar();

        initTabbedPane();
    }

    private void initToolBar(){
        JToolBar toolBar = new JToolBar(SwingConstants.HORIZONTAL);
        toolBar.setFloatable(false);

        newBtn.setToolTipText("清空当前目标");
        newBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                targetSetting.clearTargets();
            }
        });

        toolBar.add(newBtn);
        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        toolBar.addSeparator();
        toolBar.add(terminalBtn);
        this.add(toolBar, BorderLayout.NORTH);
    }

    private void initTabbedPane(){
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        // add Tab
        tabbedPane.addTab("Target", new FlatSVGIcon("icons/Target.svg"), targetSetting);
        tabbedPane.addTab("Templates", new FlatSVGIcon("icons/pinTab.svg"), templateSetting);
        tabbedPane.addTab("Filtering", new FlatSVGIcon("icons/pinTab.svg"), filteringSetting);
        tabbedPane.addTab("Output", new FlatSVGIcon("icons/pinTab.svg"), outputSetting);
        tabbedPane.addTab("Configurations", new FlatSVGIcon("icons/pinTab.svg"), configurationSetting);
        tabbedPane.addTab("Interactsh", new FlatSVGIcon("icons/pinTab.svg"), interactshSetting);
        tabbedPane.addTab("RateLimit", new FlatSVGIcon("icons/pinTab.svg"), rateLimitSetting);
        tabbedPane.addTab("Optimizations", new FlatSVGIcon("icons/pinTab.svg"), optimizationSetting);
        tabbedPane.addTab("Headless", new FlatSVGIcon("icons/pinTab.svg"), headlessSetting);
        tabbedPane.addTab("Debug", new FlatSVGIcon("icons/pinTab.svg"), debugSetting);
        tabbedPane.addTab("Update", new FlatSVGIcon("icons/pinTab.svg"), updateSetting);
        tabbedPane.addTab("Statistics", new FlatSVGIcon("icons/pinTab.svg"), statisticsSetting);

        this.add(tabbedPane, BorderLayout.CENTER);
    }
}
