package com.g3g4x5x6;

import com.g3g4x5x6.ftp.FtpPane;
import com.g3g4x5x6.serial.SerialPane;
import com.g3g4x5x6.ssh.panel.NewSshPane;
import com.g3g4x5x6.telnet.TelnetPane;

import javax.swing.*;

public class NewTabbedPane extends JTabbedPane {

    public NewTabbedPane(JTabbedPane mainTabbedPane) {
        this.addTab("SSH", new NewSshPane(mainTabbedPane));
        this.addTab("Serial", new SerialPane(mainTabbedPane));
        this.addTab("Telnet", new TelnetPane(mainTabbedPane));
        this.addTab("FTP", new FtpPane(mainTabbedPane));
    }
}
