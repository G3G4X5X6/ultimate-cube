package com.g3g4x5x6.dashboard;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.MainFrame;
import com.g3g4x5x6.remote.RecentSessionPane;
import com.g3g4x5x6.panel.SessionManagerPanel;

import javax.swing.*;
import java.awt.*;


/**
 * 仪表盘面板
 */
public class QuickStartPane extends JPanel {

    public QuickStartPane() {
        this.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("最近会话", new FlatSVGIcon("icons/ShowRecentTabStripItems(Color).svg"), new RecentSessionPane());
        tabbedPane.addTab("会话管理", new FlatSVGIcon("icons/addList.svg"), new SessionManagerPanel(MainFrame.mainTabbedPane));
        tabbedPane.addTab("备忘笔记", new FlatSVGIcon("icons/addNote.svg"), new NotePane());

        this.add(tabbedPane, BorderLayout.CENTER);
    }
}
