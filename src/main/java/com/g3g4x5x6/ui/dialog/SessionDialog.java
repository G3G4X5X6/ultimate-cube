package com.g3g4x5x6.ui.dialog;

import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.g3g4x5x6.ui.formatter.IpAddressFormatter;
import com.g3g4x5x6.ui.formatter.PortFormatter;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SessionDialog extends JDialog {
    final static Logger logger = Logger.getLogger(SessionDialog.class);

    private JPanel mainPane;
    private JTabbedPane tabbedPane;
    private JPanel btnPane;

    private JFormattedTextField hostField;
    private JFormattedTextField portField;
    private JFormattedTextField userField;
    private JPasswordField passField;

    public SessionDialog(Component parentComponent) {
        this.setSize(750, 550);
        this.setResizable(false);
        this.setLocationRelativeTo(parentComponent);
        this.setModal(true);

        // 创建对话框的内容面板
        mainPane = new JPanel();
        mainPane.setLayout(new BorderLayout());

        // 新增会话配置标签页
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("基本设置", new BasicPane());
        tabbedPane.addTab("高级设置", new Panel());

        // 取消退出窗口
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("取消关闭窗口");
                // 关闭对话框
                dispose();
            }
        });

        // 确认保存
        JButton okBtn = new JButton("保存");
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("确认保存会话");
            }
        });

        // 测试连接
        JButton testBtn = new JButton("测试");
        testBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("测试连接");

            }
        });

        // Button Panel
        btnPane = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        btnPane.setLayout(flowLayout);
        btnPane.add(okBtn);
        btnPane.add(testBtn);
        btnPane.add(cancelBtn);

        // 添加组件到面板
        mainPane.add(tabbedPane, BorderLayout.CENTER);
        mainPane.add(btnPane, BorderLayout.SOUTH);

        // 设置对话框的内容面板
        this.setContentPane(mainPane);
        // 显示对话框
        this.setVisible(true);
    }


    private class BasicPane extends JPanel {

        private BasicPane() {
            Box vBox = Box.createVerticalBox();

            Box basicBox = Box.createHorizontalBox();
            // TODO host address
            JPanel hostPane = new JPanel();
            JLabel hostLabel = new JLabel("Remote Host*");
            hostField = new JFormattedTextField(new IpAddressFormatter());
            hostField.setColumns(10);
            hostField.setText("192.168.83.137");    // For testing
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
            userField = new JFormattedTextField();
            userField.setText("root");
            userField.setColumns(8);
            userPane.add(userLabel);
            userPane.add(userField);

            // TODO password
            JPanel passPane = new JPanel();
            JLabel passLabel = new JLabel("Password");
            passField = new JPasswordField();
            passField.setText("12345678");
            passField.setColumns(8);
            passPane.add(passLabel);
            passPane.add(passField);

            basicBox.add(hostPane);
            basicBox.add(portPane);
            basicBox.add(userPane);
            basicBox.add(passPane);
            ////////////////////////////////////////////////////////////

            Box checkBox = Box.createHorizontalBox();
            // TODO 启用私钥
            JButton keyBtn = new JButton();
            keyBtn.setIcon(new FlatTreeClosedIcon());
            keyBtn.setEnabled(false);

            JLabel keyLabel = new JLabel("点击按钮选择私钥");
            keyBtn.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fileChooser = new JFileChooser();
                    // 设置默认显示的文件夹为当前文件夹
                    fileChooser.setCurrentDirectory(new File("."));
                    // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    // 设置是否允许多选
                    fileChooser.setMultiSelectionEnabled(false);
                    // 添加可用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
                    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("zip(*.zip, *.rar)", "zip", "rar"));
                    // 设置默认使用的文件过滤器
                    fileChooser.setFileFilter(new FileNameExtensionFilter("image(*.jpg, *.png, *.gif)", "jpg", "png", "gif"));
                    // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
                    int result = fileChooser.showOpenDialog(SessionDialog.this);

                    if (result == JFileChooser.APPROVE_OPTION) {
                        // 如果点击了"确定", 则获取选择的文件路径
                        File file = fileChooser.getSelectedFile();
                        keyLabel.setText(file.getAbsolutePath());
                    }
                }
            });

            JCheckBox jCheckBox = new JCheckBox("启用私钥");
            jCheckBox.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 获取事件源（即复选框本身）
                    JCheckBox checkBox = (JCheckBox) e.getSource();
                    logger.debug(checkBox.getText() + " 是否选中: " + checkBox.isSelected());

                    if (checkBox.isSelected()) {
                        keyBtn.setEnabled(true);
                    } else {
                        keyBtn.setEnabled(false);
                    }
                }
            });

            checkBox.add(jCheckBox);
            checkBox.add(Box.createHorizontalGlue());

            Box keyBox = Box.createHorizontalBox();
            keyBox.add(keyBtn);
            keyBox.add(new JLabel(" "));
            keyBox.add(keyLabel);
            keyBox.add(Box.createHorizontalGlue());

            vBox.add(basicBox);
            vBox.add(Box.createVerticalStrut(10));
            vBox.add(checkBox);
            vBox.add(Box.createVerticalStrut(5));
            vBox.add(keyBox);
            this.add(vBox);
        }

    }
}
