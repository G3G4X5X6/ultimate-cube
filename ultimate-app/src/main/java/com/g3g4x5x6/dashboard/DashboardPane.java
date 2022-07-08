package com.g3g4x5x6.dashboard;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.dashboard.quickstarter.QuickStarterPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * 仪表盘面板
 */
public class DashboardPane extends JPanel {
    private BorderLayout borderLayout = new BorderLayout();
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JToolBar statusBar = new JToolBar();
    private JPopupMenu statusPopupMenu = new JPopupMenu();

    public DashboardPane() {
        this.setLayout(borderLayout);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(statusBar, BorderLayout.SOUTH);
        tabbedPane.addTab("快速启动", new QuickStarterPane());
//        tabbedPane.addTab("本地终端", new ConsolePane());
        tabbedPane.addTab("网络连接", null, new ConnectionPane(), "手动刷新");
        tabbedPane.addTab("系统信息", null, new SysinfoPane(), "默认10分钟刷新一次");
        tabbedPane.addTab("备忘笔记", new NotePane());
//        tabbedPane.addTab("日志记录", new LogPane());

        initStatusBar();

        initPopupMenu();

    }

    private void initStatusBar() {
        statusBar.setFloatable(false);

        /**
         * <html>Power by <font style='color:blue;'><a href='https://github.com/G3G4X5X6/ultimateshell'>G3G4X5X6</a></font></html>
         */
        statusBar.add(new JLabel("<html>Power by <font style='color:blue;'><a href='https://github.com/G3G4X5X6/ultimateshell'>G3G4X5X6</a></font></html>"));
        statusBar.add(Box.createHorizontalGlue());
        statusBar.add(new JLabel("Ready"));
    }

    private void initPopupMenu() {
        // TODO statusPopupMenu Items
        // 生成随机密码
        JMenuItem passwdItem = new JMenuItem("生成随机密码");
        passwdItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        passwdItem.setIcon(new FlatSVGIcon("icons/shield.svg"));
        statusPopupMenu.add(passwdItem);
    }
}
