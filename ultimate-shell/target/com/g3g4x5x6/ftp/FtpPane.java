package com.g3g4x5x6.ftp;


import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


@Slf4j
public class FtpPane extends JPanel {
    private JTabbedPane mainTabbedPane;
    private JTabbedPane basicSettingTabbedPane;
    private String basicSettingPaneTitle;
    private JPanel basicSettingPane;

    private JTabbedPane advancedSettingTabbedPane;
    private String advancedSettingPaneTitle;
    private JPanel advancedSettingPane;

    private JFormattedTextField hostField;
    private JFormattedTextField portField;
    private JTextField userField;
    private JPasswordField passField;

    public FtpPane(JTabbedPane mainTabbedPane) {
        this.mainTabbedPane = mainTabbedPane;
        this.setLayout(new BorderLayout());
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);

        basicSettingTabbedPane = new JTabbedPane();
        basicSettingPane = new JPanel();
        basicSettingPane.setLayout(flowLayout);
        basicSettingPaneTitle = "Basic FTP Settings";

        advancedSettingTabbedPane = new JTabbedPane();
        advancedSettingPane = new JPanel();
        advancedSettingPane.setLayout(flowLayout);
        advancedSettingPaneTitle = "Advanced FTP Settings";

        initBasicPane();
        initAdvancePane();

        this.add(basicSettingTabbedPane, BorderLayout.NORTH);
        this.add(advancedSettingTabbedPane, BorderLayout.CENTER);
    }

    private void initBasicPane() {
        basicSettingTabbedPane.addTab(basicSettingPaneTitle, basicSettingPane);
        // Host
        JPanel hostPane = new JPanel();
        JLabel hostLabel = new JLabel("Remote Host*");
        hostField = new JFormattedTextField();
        hostField.setColumns(10);
        hostPane.add(hostLabel);
        hostPane.add(hostField);

        // Port
        JPanel portPane = new JPanel();
        JLabel portLabel = new JLabel("Port*");
        portField = new JFormattedTextField();
        portField.setColumns(4);
        portField.setText("21");
        portPane.add(portLabel);
        portPane.add(portField);

        // User
        JPanel userPane = new JPanel();
        JLabel userLabel = new JLabel("Username");
        userField = new JTextField();
        userField.setColumns(8);
        userPane.add(userLabel);
        userPane.add(userField);

        // Pass
        JPanel passPane = new JPanel();
        JLabel passLabel = new JLabel("Password");
        passField = new JPasswordField();
        passField.setColumns(8);
        passField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        passPane.add(passLabel);
        passPane.add(passField);

        // 按钮
        JPanel btnPane = new JPanel();
        JButton saveBtn = new JButton("快速连接");
        saveBtn.setToolTipText("自动保存会话");
        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("FTP 快速连接");
                if (testOpen()) {
                    // TODO 保存会话

                    // 打开会话
                    mainTabbedPane.insertTab("FTP-" + hostField.getText(), new FlatSVGIcon("icons/OpenTerminal_13x13.svg"),
                            new JPanel(),
                            "FTP-" + hostField.getText(),
                            mainTabbedPane.getSelectedIndex());
                    mainTabbedPane.removeTabAt(mainTabbedPane.getSelectedIndex());
                } else {
                    JOptionPane.showMessageDialog(FtpPane.this, "敬请期待！", "警告", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        JButton testBtn = new JButton("测试通信");
        testBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("FTP 测试通信");
                if (testOpen()) {
                    JOptionPane.showMessageDialog(FtpPane.this, "敬请期待！", "警告", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(FtpPane.this, "敬请期待！", "警告", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        btnPane.add(saveBtn);
        btnPane.add(testBtn);

        basicSettingPane.add(hostPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(portPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(userPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(passPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(btnPane);
    }

    private void initAdvancePane() {
        advancedSettingTabbedPane.addTab(advancedSettingPaneTitle, advancedSettingPane);

    }

    private Boolean testOpen() {
        return false;
    }

    private @NotNull JediTermWidget createTerminalWidget() {
        JediTermWidget widget = new JediTermWidget(new FtpSettingsProvider());
        widget.setTtyConnector(createTtyConnector());
        widget.start();
        return widget;
    }

    @Deprecated
    private @NotNull TtyConnector createTtyConnector() {
        return new FtpTtyConnector(hostField.getText(), Integer.parseInt(portField.getText()));
    }
}
