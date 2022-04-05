package com.g3g4x5x6.panels.ssh.panel;

import javax.swing.*;
import java.awt.*;


/**
 * 新增面板的主面板
 */
public class NewTabbedPane extends JPanel {
    // TODO 新建选项卡面板： 会话列表、新增会话、其他

    public NewTabbedPane(JTabbedPane tabbedPane) {
        this.setLayout(new BorderLayout());

        this.add(new CreateSessionTabbedPane(tabbedPane), BorderLayout.CENTER);
    }
}
