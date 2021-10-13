package com.g3g4x5x6.ui.panels.session;

import com.g3g4x5x6.ui.panels.ftp.FtpPane;
import com.g3g4x5x6.ui.panels.serial.SerialPane;
import com.g3g4x5x6.ui.panels.telnet.TelnetPane;

import javax.swing.*;

public class CreateSessionTabbedPane extends JTabbedPane {

    public CreateSessionTabbedPane(JTabbedPane mainTabbedPane) {
        this.addTab("SSH", new SshPane(mainTabbedPane));
        this.addTab("Serial", new SerialPane(mainTabbedPane));
        this.addTab("Telnet", new TelnetPane(mainTabbedPane));
        this.addTab("FTP", new FtpPane(mainTabbedPane));
    }
}
