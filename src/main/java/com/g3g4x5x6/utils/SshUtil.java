package com.g3g4x5x6.utils;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.hosts.HostConfigEntry;
import org.apache.sshd.client.session.ClientSession;

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
            ClientSession session = client.connect(hostConfigEntry).verify(5000).getClientSession();
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
}
