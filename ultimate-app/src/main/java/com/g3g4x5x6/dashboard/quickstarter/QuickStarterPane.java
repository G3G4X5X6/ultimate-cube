package com.g3g4x5x6.dashboard.quickstarter;


import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.MainFrame;
import com.g3g4x5x6.panels.ssh.panel.RecentSessionsPanel;
import com.g3g4x5x6.panels.ssh.panel.SessionManagerPanel;
import com.g3g4x5x6.utils.DialogUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * 仪表板的快速启动面板
 */
@Slf4j
public class QuickStarterPane extends JPanel {

    private BorderLayout borderLayout = new BorderLayout();
    private JTabbedPane mainTabbedPane;

    private JTabbedPane basicSettingTabbedPane;
    private String basicSettingPaneTitle = "基本设置(SSH)";
    private BasicSettingStarterPane basicSettingPane;

    private JTabbedPane recentSessionTabbedPane;
    private String recentSessionPaneTitle = "最近的会话";
    private RecentSessionsPanel recentSessionPane;
    private String sessionsManagerTitle = "会话管理";
    private SessionManagerPanel sessionsManager;

    public QuickStarterPane() {
        this.mainTabbedPane = MainFrame.mainTabbedPane;
        this.setLayout(borderLayout);

        initQuickPane();

        log.info(">>>>>>>> “快速启动”窗口初始化完成......");
    }

    private void initQuickPane() {

        // 基本设置
        basicSettingTabbedPane = new JTabbedPane();
        basicSettingPane = new BasicSettingStarterPane();
        basicSettingTabbedPane.addTab(basicSettingPaneTitle, basicSettingPane);

        // 会话管理
        recentSessionTabbedPane = new JTabbedPane();
        recentSessionPane = new RecentSessionsPanel();
        sessionsManager = new SessionManagerPanel(mainTabbedPane);
        recentSessionTabbedPane.addTab(recentSessionPaneTitle, new FlatSVGIcon("icons/ShowRecentTabStripItems(Color).svg"), recentSessionPane);
        recentSessionTabbedPane.addTab(sessionsManagerTitle, new FlatSVGIcon("icons/addList.svg"), sessionsManager);

        this.add(basicSettingTabbedPane, BorderLayout.NORTH);
        this.add(recentSessionTabbedPane, BorderLayout.CENTER);
    }

    private AbstractAction generatePassAction = new AbstractAction("生成随机密码") {
        @Override
        public void actionPerformed(ActionEvent e) {
            DialogUtil.info("敬请期待！");
        }
    };

}
