package com.g3g4x5x6.nuclei.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.nuclei.panel.settings.*;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JButton newBtn = new JButton(new FlatSVGIcon("icons/addFile.svg"));
    private JButton openBtn = new JButton(new FlatSVGIcon("icons/menu-open.svg"));
    private JButton saveBtn = new JButton(new FlatSVGIcon("icons/menu-saveall.svg"));
    private JButton terminalBtn = new JButton(new FlatSVGIcon("icons/changeView.svg"));

    private JToolBar toolBar;
    private JTabbedPane tabbedPane;

    public SettingsPanel() {
        this.setLayout(new BorderLayout());

        initToolBar();

        initTabbedPane();
    }

    private void initToolBar(){
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
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
        TargetSetting targetSetting = new TargetSetting();
        tabbedPane.addTab("Target", new FlatSVGIcon("icons/pinTab.svg"), targetSetting);

        TemplateSetting templateSetting = new TemplateSetting();
        tabbedPane.addTab("Templates", new FlatSVGIcon("icons/pinTab.svg"), templateSetting);

        FilteringSetting filteringSetting = new FilteringSetting();
        tabbedPane.addTab("Filtering", new FlatSVGIcon("icons/pinTab.svg"), filteringSetting);

        OutputSetting outputSetting = new OutputSetting();
        tabbedPane.addTab("Output", new FlatSVGIcon("icons/pinTab.svg"), outputSetting);

        ConfigurationSetting configurationSetting = new ConfigurationSetting();
        tabbedPane.addTab("Configurations", new FlatSVGIcon("icons/pinTab.svg"), configurationSetting);

        InteractshSetting interactshSetting = new InteractshSetting();
        tabbedPane.addTab("Interactsh", new FlatSVGIcon("icons/pinTab.svg"), interactshSetting);

        RateLimitSetting rateLimitSetting = new RateLimitSetting();
        tabbedPane.addTab("RateLimit", new FlatSVGIcon("icons/pinTab.svg"), rateLimitSetting);

        OptimizationSetting optimizationSetting = new OptimizationSetting();
        tabbedPane.addTab("Optimizations", new FlatSVGIcon("icons/pinTab.svg"), optimizationSetting);

        HeadlessSetting headlessSetting = new HeadlessSetting();
        tabbedPane.addTab("Headless", new FlatSVGIcon("icons/pinTab.svg"), headlessSetting);

        DebugSetting debugSetting = new DebugSetting();
        tabbedPane.addTab("Debug", new FlatSVGIcon("icons/pinTab.svg"), debugSetting);

        UpdateSetting updateSetting = new UpdateSetting();
        tabbedPane.addTab("Update", new FlatSVGIcon("icons/pinTab.svg"), updateSetting);

        StatisticsSetting statisticsSetting = new StatisticsSetting();
        tabbedPane.addTab("Statistics", new FlatSVGIcon("icons/pinTab.svg"), statisticsSetting);

        this.add(tabbedPane, BorderLayout.CENTER);
    }
}
