package com.g3g4x5x6.remote.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.config.hosts.HostConfigEntry;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;

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

    public static String exec(String host, String username, String password, int port, long defaultTimeout, String command) throws Exception {
        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        try (ClientSession session = client.connect(username, host, port).verify(defaultTimeout, TimeUnit.MILLISECONDS).getSession()) {
            session.addPasswordIdentity(password);
            session.auth().verify(defaultTimeout, TimeUnit.MILLISECONDS);

            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            ClientChannel channel = session.createChannel(Channel.CHANNEL_EXEC, command);   // to execute remote commands
            channel.setOut(responseStream);
            channel.setErr(errorStream);
            channel.open().verify(defaultTimeout, TimeUnit.MILLISECONDS);
            channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(defaultTimeout));
            String error = new String(errorStream.toByteArray());
            log.debug(error);
            return responseStream.toString();
        } finally {
            client.stop();
        }
    }

    /**
     * Test
     */
    public static String execCmd(ClientSession session, String command) throws Exception {
        try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
             ClientChannel channel = session.createChannel(Channel.CHANNEL_SHELL)) {
            channel.setOut(responseStream);
            try {
                channel.open().verify(3, TimeUnit.SECONDS);
                try (OutputStream pipedIn = channel.getInvertedIn()) {
                    pipedIn.write(command.getBytes());
                    pipedIn.flush();
                }

                channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),
                        TimeUnit.SECONDS.toMillis(3));
                String responseString = new String(responseStream.toByteArray());
                System.out.println(responseString);
                return responseString;
            } finally {
                channel.close(false);
                return null;
            }
        }
    }

    public static String exec(ClientSession session, String command) throws Exception {
        try (ClientChannel channel = session.createChannel(Channel.CHANNEL_EXEC, command)) {
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            channel.setOut(responseStream);
            channel.setErr(errorStream);
            channel.open().verify(3000, TimeUnit.MILLISECONDS);
            channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(3));
            String error = new String(errorStream.toByteArray());
            log.debug(error);
            return responseStream.toString();
        }
    }
}
