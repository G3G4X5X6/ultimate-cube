package com.g3g4x5x6.ui.panels.ssh.monitor;

import com.g3g4x5x6.App;

import javax.swing.*;
import java.awt.*;

public class ShowEditorPane extends JDialog {
    private JEditorPane editorPane;

    public ShowEditorPane() {
        super(App.mainFrame);
        this.setLayout(new BorderLayout());
        this.setVisible(true);
        this.setSize(new Dimension(600, 200));
        this.setLocationRelativeTo(App.mainFrame);
        editorPane = new JEditorPane();
        editorPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void setText(String text) {
        editorPane.setText(text);
    }

    public void cleanText() {
        editorPane.setText("");
    }
}