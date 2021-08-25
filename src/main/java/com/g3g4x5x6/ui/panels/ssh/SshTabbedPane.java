package com.g3g4x5x6.ui.panels.ssh;

import com.g3g4x5x6.ui.panels.ssh.sftp.SftpPane;
import com.jediterm.terminal.ui.JediTermWidget;

import javax.swing.*;

public class SshTabbedPane extends JTabbedPane {
    private JTabbedPane mainTabbedPane;

    public SshTabbedPane(JTabbedPane mainTabbedPane, JediTermWidget Ssh, String hostField, String portField, String userField, String passField) {
        this.mainTabbedPane = mainTabbedPane;

        this.addTab("SSH", Ssh);
        this.addTab("SFTP", new SftpPane(hostField, portField, userField, passField));
        this.addTab("Monitor", new JPanel());
        this.addTab("Editor", new JPanel());
    }

}
