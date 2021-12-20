package com.g3g4x5x6.ui.panels.dashboard;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.panels.console.ConsolePane;
import com.g3g4x5x6.ui.panels.dashboard.quickstarter.QuickStarterPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


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
        tabbedPane.addTab("本地终端", new ConsolePane());
        tabbedPane.addTab("网络连接", null,  new ConnectionPane(), "手动刷新");
        tabbedPane.addTab("系统信息", null, new SysinfoPane(), "默认10分钟刷新一次");
        tabbedPane.addTab("备忘笔记", new NotePane());
//        tabbedPane.addTab("日志记录", new LogPane());

        initStatusBar();

        initPopupMenu();

    }

    private void initStatusBar(){
        statusBar.setFloatable(false);
        // TODO 添加状态栏组件
        JButton statusBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/colors.svg"));
        statusBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                statusPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        statusBar.add(statusBtn);
    }

    private void initPopupMenu(){
        // TODO statusPopupMenu Items
        // 生成随机密码
        JMenuItem passwdItem = new JMenuItem("生成随机密码");
        passwdItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        passwdItem.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/shield.svg"));
        statusPopupMenu.add(passwdItem);
    }
}
