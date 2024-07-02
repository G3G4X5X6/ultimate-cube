package com.g3g4x5x6.remote.rdp;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.panel.session.SessionFileUtil;
import com.g3g4x5x6.remote.utils.VaultUtil;
import com.g3g4x5x6.utils.DialogUtil;
import com.g3g4x5x6.utils.os.OsInfoUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Slf4j
public class RdpPane extends JPanel {

    private JTabbedPane mainTabbedPane;
    private final JPanel controlPane;
    private final JTabbedPane basicSettingTabbedPane;
    private final String basicSettingPaneTitle;
    private final JPanel basicSettingPane;

    private final JTabbedPane advancedSettingTabbedPane;
    private final String advancedSettingPaneTitle;
    private final JPanel advancedSettingPane;

    private String editPath;

    private ArrayList<String> cmdList;
    // 参数区域: basicSettingPane
    private JFormattedTextField hostField;
    private JFormattedTextField portField;
    private JTextField userField;
    private JPasswordField passField;
    // 参数区域: advancedSettingPane
    private JCheckBox fullscreen;
    private JCheckBox sound;
    private JCheckBox microphone;
    private JTextField title;
    private JTextField width;
    private JTextField height;
    private JComboBox<String> categoryCombo;
    private String sessionCategory = "";
    @Setter
    private String authType = "local";

    private boolean openFlag = false;
    private String exePath = Path.of(AppConfig.getInstallPath(), "wfreerdp.exe").toString();

    {
        log.debug(exePath);
        if (!Files.exists(Path.of(exePath))) {
            exePath = AppConfig.getBinPath() + "/wfreerdp.exe";
        }
    }

    public RdpPane(JTabbedPane mainTabbedPane) {
        this.mainTabbedPane = mainTabbedPane;
        this.setLayout(new BorderLayout());
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);

        basicSettingTabbedPane = new JTabbedPane();
        basicSettingPane = new JPanel();
        basicSettingPane.setLayout(flowLayout);
        basicSettingPaneTitle = "Basic RDP Settings";

        advancedSettingTabbedPane = new JTabbedPane();
        advancedSettingPane = new JPanel();
        advancedSettingPane.setLayout(flowLayout);
        advancedSettingPaneTitle = "Advanced RDP Settings";

        controlPane = new JPanel();

        initBasicPane();
        initAdvancePane();
        initControlPane();

