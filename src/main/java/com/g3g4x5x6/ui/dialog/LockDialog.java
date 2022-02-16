package com.g3g4x5x6.ui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.g3g4x5x6.App;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


@Slf4j
public class LockDialog extends JDialog {
    private final JPasswordField passwordField = new JPasswordField();

    public LockDialog() {
        super(App.mainFrame);
        this.setTitle("应用锁");
        this.setSize(new Dimension(280, 130));
        this.setPreferredSize(new Dimension(280, 130));
        this.setLocationRelativeTo(App.mainFrame);
        this.setModal(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
            }
        });

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.CENTER);
        flowLayout.setVgap(25);
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        passwordField.putClientProperty("JTextField.placeholderText", "回车即锁定应用");
        passwordField.setColumns(10);
        passwordField.registerKeyboardAction(e -> {
                    log.debug(String.valueOf(passwordField.getPassword()));
                    dispose();
                    // TODO lock
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
        this.setLayout(flowLayout);
        this.add(new JLabel("解锁密码："));
        this.add(passwordField);
    }
}
