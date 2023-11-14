package com.g3g4x5x6.dashboard;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.remote.SessionManagerPanel;
import com.g3g4x5x6.remote.RecentSessionPane;

import javax.swing.*;


/**
 * 仪表盘面板
 */
public class QuickStartTabbedPane extends JTabbedPane {

    public QuickStartTabbedPane() {
        this.addTab("最近会话", new FlatSVGIcon("icons/ShowRecentTabStripItems(Color).svg"), new RecentSessionPane());
        this.addTab("会话管理", new FlatSVGIcon("icons/addList.svg"), new SessionManagerPanel());
        this.addTab("备忘笔记", new FlatSVGIcon("icons/addNote.svg"), new NotePane());
    }
}
