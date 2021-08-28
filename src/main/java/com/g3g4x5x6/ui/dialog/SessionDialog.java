package com.g3g4x5x6.ui.dialog;

import com.formdev.flatlaf.icons.FlatTreeClosedIcon;
import com.g3g4x5x6.ui.formatter.IpAddressFormatter;
import com.g3g4x5x6.ui.formatter.PortFormatter;
import com.g3g4x5x6.utils.DbUtil;
import com.g3g4x5x6.utils.DialogUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class SessionDialog extends JDialog {

    private JPanel mainPane;
    private JTabbedPane tabbedPane;
    private JPanel btnPane;

    private JFormattedTextField hostField;
    private JFormattedTextField portField;
    private JFormattedTextField userField;
    private JPasswordField passField;

    private JCheckBox jCheckBox;
    private JLabel keyLabel;

    private String currentTag;


    public SessionDialog(Component parentComponent, String currentTag) {
        this.setSize(750, 550);
        this.setResizable(false);
        this.setLocationRelativeTo(parentComponent);
        this.setModal(true);
        this.currentTag = currentTag;

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
                log.debug("取消关闭窗口");
                // 关闭对话框
                dispose();
            }
        });

        // 确认保存
        JButton okBtn = new JButton("保存");
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("确认保存会话");
                log.debug("当前系统时间戳：" + new Date().getTime());

                Long currentTime = new Date().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formatTime = simpleDateFormat.format(new Date(currentTime));

                String host = hostField.getText();
                String port = portField.getText();
                String user = userField.getText();
                String pass = String.valueOf(passField.getPassword());
                String privateKey = "";
                String auth = "password";
                if (jCheckBox.isSelected()) {
                    auth = "key";
                    privateKey = keyLabel.getText();
                }
                String protocol = "SSH";
                String session = host;

                // TODO 数据库插入数据
                try {
                    Connection connection = DbUtil.getConnection();
                    Statement statement = connection.createStatement();
                    // 更新 session 表
                    String sql_session = "INSERT INTO session VALUES (null , " +    // id, 自增
                            "'" + session + "', " +     // session name
                            "'" + protocol + "', " +    // protocol
                            "'" + host + "', " +        // host
                            "'" + port + "', " +        // port
                            "'" + auth + "', " +        // auth
                            "'" + user + "', " +        // user
                            "'" + pass + "', " +        // pass
                            "'" + privateKey + "', " +  // private key
                            "'" + currentTime + "', " + // create time
                            "'" + currentTime + "', " + // access time
                            "'" + currentTime + "', " + // modified time
                            "'该会话创建于：" + formatTime + "');";  // comment
                    log.debug("sql_session: " + sql_session);
                    statement.executeUpdate(sql_session);

                    // 更新 relation 表
                    String sql_relation = "INSERT INTO relation VALUES (null, " +
                                "(SELECT id FROM session WHERE create_time = '" + currentTime + "') , " +
                                "(SELECT id FROM tag WHERE tag = '" +
                                currentTag +
                                "')" +
                            ");";
                    log.debug("sql_relation: " + sql_relation);
                    statement.executeUpdate(sql_relation);

                    DbUtil.close(connection, statement);
                    DialogUtils.info("会话创建成功");
                } catch (SQLException throwables) {
                    DialogUtils.warn("会话创建失败");
                    throwables.printStackTrace();
                }
            }
        });

        // 测试连接
        JButton testBtn = new JButton("测试");
        testBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("测试连接");

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

            keyLabel = new JLabel("点击按钮选择私钥");
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

            jCheckBox = new JCheckBox("启用私钥");
            jCheckBox.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 获取事件源（即复选框本身）
                    JCheckBox checkBox = (JCheckBox) e.getSource();
                    log.debug(checkBox.getText() + " 是否选中: " + checkBox.isSelected());

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
