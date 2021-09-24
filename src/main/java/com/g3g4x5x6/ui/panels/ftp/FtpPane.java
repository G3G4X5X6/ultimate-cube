package com.g3g4x5x6.ui.panels.ftp;


import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.formatter.IpAddressFormatter;
import com.g3g4x5x6.ui.formatter.PortFormatter;
import com.g3g4x5x6.ui.panels.telnet.TelnetSettingsProvider;
import com.g3g4x5x6.ui.panels.telnet.TelnetTtyConnector;
import com.g3g4x5x6.utils.DialogUtil;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.telnet.TelnetClient;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;


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
        hostField = new JFormattedTextField(new IpAddressFormatter());
        hostField.setColumns(10);
        hostField.setText("172.17.200.12");
        hostPane.add(hostLabel);
        hostPane.add(hostField);

        // Port
        JPanel portPane = new JPanel();
        JLabel portLabel = new JLabel("Port*");
        portField = new JFormattedTextField(new PortFormatter());
        portField.setColumns(4);
        portField.setText("21");
        portPane.add(portLabel);
        portPane.add(portField);

        // User
        JPanel userPane = new JPanel();
        JLabel userLabel = new JLabel("Username");
        userField = new JTextField();
        userField.setColumns(8);
        userField.setText("Anonymous");
        userPane.add(userLabel);
        userPane.add(userField);

        // Pass
        JPanel passPane = new JPanel();
        JLabel passLabel = new JLabel("Password");
        passField = new JPasswordField();
        passField.setColumns(8);
        passField.setText("12345678");
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
                    mainTabbedPane.insertTab("FTP-" + hostField.getText(), new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg"),
                            createTerminalWidget(),
                            "FTP-" + hostField.getText(),
                            mainTabbedPane.getSelectedIndex());
                    mainTabbedPane.removeTabAt(mainTabbedPane.getSelectedIndex());
                } else {
                    DialogUtil.warn("FTP is not Open!");
                }
            }
        });
        JButton testBtn = new JButton("测试通信");
        testBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("FTP 测试通信");
                if (testOpen()) {
                    DialogUtil.info("FTP is Open!");
                } else {
                    DialogUtil.warn("FTP is not Open!");
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
        log.debug(hostField.getText() + " / " + portField.getText());
        boolean flag = false;
        TelnetClient telnetClient = new TelnetClient();
        try {
            telnetClient.setConnectTimeout(2000);
            telnetClient.connect(hostField.getText(), Integer.parseInt(portField.getText()));
            flag = telnetClient.isConnected();
            telnetClient.disconnect();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return flag;
    }

    private @NotNull JediTermWidget createTerminalWidget() {
        JediTermWidget widget = new JediTermWidget(new TelnetSettingsProvider());
        widget.setTtyConnector(createTtyConnector());
        widget.start();
        return widget;
    }

    private @NotNull TtyConnector createTtyConnector() {
        return new TelnetTtyConnector(hostField.getText(), Integer.parseInt(portField.getText()));
    }
}
