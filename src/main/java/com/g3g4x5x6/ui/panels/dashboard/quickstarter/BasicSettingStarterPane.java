package com.g3g4x5x6.ui.panels.dashboard.quickstarter;


import com.alibaba.fastjson.JSON;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.ui.formatter.IpAddressFormatter;
import com.g3g4x5x6.ui.formatter.PortFormatter;
import com.g3g4x5x6.ui.panels.ssh.SshTabbedPane;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.DialogUtil;
import com.g3g4x5x6.utils.SshUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;


@Slf4j
public class BasicSettingStarterPane extends JPanel {

    private FlowLayout flowLayout = new FlowLayout();
    private JTabbedPane mainTabbedPane;

    private JFormattedTextField hostField;
    private JFormattedTextField portField;

    private String host;
    private int port;
    private String username;
    private String password;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BasicSettingStarterPane() {
        flowLayout.setAlignment(FlowLayout.LEFT);
        this.setLayout(flowLayout);
        this.mainTabbedPane = MainFrame.mainTabbedPane;
        //
        createBasicComponent();
    }


    private void createBasicComponent() {
        // TODO host address
        JPanel hostPane = new JPanel();
        JLabel hostLabel = new JLabel("Remote Host*");
        hostField = new JFormattedTextField(new IpAddressFormatter());
        hostField.setColumns(10);
//        hostField.setText("172.17.200.104");    // For testing
        hostPane.add(hostLabel);
        hostPane.add(hostField);

        // TODO port
        JPanel portPane = new JPanel();
        JLabel portLabel = new JLabel("Port*");
        portField = new JFormattedTextField(new PortFormatter());
        portField.setColumns(4);
        portField.setText("22");
        portPane.add(portLabel);
        portPane.add(portField);

        // TODO user name
        JPanel userPane = new JPanel();
        JLabel userLabel = new JLabel("Username");
        JFormattedTextField userField = new JFormattedTextField();
//        userField.setText("security");
        userField.setColumns(8);
        userPane.add(userLabel);
        userPane.add(userField);

        // TODO password
        JPanel passPane = new JPanel();
        JLabel passLabel = new JLabel("Password");
        JPasswordField passField = new JPasswordField();
//        passField.setText("123456");
        passField.setColumns(8);
        passPane.add(passLabel);
        passPane.add(passField);

        // TODO Save and open session
        JPanel savePane = new JPanel();
        JButton openButton = new JButton("快速连接");
//        openButton.setToolTipText("默认自动保存会话");
        JButton testButton = new JButton("测试通信");
        savePane.add(openButton);
        savePane.add(testButton);

        this.add(hostPane);
        this.add(portPane);
        this.add(userPane);
        this.add(passPane);
        this.add(savePane);

        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("快速连接");

                // TODO 测试连接
                if (SshUtil.testConnection(hostField.getText(), portField.getText()) == 1) {
                    host = hostField.getText();
                    port = Integer.parseInt(portField.getText());
                    username = userField.getText();
                    password = String.valueOf(passField.getPassword());
                    // TODO 删除密码日志输出
                    log.info("快速连接，密码：" + password);

                    String defaultTitle = hostField.getText().equals("") ? "未命名" : hostField.getText();
                    mainTabbedPane.addTab(defaultTitle, new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg"),
                            new SshTabbedPane(
                                    hostField.getText(),
                                    portField.getText(),
                                    userField.getText(),
                                    String.valueOf(passField.getPassword())));
                    mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount()-1);

                    // TODO 保存最近会话到工作目录
                    LinkedHashMap<String, String> session = new LinkedHashMap<>();
                    session.put("host", host);
                    session.put("port", String.valueOf(port));
                    session.put("username", username);
                    session.put("password", password);
//                    session.put("accessTime", simpleDateFormat.format(new Date().getTime()));
                    String sessionJson = JSON.toJSONString(session);
                    // Filename: recent_md5_accessTime
                    String fileName = "recent_" + DigestUtils.md5Hex(JSON.toJSONString(sessionJson)) + ".json";
                    // 判断是否已存在该会话，存在则删除会话先
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ConfigUtil.getWorkPath() + "/sessions/" + new File(fileName)))){
                        writer.write(sessionJson);
                        writer.flush();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    log.debug("\nFileName: " + fileName + "\nJson: " + sessionJson);
                } else {
                    DialogUtil.warn("连接失败");
                }
            }
        });

        testButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.debug("测试连接");
                switch (SshUtil.testConnection(hostField.getText(), portField.getText())) {
                    case 0:
                        DialogUtil.warn("连接失败");
                        break;
                    case 1:
                        DialogUtil.info("连接成功");
                        break;
                    case 2:
                        DialogUtil.info("请输入主机地址！！！");
                }
            }
        });
    }


//    private @NotNull JediTermWidget createTerminalWidget() {
//        JediTermWidget widget = new JediTermWidget(new SshSettingsProvider());
//        widget.setTtyConnector(createTtyConnector());
//        widget.start();
//        return widget;
//    }
//
//    // TODO 创建 sFTP channel
//    private @NotNull TtyConnector createTtyConnector() {
//        try {
//            if (username.equals("")) {
//                return new MyJSchShellTtyConnector(host, port);
//            }
//            if (password.equals("")) {
//                return new MyJSchShellTtyConnector(host, port, username);
//            }
//            return new MyJSchShellTtyConnector(host, port, username, password);
//        } catch (Exception e) {
//            throw new IllegalStateException(e);
//        }
//    }
//
//    // TODO 获取 sFTP channel

}
