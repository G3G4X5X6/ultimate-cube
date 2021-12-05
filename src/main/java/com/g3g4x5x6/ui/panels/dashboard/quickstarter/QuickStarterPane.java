package com.g3g4x5x6.ui.panels.dashboard.quickstarter;


import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.ui.panels.SessionsManager;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;


/**
 * 仪表板的快速启动面板
 */
@Slf4j
public class QuickStarterPane extends JPanel {

    private BorderLayout borderLayout = new BorderLayout();
    private JTabbedPane mainTabbedPane;

    protected JTabbedPane basicSettingTabbedPane;
    protected String basicSettingPaneTitle = "基本设置(SSH)";
    protected BasicSettingStarterPane basicSettingPane;

    protected JTabbedPane recentSessionTabbedPane;
    protected String recentSessionPaneTitle = "最近的会话";
    protected RecentSessionsPane recentSessionPane;
    protected String sessionsManagerTitle = "会话管理";
    protected SessionsManager sessionsManager;


    public QuickStarterPane() {
        this.mainTabbedPane = MainFrame.mainTabbedPane;
        this.setLayout(borderLayout);

        initQuickPane();
        log.info(">>>>>>>> “快速启动”窗口初始化完成......");
    }

    protected void initQuickPane() {

        // 基本设置
        basicSettingTabbedPane = new JTabbedPane();
        basicSettingPane = new BasicSettingStarterPane();
        basicSettingTabbedPane.addTab(basicSettingPaneTitle, basicSettingPane);

        // 会话管理
        recentSessionTabbedPane = new JTabbedPane();
        recentSessionPane = new RecentSessionsPane(mainTabbedPane);
        sessionsManager = new SessionsManager(mainTabbedPane);
        recentSessionTabbedPane.addTab(recentSessionPaneTitle, new FlatSVGIcon("com/g3g4x5x6/ui/icons/ShowRecentTabStripItems(Color).svg"), recentSessionPane);
        recentSessionTabbedPane.addTab(sessionsManagerTitle, new FlatSVGIcon("com/g3g4x5x6/ui/icons/addList.svg"), sessionsManager);

        this.add(basicSettingTabbedPane, BorderLayout.NORTH);
        this.add(recentSessionTabbedPane, BorderLayout.CENTER);
    }

}
