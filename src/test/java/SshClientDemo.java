import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import com.g3g4x5x6.utils.SshUtil;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;

// https://stackoverflow.com/questions/63075107/how-to-execute-remote-commands-using-//apache-mina-sshd
public class SshClientDemo {
    public static String getFileList(String host, String username, String password, int port, long defaultTimeout) throws Exception {

        // uses the default id_rsa and id_rsa.pub files to connect to ssh server
        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        try (ClientSession session = client.connect(username, host, port).verify(defaultTimeout, TimeUnit.SECONDS).getSession()) {
            session.addPasswordIdentity(password);
            session.auth().verify(defaultTimeout, TimeUnit.SECONDS);
            try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                 ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                 ClientChannel channel = session.createChannel(Channel.CHANNEL_EXEC, "ls -la")) // to execute remote commands
            {
                channel.setOut(responseStream);
                channel.setErr(errorStream);
                try {
                    channel.open().verify(defaultTimeout, TimeUnit.SECONDS);
                    try (OutputStream pipedIn = channel.getInvertedIn()) {
                        pipedIn.write("dir".getBytes());
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

    public static void main(String[] args) {
        try {
            System.out.println(SshUtil.exec("172.17.200.104", "security", "123456", 22, 3000, "ps axu"));;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}