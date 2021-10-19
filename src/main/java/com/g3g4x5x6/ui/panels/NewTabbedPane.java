package com.g3g4x5x6.ui.panels;

import javax.swing.*;
import java.awt.*;


/**
 * 新增面板的主面板
 */
public class NewTabbedPane extends JPanel {
    // TODO 新建选项卡面板： 会话列表、新增会话、其他

    private JTabbedPane mainTabbedPane;

    public NewTabbedPane(JTabbedPane mainTabbedPane) {
        this.setLayout(new BorderLayout());

        this.mainTabbedPane = mainTabbedPane;
        this.add(new CreateSessionTabbedPane(mainTabbedPane), BorderLayout.CENTER);
    }
}
