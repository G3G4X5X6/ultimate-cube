package com.g3g4x5x6.ui.panels.dashboard;

import com.g3g4x5x6.ui.panels.console.ConsolePane;
import com.g3g4x5x6.ui.panels.dashboard.quickstarter.QuickStarterPane;

import javax.swing.*;
import java.awt.*;


/**
 * 仪表盘面板
 */
public class DashboardPane extends JPanel {
    private BorderLayout borderLayout = new BorderLayout();
    private JTabbedPane tabbedPane = new JTabbedPane();

    public DashboardPane() {
        this.setLayout(borderLayout);
        this.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("快速启动", new QuickStarterPane());
        tabbedPane.addTab("本地终端", new ConsolePane());
        tabbedPane.addTab("网络连接", null,  new ConnectionPane(), "默认10分钟刷新一次");
        tabbedPane.addTab("系统信息", null, new SysinfoPane(), "默认10分钟刷新一次");
        tabbedPane.addTab("备忘笔记", new NotePane());
        tabbedPane.addTab("日志记录", new LogPane());
    }
}
