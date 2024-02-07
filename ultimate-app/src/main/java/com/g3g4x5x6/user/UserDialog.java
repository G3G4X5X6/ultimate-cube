package com.g3g4x5x6.user;

import com.g3g4x5x6.App;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class UserDialog extends JDialog {
    private JToolBar toolBar = new JToolBar();

    public UserDialog() {
        this.setTitle("用户中心");
        this.setSize(new Dimension(500, 550));
        this.setModal(true);
        this.setLocationRelativeTo(App.mainFrame);
        this.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("app.png"))).getImage());

        this.setLayout(new BorderLayout());
        initToolbar();
    }

    private void initToolbar() {
        toolBar.setFloatable(false);
        toolBar.add(new JButton("Test"));
        this.add(toolBar, BorderLayout.NORTH);
    }
}
