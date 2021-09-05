package com.g3g4x5x6.ui;

import javax.swing.*;
import java.awt.*;


/**
 * 选项卡标题面板
 */
public class TabbedTitlePane extends JPanel {
    private JLabel titleLabel = new JLabel("新建选项卡");

    public TabbedTitlePane(String title, JTabbedPane mainTabbedPane, CloseButton closeButton) {
        super();
        FlowLayout flowLayout = new FlowLayout();
        this.setLayout(flowLayout);
        this.setOpaque(false);
        this.setBorder(null);

        titleLabel.setText(title);

        this.add(titleLabel);
        this.add(closeButton);

    }

}
