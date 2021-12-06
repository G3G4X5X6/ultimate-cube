package com.g3g4x5x6.utils;

import com.g3g4x5x6.ui.panels.ssh.MyJSchShellTtyConnector;
import com.g3g4x5x6.ui.panels.ssh.SshSettingsProvider;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.config.hosts.HostConfigEntry;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.sftp.client.SftpClient;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;


@Slf4j
public class SshUtil {

    private SshUtil() {
        // 工具类小技巧
    }

    public static int testConnection(String host, String port) {

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
                log.debug(e.getMessage());
            }
            log.debug(ioException.getMessage());
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
    private @NotNull
    static TtyConnector createTtyConnector(String host, String port, String username, String password) {
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

    public static String exec(String host, String username, String password, int port, long defaultTimeout, String command) throws Exception {
        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        try (ClientSession session = client.connect(username, host, port).verify(defaultTimeout, TimeUnit.SECONDS).getSession()) {
            session.addPasswordIdentity(password);
            session.auth().verify(defaultTimeout, TimeUnit.SECONDS);
            try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                 ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                 ClientChannel channel = session.createChannel(Channel.CHANNEL_EXEC, command)) // to execute remote commands
            {
                channel.setOut(responseStream);
                channel.setErr(errorStream);
                try {
                    channel.open().verify(defaultTimeout, TimeUnit.SECONDS);
                    try (OutputStream pipedIn = channel.getInvertedIn()) {
                        pipedIn.write(command.getBytes());
                        pipedIn.flush();
                    }
                    channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),
                            TimeUnit.SECONDS.toMillis(defaultTimeout));
                    String error = new String(errorStream.toByteArray());
                    if (!error.isEmpty()) {
                        throw new Exception(error);
                    }
                    return responseStream.toString();
                } finally {
                    channel.close(false);
                }
            }
        } finally {
            client.stop();
        }
    }
}
