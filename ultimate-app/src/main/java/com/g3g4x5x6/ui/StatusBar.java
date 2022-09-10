package com.g3g4x5x6.ui;

import com.g3g4x5x6.ui.icon.SizeGripIcon;

import javax.swing.*;
import java.awt.*;


public class StatusBar extends JPanel {
    private final JLabel label;

    public StatusBar() {
        this.setLayout(new BorderLayout());

        label = new JLabel("就绪");
        add(label, BorderLayout.LINE_START);

        // 总是在最后
        add(new JLabel(new SizeGripIcon()), BorderLayout.LINE_END);
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }
}
