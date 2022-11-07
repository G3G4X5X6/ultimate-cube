package com.g3g4x5x6.remote;

import com.g3g4x5x6.remote.ftp.FtpPane;
import com.g3g4x5x6.remote.serial.SerialPane;
import com.g3g4x5x6.remote.ssh.panel.NewSshPane;
import com.g3g4x5x6.remote.telnet.TelnetPane;

import javax.swing.*;

public class NewTabbedPane extends JTabbedPane {

    public NewTabbedPane(JTabbedPane mainTabbedPane) {
        this.addTab("SSH", new NewSshPane(mainTabbedPane));
        this.addTab("Serial", new SerialPane(mainTabbedPane));
        this.addTab("Telnet", new TelnetPane(mainTabbedPane));
        this.addTab("FTP", new FtpPane(mainTabbedPane));
    }
}
