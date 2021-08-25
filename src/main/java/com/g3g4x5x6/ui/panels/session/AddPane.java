package com.g3g4x5x6.ui.panels.session;

import javax.swing.*;
import java.awt.*;

/**
 * 选项卡添加按钮面板
 */
public class AddPane extends JPanel {

    private JTabbedPane mainTabbedPane;

    public AddPane(JTabbedPane mainTabbedPane) {
        this.setLayout(new BorderLayout());

        this.mainTabbedPane = mainTabbedPane;
        this.add(new CreateSessionTabbedPane(mainTabbedPane), BorderLayout.CENTER);
    }
}
