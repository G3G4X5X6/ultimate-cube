package com.g3g4x5x6.ui.panels.tools;

import com.alibaba.fastjson.JSON;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.g3g4x5x6.App;
import com.g3g4x5x6.utils.os.OsInfoUtil;
import com.g3g4x5x6.ui.formatter.IpAddressFormatter;
import com.g3g4x5x6.ui.formatter.PortFormatter;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.DialogUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


@Slf4j
public class FreeRdp extends JDialog {

    private JToolBar toolBar;
    private JPanel centerPane;

    private JTabbedPane basicSettingTabbedPane;
    private String basicSettingPaneTitle;
    private JPanel basicSettingPane;

    private JTabbedPane advancedSettingTabbedPane;
    private String advancedSettingPaneTitle;
    private JPanel advancedSettingPane;

    private JPanel controlPane;


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

    private boolean openFlag = false;
    private String freeRdpSessionsDirPath = ConfigUtil.getWorkPath() + "/sessions/freeRdp";
    private String exePath = ConfigUtil.getWorkPath() + "/tools/freerdp/wfreerdp.exe";

    public FreeRdp() {
        super(App.mainFrame);
        this.setTitle("FreeRDP");
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(750, 450));
        this.setMinimumSize(new Dimension(750, 450));
        this.setLocationRelativeTo(App.mainFrame);

