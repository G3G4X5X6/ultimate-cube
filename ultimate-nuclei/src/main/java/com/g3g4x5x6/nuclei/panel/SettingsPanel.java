package com.g3g4x5x6.nuclei.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JButton newBtn = new JButton(new FlatSVGIcon("icons/addFile.svg"));
    private JButton openBtn = new JButton(new FlatSVGIcon("icons/menu-open.svg"));
    private JButton saveBtn = new JButton(new FlatSVGIcon("icons/menu-saveall.svg"));
    private JButton terminalBtn = new JButton(new FlatSVGIcon("icons/changeView.svg"));

    public SettingsPanel() {
        this.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(newBtn);
        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        toolBar.addSeparator();
        toolBar.add(terminalBtn);
        this.add(toolBar, BorderLayout.NORTH);
        this.add(new JLabel("待定"), BorderLayout.CENTER);
    }
}
