package com.g3g4x5x6.ui.panels.dashboard.quickstarter;


import com.alibaba.fastjson.JSON;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.formatter.PortFormatter;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.ui.panels.ssh.SessionInfo;
import com.g3g4x5x6.ui.panels.ssh.SshTabbedPane;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.DialogUtil;
import com.g3g4x5x6.utils.SshUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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

    private final JTabbedPane mainTabbedPane;

    private JTextField hostField;
    private JFormattedTextField portField;

    private String host;
    private int port;
    private String username;
    private String password;
    private String privateKey = "";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BasicSettingStarterPane() {
        FlowLayout flowLayout = new FlowLayout();
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
        hostField = new JTextField();
        hostField.setColumns(10);
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
        userField.setColumns(8);
        userPane.add(userLabel);
        userPane.add(userField);

        // TODO password
        JPanel passPane = new JPanel();
        JLabel passLabel = new JLabel("Password");
        JPasswordField passField = new JPasswordField();
        passField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        passField.setColumns(8);
        passPane.add(passLabel);
        passPane.add(passField);

        // TODO Save and open session
        JPanel savePane = new JPanel();
        JPopupMenu jPopupMenu = new JPopupMenu();
        JButton privateBtn = new JButton();
        privateBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/goldKey.svg"));
        privateBtn.setToolTipText("公钥登录");
        privateBtn.setComponentPopupMenu(jPopupMenu);
        privateBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        JMenuItem showItem = new JMenuItem("...");

        JCheckBox enablePrivateKeyCheckBox = new JCheckBox("是否启用公钥登录");
        enablePrivateKeyCheckBox.setSelected(false);
        enablePrivateKeyCheckBox.addChangeListener(e -> {
            if (!enablePrivateKeyCheckBox.isSelected()) {
                privateKey = "";
            } else {
                privateKey = showItem.getText();
            }
        });

        JMenuItem selectKeyItem = new JMenuItem("选择私钥");
        selectKeyItem.addActionListener(new AbstractAction("选择私钥") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("选中私钥");
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                chooser.setDialogTitle("请选择登录私钥");
                int value = chooser.showOpenDialog(BasicSettingStarterPane.this);
                if (value == JFileChooser.APPROVE_OPTION) {
                    File files = chooser.getSelectedFile();
                    privateKey = files.getAbsolutePath();
                    privateBtn.setToolTipText(privateKey);
                    enablePrivateKeyCheckBox.setSelected(true);
                    showItem.setText(privateKey);
                }
            }
        });
        selectKeyItem.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/goldKey.svg"));

        jPopupMenu.add(showItem);
        jPopupMenu.add(selectKeyItem);
        jPopupMenu.add(enablePrivateKeyCheckBox);

        JButton openButton = new JButton("快速连接");
        openButton.setToolTipText("不保存会话");
        JButton testButton = new JButton("测试通信");
        testButton.setToolTipText("测试主机可达（IP/Port），用户名可用，密码正确");
        savePane.add(privateBtn);
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
                    log.info("快速连接，密码：" + password);

                    String defaultTitle = hostField.getText().equals("") ? "未命名" : hostField.getText();
                    SessionInfo sessionInfo = new SessionInfo();
                    sessionInfo.setSessionAddress(hostField.getText());
                    sessionInfo.setSessionPort(portField.getText());
                    sessionInfo.setSessionUser(userField.getText());
                    sessionInfo.setSessionPass(String.valueOf(passField.getPassword()));
                    sessionInfo.setSessionKeyPath(privateKey);

                    mainTabbedPane.addTab(
                            defaultTitle,
                            new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg"),
                            new SshTabbedPane(sessionInfo)
                    );
                    mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);

                    // TODO 保存最近会话到工作目录
                    LinkedHashMap<String, String> session = new LinkedHashMap<>();
                    session.put("host", host);
                    session.put("port", String.valueOf(port));
                    session.put("username", username);
                    session.put("password", password);
//                    session.put("accessTime", simpleDateFormat.format(new Date().getTime()));
                    String sessionJson = JSON.toJSONString(session);
                    // Filename: recent_md5_accessTime
                    // TODO Change
                    String fileName = "recent_" + DigestUtils.md5Hex(JSON.toJSONString(sessionJson)) + ".json";
                    // 判断是否已存在该会话，存在则删除会话先
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ConfigUtil.getWorkPath() + "/sessions/" + new File(fileName)))) {
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
}
