package com.g3g4x5x6.remote;

import com.g3g4x5x6.remote.rdp.RdpPane;
import com.g3g4x5x6.remote.serial.SerialPane;
import com.g3g4x5x6.remote.ssh.panel.NewSshPane;
import com.g3g4x5x6.remote.telnet.TelnetPane;
import com.g3g4x5x6.remote.vnc.VncPane;

import javax.swing.*;

public class NewTabbedPane extends JTabbedPane {

    public NewTabbedPane(JTabbedPane mainTabbedPane) {
        this.addTab("SSH", new NewSshPane(mainTabbedPane));
        this.addTab("RDP", new RdpPane(mainTabbedPane));
        this.addTab("VNC", new VncPane(mainTabbedPane));
        this.addTab("Telnet", new TelnetPane(mainTabbedPane));
        this.addTab("Serial", new SerialPane(mainTabbedPane));
    }
}
