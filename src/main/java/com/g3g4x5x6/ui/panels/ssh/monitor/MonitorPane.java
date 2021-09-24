package com.g3g4x5x6.ui.panels.ssh.monitor;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


@Slf4j
public class MonitorPane extends JPanel {

    private JToolBar toolBar;

    // 远程文件系统
    private SshClient client;
    private SftpFileSystem fs;
    private String host;
    private int port;
    private String user;
    private String pass;

    public MonitorPane(){
        this.setLayout(new BorderLayout());
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        initToolBarAction();

        this.add(new JLabel("敬请期待!"), BorderLayout.CENTER);
        this.add(toolBar, BorderLayout.NORTH);
    }

    public MonitorPane(String host, int port, String user, String pass){
        this();

        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    private void initToolBarAction(){
        JButton freshBtn = new JButton();
        freshBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/refresh.svg"));
        freshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Refresh");
            }
        });

        toolBar.add(freshBtn);
    }
}
