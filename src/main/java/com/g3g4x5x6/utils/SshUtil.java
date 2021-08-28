package com.g3g4x5x6.utils;

import com.g3g4x5x6.ui.panels.ssh.MyJSchShellTtyConnector;
import com.g3g4x5x6.ui.panels.ssh.SshSettingsProvider;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.hosts.HostConfigEntry;
import org.apache.sshd.client.session.ClientSession;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SshUtil {

    private SshUtil(){
        // 工具类小技巧
    }

    public static int testConnection(String host, String port){

        HostConfigEntry hostConfigEntry = new HostConfigEntry();
        hostConfigEntry.setHostName(host);
        hostConfigEntry.setHost(host);
        hostConfigEntry.setPort(Integer.parseInt(port));

        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        if (host.equals("")) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 2;
        }

        try {
            ClientSession session = client.connect(hostConfigEntry).verify(2000).getClientSession();
            session.close();
            client.close();
            return 1;
        } catch (IOException ioException) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ioException.printStackTrace();
        }
        return 0;
    }


    @NotNull
    public static JediTermWidget createTerminalWidget(String host, String port, String username, String password) {
        JediTermWidget widget = new JediTermWidget(new SshSettingsProvider());
        widget.setTtyConnector(createTtyConnector(host, port, username, password));
        widget.start();
        return widget;
    }

    // TODO 创建 sFTP channel
    private @NotNull static TtyConnector createTtyConnector(String host, String port, String username, String password) {
        try {
            if (username.equals("")) {
                return new MyJSchShellTtyConnector(host, Integer.parseInt(port));
            }
            if (password.equals("")) {
                return new MyJSchShellTtyConnector(host, port, username);
            }
            return new MyJSchShellTtyConnector(host, Integer.parseInt(port), username, password);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