        toolBar = new JToolBar();

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);

        basicSettingTabbedPane = new JTabbedPane();
        basicSettingPane = new JPanel();
        basicSettingPane.setLayout(flowLayout);
        basicSettingPaneTitle = "Basic FreeRDP Settings";

        advancedSettingTabbedPane = new JTabbedPane();
        advancedSettingPane = new JPanel();
        advancedSettingPane.setLayout(flowLayout);
        advancedSettingPaneTitle = "Advanced FreeRDP Settings";

        controlPane = new JPanel();

        initToolBar();
        initBasicPane();
        initAdvancePane();
        initControlPane();

        centerPane = new JPanel(new BorderLayout());
        centerPane.add(basicSettingTabbedPane, BorderLayout.NORTH);
        centerPane.add(advancedSettingTabbedPane, BorderLayout.CENTER);

        this.add(toolBar, BorderLayout.NORTH);
        this.add(centerPane, BorderLayout.CENTER);
        this.add(controlPane, BorderLayout.SOUTH);
    }

    private void initToolBar() {
        FlatButton listBtn = new FlatButton();
        listBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        listBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/listFiles.svg"));
        listBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Open FreeRDP list");
                FreeRdpDialog freeRdpDialog = new FreeRdpDialog();
                freeRdpDialog.setVisible(true);
            }
        });

        FlatButton importBtn = new FlatButton();
        importBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        importBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/import.svg"));
        importBtn.setToolTipText("导入FreeRDP会话设置");
        importBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Import FreeRDP settings");
            }
        });

        FlatButton exportBtn = new FlatButton();
        exportBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        exportBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/export.svg"));
        exportBtn.setToolTipText("导出FreeRDP会话设置");
        exportBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Export FreeRDP settings");
            }
        });

        toolBar.setFloatable(false);
        toolBar.add(listBtn);
        toolBar.add(importBtn);
        toolBar.add(exportBtn);
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
        portField.setText("3389");
        portPane.add(portLabel);
        portPane.add(portField);

        // User
        JPanel userPane = new JPanel();
        JLabel userLabel = new JLabel("Username");
        userField = new JTextField();
        userField.setColumns(8);
        userField.setText("Administrator");
        userPane.add(userLabel);
        userPane.add(userField);

        // Pass
        JPanel passPane = new JPanel();
        JLabel passLabel = new JLabel("Password");
        passField = new JPasswordField();
        passField.setColumns(8);
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

    /**
     * private JCheckBox fullscreen;
     * private JTextField title;
     * private JTextField width;
     * private JTextField height;
     */
    private void initAdvancePane() {
        advancedSettingTabbedPane.addTab(advancedSettingPaneTitle, advancedSettingPane);
        // Title
        JPanel titlePane = new JPanel();
        title = new JTextField();
        title.setColumns(15);
        titlePane.add(new JLabel("窗口标题"));
        titlePane.add(title);

        // Size
        JPanel sizePane = new JPanel();
        width = new JTextField();
        width.setColumns(4);
        width.setEditable(false);
        height = new JTextField();
        height.setColumns(4);
        height.setEditable(false);
        fullscreen = new JCheckBox("Fullscreen");
        fullscreen.setSelected(true);
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
        advancedSettingPane.add(sizePane);
        advancedSettingPane.add(Box.createHorizontalGlue());
        advancedSettingPane.add(checkBoxPane);
    }

    private void initControlPane() {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        controlPane.setLayout(flowLayout);

        JButton openBtn = new JButton("打开");
        JButton saveBtn = new JButton("保存");
        JButton cancelBtn = new JButton("关闭");

        controlPane.add(openBtn);
        controlPane.add(saveBtn);
        controlPane.add(cancelBtn);

        openBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Open FreeRDP");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        openFreeRDP();
                    }
                }).start();
            }
        });

        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Save FreeRDP");
                saveFreeRDP();
            }
        });

        cancelBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Cancel FreeRDP Dialog");
                dispose();
            }
        });
    }

    private void openFreeRDP() {
        // TODO 打开远程桌面
        if (!hostField.getText().strip().equals("")) {
            try {
                //创建ProcessBuilder对象
                ProcessBuilder processBuilder = new ProcessBuilder();

                //封装执行的第三方程序(命令)
                if (!openFlag) {
                    packCmdList();
                } else {
                    openFlag = false;
                }

                // 设置执行命令及其参数列表
                processBuilder.command(cmdList);
                log.debug(cmdList.toString());

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
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            DialogUtil.warn("请输入服务器IP地址或者域名！");
        }
    }

    private void saveFreeRDP() {
        // TODO 保存远程桌面设置
        packCmdList();
        String sessionFile = "";
        if (!hostField.getText().strip().equals("")) {
            sessionFile = freeRdpSessionsDirPath + "/freeRdp_" + hostField.getText() + "_" +
                    (portField.getText().strip().equals("") ? "3389" : portField.getText().strip()) + "_" +
                    (userField.getText().strip().equals("") ? "-" : userField.getText().strip()) + ".json";
            log.debug("SessionFile: " + sessionFile);

            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(sessionFile)));
                out.write(JSON.toJSONString(cmdList).getBytes(StandardCharsets.UTF_8));
                out.flush();
                out.close();
                DialogUtil.info("远程桌面会话保存成功！");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            DialogUtil.warn("请输入服务器IP地址或者域名！");
        }
        log.debug(JSON.toJSONString(cmdList));
    }

    private void packCmdList() {
        cmdList = new ArrayList<>();
        if (OsInfoUtil.isWindows()) {
            cmdList.add(exePath);
        }
        if (OsInfoUtil.isLinux()) {

        }
        if (OsInfoUtil.isMacOS()) {

        }
        if (OsInfoUtil.isMacOSX()) {

        }
        // 不同操作系统平台通用参数封装
        // 服务器IP
        cmdList.add("/v:" + hostField.getText());
        // 远程桌面端口
        if (!portField.getText().strip().equals(""))
            cmdList.add("/port:" + portField.getText());
        // 用户名
        cmdList.add("/u:" + userField.getText());
        // 用户密码
        if (!String.valueOf(passField.getPassword()).strip().equals(""))
            cmdList.add("/p:" + String.valueOf(passField.getPassword()));
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

    private class FreeRdpDialog extends JDialog {
        private JTable noteTable;
        private DefaultTableModel tableModel;
        private String[] columnNames = {"IPAddress", "Port", "Username"};

        private JButton delButton;
        private JButton closeButton;

        public FreeRdpDialog() {
            super(FreeRdp.this);
            this.setLayout(new BorderLayout());
            this.setPreferredSize(new Dimension(700, 350));
            this.setSize(new Dimension(700, 350));
            this.setLocationRelativeTo(FreeRdp.this);
            this.setModal(false);
            this.setTitle("FreeRDP Lists");

            initEnableOption();

            initFreeRdpListTable();

            initControlButton();

            noteTable.addMouseListener(new MouseAdapter() {
                @SneakyThrows
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        log.debug("双击打开FreeRDP远程桌面");
                        int index = noteTable.getSelectedRow();
                        String address = (String) tableModel.getValueAt(index, 0);
                        String port = (String) tableModel.getValueAt(index, 1);
                        String user = (String) tableModel.getValueAt(index, 2);
                        String filePath = freeRdpSessionsDirPath + "/freeRdp_" + address + "_" + port + "_" + user + ".json";
                        File file = new File(filePath);
                        if (file.exists()) {
                            BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
                            cmdList = (ArrayList<String>) JSON.parseArray(reader.readLine(), String.class);
                            openFlag = true;
                            new Thread(() -> openFreeRDP()).start();
                        } else {
                            DialogUtil.warn("不存在会话： " + filePath);
                        }
                    }
                }
            });
        }

        private void initEnableOption() {
            // TODO Enable Option
            JPanel enablePanel = new JPanel();
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            enablePanel.setLayout(flowLayout);

            JLabel tips = new JLabel("双击打开FreeRDP远程桌面");
            tips.setEnabled(false);
            enablePanel.add(tips);

            this.add(enablePanel, BorderLayout.NORTH);
        }

        private void initFreeRdpListTable() {
            noteTable = new JTable();
            tableModel = new DefaultTableModel() {
                // 不可编辑
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tableModel.setColumnIdentifiers(columnNames);

            initTable();

            JScrollPane tableScroll = new JScrollPane(noteTable);
            tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JTextField.CENTER);
//            noteTable.getColumn("IPAddress").setCellRenderer(centerRenderer);
            this.add(tableScroll, BorderLayout.CENTER);
        }

        private void initTable() {
            log.debug("FreeRdpDialog::initTable()");
            tableModel.setRowCount(0);
            // 添加 Row 数据
            File sessionsDir = new File(freeRdpSessionsDirPath);
            for (File file : sessionsDir.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".json") && file.getName().startsWith("freeRdp")) {
                    log.debug(file.getAbsolutePath());
                    String[] rows = file.getName().split("_");
                    rows[3] = rows[3].substring(0, rows[3].lastIndexOf(".json"));

                    tableModel.addRow(new String[]{rows[1], rows[2], rows[3]});
                }
            }

            noteTable.setModel(tableModel);
        }

        private void initControlButton() {
            JPanel controlPane = new JPanel();
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            controlPane.setLayout(flowLayout);
            delButton = new JButton("Delete");
            closeButton = new JButton("Close");
            controlPane.add(delButton);
            controlPane.add(closeButton);
            this.add(controlPane, BorderLayout.SOUTH);

            delButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (DialogUtil.yesOrNo(App.mainFrame, "是否删除选中FreeRDP会话设置？") == 0) {
                        int[] rows = noteTable.getSelectedRows();
                        for (int index : rows) {
                            String address = (String) tableModel.getValueAt(index, 0);
                            String port = (String) tableModel.getValueAt(index, 1);
                            String user = (String) tableModel.getValueAt(index, 2);
                            String filePath = freeRdpSessionsDirPath + "/freeRdp_" + address + "_" + port + "_" + user + ".json";
                            log.debug(filePath);
                            File file = new File(filePath);
                            if (file.exists()) {
                                file.delete();
                            } else {
                                DialogUtil.warn("不存在会话： " + filePath);
                            }
                        }
                        initTable();
                    }
                }
            });

            closeButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
        }
    }
}