        this.add(basicSettingTabbedPane, BorderLayout.NORTH);
        this.add(advancedSettingTabbedPane, BorderLayout.CENTER);
        this.add(controlPane, BorderLayout.SOUTH);
    }

    private void initBasicPane() {
        basicSettingTabbedPane.addTab(basicSettingPaneTitle, basicSettingPane);
        // Host
        JPanel hostPane = new JPanel();
        JLabel hostLabel = new JLabel("Remote Host*");
        hostField = new JFormattedTextField();
        hostField.setColumns(17);
        hostPane.add(hostLabel);
        hostPane.add(hostField);

        // Port
        JPanel portPane = new JPanel();
        JLabel portLabel = new JLabel("Port*");
        portField = new JFormattedTextField();
        portField.setColumns(4);
        portField.setText("3389");
        portPane.add(portLabel);
        portPane.add(portField);

        // User
        JPanel userPane = new JPanel();
        JLabel userLabel = new JLabel("Username");
        userField = new JTextField();
        userField.setColumns(16);
        userPane.add(userLabel);
        userPane.add(userField);

        // Pass
        JPanel passPane = new JPanel();
        JLabel passLabel = new JLabel("Password");
        passField = new JPasswordField();
        passField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        passField.setColumns(16);
        passPane.add(passLabel);
        passPane.add(passField);

        basicSettingPane.add(hostPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(portPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(userPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(passPane);
    }

    private void initAdvancePane() {
        advancedSettingTabbedPane.addTab(advancedSettingPaneTitle, advancedSettingPane);
        // Title
        JPanel titlePane = new JPanel();
        title = new JTextField();
        title.putClientProperty("JTextField.placeholderText", "Default: Host_Port_User_LoginType"); // 本地或域名
        title.setColumns(35);
        titlePane.add(new JLabel("窗口标题"));
        titlePane.add(title);

        // 会话分类
        JPanel categoryPane = new JPanel();
        categoryCombo = new JComboBox<>();
        categoryCombo.setEditable(true);
        categoryCombo.setMinimumSize(new Dimension(250, 25));
        categoryCombo.setSize(new Dimension(250, 25));
        categoryCombo.setPreferredSize(new Dimension(250, 25));
        categoryCombo.addItemListener(e -> sessionCategory = Objects.requireNonNull(categoryCombo.getSelectedItem()).toString());

        HashMap<String, ArrayList<JSONObject>> categoriesMap = SessionFileUtil.getCategoriesMap();
        ArrayList<String> categoryList = new ArrayList<>(categoriesMap.keySet());
        for (String category : categoryList) {
            categoryCombo.addItem(category);
        }
        if (categoryCombo.getItemCount() <= 0) {
            categoryCombo.addItem("");
        }
        categoryCombo.setSelectedItem(Objects.requireNonNullElse(sessionCategory, ""));

        categoryPane.add(new JLabel("会话分类:"));
        categoryPane.add(categoryCombo);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Size
        JPanel sizePane = new JPanel();
        width = new JTextField();
        width.setColumns(4);
        width.setText(String.valueOf(screenSize.width - 100));
        height = new JTextField();
        height.setColumns(4);
        height.setText(String.valueOf(screenSize.height - 100));
        fullscreen = new JCheckBox("Fullscreen");
        fullscreen.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fullscreen.isSelected()) {
                    width.setEditable(false);
                    height.setEditable(false);
                } else {
                    width.setEditable(true);
                    height.setEditable(true);
                }
            }
        });
        sizePane.add(new JLabel("分辨率: "));
        sizePane.add(new JLabel("Width"));
        sizePane.add(width);
        sizePane.add(new JLabel("Height"));
        sizePane.add(height);
        sizePane.add(fullscreen);

        // CheckBox: fullscreen
        JPanel checkBoxPane = new JPanel();
        /**
         * https://www.jianshu.com/p/f6fcf5b56fe3
         * 启用音频输出：
         * audio-mode的参数为： 0 - redirect；1 - leave on server (or laptop)； 2 - disable audio。
         * 当使用/audio-mode:1时，表示在远程电脑上输出音频
         */
        sound = new JCheckBox("Sound");
        sound.setSelected(false);
        microphone = new JCheckBox("Microphone");
        microphone.setSelected(false);
        checkBoxPane.add(sound);
        checkBoxPane.add(microphone);

        advancedSettingPane.add(titlePane);
        advancedSettingPane.add(Box.createHorizontalGlue());
        advancedSettingPane.add(categoryPane);
        advancedSettingPane.add(Box.createHorizontalGlue());
        advancedSettingPane.add(sizePane);
        advancedSettingPane.add(Box.createHorizontalGlue());
        advancedSettingPane.add(checkBoxPane);
    }

    private void initControlPane() {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.CENTER);
        controlPane.setLayout(flowLayout);

        JButton openBtn = new JButton("快速连接");
        openBtn.setToolTipText("默认不保存会话");
        openBtn.setIcon(new FlatSVGIcon("icons/rerun.svg"));

        JButton testBtn = new JButton("测试通信");
        testBtn.setIcon(new FlatSVGIcon("icons/lightning.svg"));

        JButton saveBtn = new JButton("保存会话");
        saveBtn.setIcon(new FlatSVGIcon("icons/menu-saveall.svg"));

        JButton saveAndOpenBtn = new JButton("保存并连接");
        saveAndOpenBtn.setIcon(new FlatSVGIcon("icons/connectionStatus.svg"));

        controlPane.add(openBtn);
        controlPane.add(testBtn);
        controlPane.add(saveBtn);
        controlPane.add(saveAndOpenBtn);

        openBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Open FreeRDP");
                new Thread(() -> openFreeRDP()).start();
            }
        });

        testBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 测试端口是否开放
            }
        });

        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Save FreeRDP");
                saveFreeRDP();
            }
        });

        saveAndOpenBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Save & Open FreeRDP");
                if (saveFreeRDP()) new Thread(() -> openFreeRDP()).start();
            }
        });

    }

    private void openFreeRDP() {
        // TODO 打开远程桌面
        if (!hostField.getText().isBlank()) {
            try {
                //创建ProcessBuilder对象
                ProcessBuilder processBuilder = new ProcessBuilder();

                //封装执行的第三方程序(命令)
                if (!openFlag) {
                    // 封装参数
                    packCmdList();

                    // 解密密码
                    // VaultUtil.decryptPasswd(jsonObject.getString("sessionPass"))
                    // /p:7418db4111130de6004ef9b82a09e4b3
                    ArrayList<String> tmpList = (ArrayList<String>) cmdList.clone();
                    for (String arg : tmpList) {
                        if (arg.contains("/p:")) {
                            cmdList.remove(arg);
                            cmdList.add("/p:" + VaultUtil.decryptPasswd(arg.substring(3)));
                        }
                    }

                    // 列表头部插入 freerdp 命令
                    if (OsInfoUtil.isWindows()) {
                        cmdList.add(0, exePath);
                    }
                    if (OsInfoUtil.isLinux()) {

                    }
                    if (OsInfoUtil.isMacOS()) {

                    }
                    if (OsInfoUtil.isMacOSX()) {

                    }
                } else {
                    openFlag = false;
                }

                // 设置执行命令及其参数列表
                processBuilder.command(cmdList);
                log.debug("freeRDP: {}", String.join(" ", cmdList));

                //将标准输入流和错误输入流合并
                // 通过标准输入流读取信息就可以拿到第三方程序输出的错误信息、正常信息
                processBuilder.redirectErrorStream(true);

                //启动一个进程
                Process process = processBuilder.start();
                //读取输入流
                InputStream inputStream = process.getInputStream();
                //将字节流转成字符流
                InputStreamReader reader = new InputStreamReader(inputStream, "gbk");
                //字符缓冲区
                char[] chars = new char[1024];
                int len = -1;
                while ((len = reader.read(chars)) != -1) {
                    String string = new String(chars, 0, len);
                    log.debug(string);
                }
                inputStream.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                DialogUtil.error(e.getMessage());
            }
        } else {
            DialogUtil.warn("请输入服务器IP地址或者域名！");
        }
    }

    private boolean saveFreeRDP() {
        boolean isSuccess = false;
        // 保存远程桌面设置
        packCmdList();

        LinkedHashMap<String, Object> session = new LinkedHashMap<>();
        if (title.getText().isBlank()) {
            // 默认会话名称：Host_Port_User_LoginType
            session.put("sessionName", hostField.getText() + "_" + portField.getText() + "_" + userField.getText() + "_" + authType);
        } else {
            session.put("sessionName", title.getText());
        }
        session.put("sessionProtocol", "RDP");
        session.put("sessionCategory", sessionCategory);
        session.put("sessionAddress", hostField.getText());
        session.put("sessionPort", portField.getText());
        session.put("sessionUser", userField.getText());
        session.put("sessionPass", VaultUtil.encryptPasswd(String.valueOf(passField.getPassword())));
        session.put("sessionArgs", cmdList);
        session.put("sessionLoginType", "password");
        session.put("sessionComment", "暂不支持");

        if (!hostField.getText().isBlank()) {
            if (editPath == null) {
                // 保存会话路径
                Path sessionPath = Paths.get(AppConfig.getSessionPath(), "RDP");
                Path sessionFile = sessionPath.resolve(UUID.randomUUID() + ".json");
                editPath = sessionFile.toString();
                log.debug("SessionFile: {}", sessionFile);
            }

            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(editPath));
                out.write(JSON.toJSONString(session).getBytes(StandardCharsets.UTF_8));
                out.flush();
                out.close();
                DialogUtil.info("远程桌面会话保存成功！");
                isSuccess = true;
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            DialogUtil.warn("请输入服务器IP地址或者域名！");
        }
        log.debug(JSON.toJSONString(cmdList));
        return isSuccess;
    }

    private void packCmdList() {
        cmdList = new ArrayList<>();

        // 不同操作系统平台通用参数封装
        // 服务器IP
        cmdList.add("/v:" + hostField.getText());
        // 远程桌面端口
        if (!portField.getText().strip().equals("")) cmdList.add("/port:" + portField.getText());
        // 用户名
        cmdList.add("/u:" + userField.getText());
        // 用户密码
        if (!String.valueOf(passField.getPassword()).isBlank())
            cmdList.add("/p:" + VaultUtil.encryptPasswd(String.valueOf(passField.getPassword())));
        // 全屏设置
        if (fullscreen.isSelected()) {
            cmdList.add("/f");
        } else {
            // 分辨率设置：width
            if (!width.getText().strip().equals("")) {
                cmdList.add("/w:" + width.getText().strip());
            }
            // 分辨率设置：height
            if (!height.getText().strip().equals("")) {
                cmdList.add("/h:" + height.getText().strip());
            }
        }
        // 标题设置
        if (!title.getText().strip().equals("")) {
            cmdList.add("/t:" + title.getText().strip());
        }
        // 音频重定向： Sound
        if (sound.isSelected()) {
            cmdList.add("/sound");
        }
        // 麦克风设置： Microphone
        if (microphone.isSelected()) {
            cmdList.add("/mic");
        }
        // +window-drag 没看出什么效果
        cmdList.add("+window-drag");
    }

    public void setPassField(String password) {
        passField.setText(password);
    }

    public void setHostField(String sessionAddress) {
        hostField.setText(sessionAddress);
    }

    public void setPortField(String sessionPort) {
        portField.setText(sessionPort);
    }

    public void setUserField(String sessionUser) {
        userField.setText(sessionUser);
    }

    public void setSessionName(String sessionName) {
        title.setText(sessionName);
    }

    public void setCommentText(String sessionComment) {

    }

    public void setCategory(String category) {
        categoryCombo.setSelectedItem(category);
        sessionCategory = category;
    }

    public void setEditPath(String sessionFilePath) {
        editPath = sessionFilePath;
    }
}
