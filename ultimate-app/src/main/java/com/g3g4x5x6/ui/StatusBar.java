package com.g3g4x5x6.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.icon.SizeGripIcon;

import javax.swing.*;
import java.awt.*;


public class StatusBar extends JPanel {
    private JToolBar toolBar = new JToolBar();
    private JLabel statusLabel;

    public StatusBar() {
        this.setLayout(new BorderLayout());
        this.setBorder(null);
        this.add(toolBar, BorderLayout.CENTER);

        initToolbar();

        // 总是在最后
        add(new JLabel(new SizeGripIcon()), BorderLayout.LINE_END);
    }

    private void initToolbar() {
        toolBar.setBorder(null);

        JLabel spaceLabel = new JLabel(" ");
        statusLabel = new JLabel("");
        Font labelFont = statusLabel.getFont();
        statusLabel.setFont(new Font(labelFont.getName(), labelFont.getStyle(), labelFont.getSize() - 2));
        statusLabel.setIcon(new FlatSVGIcon("icons/green.svg"));

        toolBar.add(spaceLabel);
        toolBar.add(statusLabel);
    }

    public void setStatusInfoText(String text) {
        statusLabel.setText(text);
    }
}
