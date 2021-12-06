package com.g3g4x5x6.ui.panels;

import com.g3g4x5x6.ui.MainFrame;

import javax.swing.*;
import java.awt.*;


/**
 * 新增面板的主面板
 */
public class NewTabbedPane extends JPanel {
    // TODO 新建选项卡面板： 会话列表、新增会话、其他

    public NewTabbedPane() {
        this.setLayout(new BorderLayout());

        this.add(new CreateSessionTabbedPane(MainFrame.mainTabbedPane), BorderLayout.CENTER);
    }
}
