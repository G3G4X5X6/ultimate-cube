package com.g3g4x5x6.dashboard;


import com.g3g4x5x6.ftp.FtpPane;
import com.g3g4x5x6.serial.SerialPane;
import com.g3g4x5x6.ssh.panel.NewSshPane;
import com.g3g4x5x6.telnet.TelnetPane;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

import static com.g3g4x5x6.MainFrame.mainTabbedPane;

/**
 * 仪表板的快速启动面板
 */
@Slf4j
public class QuickStarterPane extends JTabbedPane {

    public QuickStarterPane() {
        this.addTab("SSH", new NewSshPane(mainTabbedPane));
        this.addTab("Serial", new SerialPane(mainTabbedPane));
        this.addTab("Telnet", new TelnetPane(mainTabbedPane));
        this.addTab("FTP", new FtpPane(mainTabbedPane));
    }

}
