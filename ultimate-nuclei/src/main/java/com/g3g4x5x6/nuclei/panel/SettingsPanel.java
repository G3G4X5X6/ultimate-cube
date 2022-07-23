package com.g3g4x5x6.nuclei.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.nuclei.panel.settings.*;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private final JButton newBtn = new JButton(new FlatSVGIcon("icons/addFile.svg"));
    private final JButton openBtn = new JButton(new FlatSVGIcon("icons/menu-open.svg"));
    private final JButton saveBtn = new JButton(new FlatSVGIcon("icons/menu-saveall.svg"));
    private final JButton terminalBtn = new JButton(new FlatSVGIcon("icons/changeView.svg"));

    public static JTabbedPane tabbedPane;

    // TODO 搞个全局配置对象

    public SettingsPanel() {
        this.setLayout(new BorderLayout());

        initToolBar();

        initTabbedPane();
    }

    private void initToolBar(){
        JToolBar toolBar = new JToolBar(SwingConstants.VERTICAL);
        toolBar.setFloatable(false);
        toolBar.add(newBtn);
        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        toolBar.addSeparator();
        toolBar.add(terminalBtn);
        this.add(toolBar, BorderLayout.EAST);
    }

    private void initTabbedPane(){
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        // add Tab
        SettingTarget targetSetting = new SettingTarget();
        tabbedPane.addTab("Target", new FlatSVGIcon("icons/Target.svg"), targetSetting);

        SettingTemplate templateSetting = new SettingTemplate();
        tabbedPane.addTab("Templates", new FlatSVGIcon("icons/pinTab.svg"), templateSetting);

        SettingFiltering filteringSetting = new SettingFiltering();
        tabbedPane.addTab("Filtering", new FlatSVGIcon("icons/pinTab.svg"), filteringSetting);

        SettingOutput outputSetting = new SettingOutput();
        tabbedPane.addTab("Output", new FlatSVGIcon("icons/pinTab.svg"), outputSetting);

        SettingConfiguration configurationSetting = new SettingConfiguration();
        tabbedPane.addTab("Configurations", new FlatSVGIcon("icons/pinTab.svg"), configurationSetting);

        SettingInteractsh interactshSetting = new SettingInteractsh();
        tabbedPane.addTab("Interactsh", new FlatSVGIcon("icons/pinTab.svg"), interactshSetting);

        SettingRateLimit rateLimitSetting = new SettingRateLimit();
        tabbedPane.addTab("RateLimit", new FlatSVGIcon("icons/pinTab.svg"), rateLimitSetting);

        SettingOptimization optimizationSetting = new SettingOptimization();
        tabbedPane.addTab("Optimizations", new FlatSVGIcon("icons/pinTab.svg"), optimizationSetting);

        SettingHeadless headlessSetting = new SettingHeadless();
        tabbedPane.addTab("Headless", new FlatSVGIcon("icons/pinTab.svg"), headlessSetting);

        SettingDebug debugSetting = new SettingDebug();
        tabbedPane.addTab("Debug", new FlatSVGIcon("icons/pinTab.svg"), debugSetting);

        SettingUpdate updateSetting = new SettingUpdate();
        tabbedPane.addTab("Update", new FlatSVGIcon("icons/pinTab.svg"), updateSetting);

        SettingStatistics statisticsSetting = new SettingStatistics();
        tabbedPane.addTab("Statistics", new FlatSVGIcon("icons/pinTab.svg"), statisticsSetting);

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

}
