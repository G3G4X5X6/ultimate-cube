package com.g3g4x5x6.ui.panels.dashboard.quickstarter;


import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;


/**
 * 仪表板的快速启动面板
 */
public class QuickStarterPane extends JPanel {
    static final Logger logger = Logger.getLogger(QuickStarterPane.class);

    private BorderLayout borderLayout = new BorderLayout();

    private JTabbedPane mainTabbedPane;

    // TODO
    protected JTabbedPane basicSettingTabbedPane;
    protected String basicSettingPaneTitle = "基本设置(SSH)";
    protected BasicSettingStarterPane basicSettingPane;

    protected JTabbedPane recentSessionTabbedPane;
    protected String recentSessionPaneTitle = "最近的会话";
    protected RecentSessionsPane recentSessionPane;
    protected String sessionsManagerTitle = "会话管理";
    protected SessionsManager sessionsManager;


    public QuickStarterPane(JTabbedPane mainTabbedPane) {
        this.mainTabbedPane = mainTabbedPane;
        this.setLayout(borderLayout);

        initQuickPane();
        logger.info("初始化<快速启动>面板完成");
    }

    protected void initQuickPane() {

        // 基本设置
        basicSettingTabbedPane = new JTabbedPane();
        basicSettingPane = new BasicSettingStarterPane(mainTabbedPane);
        basicSettingTabbedPane.addTab(basicSettingPaneTitle, basicSettingPane);

        // 会话管理
        recentSessionTabbedPane = new JTabbedPane();
        recentSessionPane = new RecentSessionsPane(mainTabbedPane);
        sessionsManager = new SessionsManager(mainTabbedPane);
        recentSessionTabbedPane.addTab(recentSessionPaneTitle, recentSessionPane);
        recentSessionTabbedPane.addTab(sessionsManagerTitle, sessionsManager);

        this.add(basicSettingTabbedPane, BorderLayout.NORTH);
        this.add(recentSessionTabbedPane, BorderLayout.CENTER);
    }

}
