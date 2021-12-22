import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.common.session.SessionHeartbeatController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class TestClientChannel {

    public static void main(String[] args) {
        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        ClientSession session;
        try {
            session = client.connect("security", "172.17.200.104", 22).verify(3000, TimeUnit.MILLISECONDS).getSession();
            session.addPasswordIdentity("123456");
            session.auth().verify(3000, TimeUnit.MILLISECONDS);     // TODO No more authentication methods available
            session.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, Duration.ofMinutes(3));

            try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                 ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                 ClientChannel channel = session.createChannel(Channel.CHANNEL_SHELL)) // to execute remote commands
            {
                channel.setOut(responseStream);
                channel.setErr(errorStream);
                try {
                    channel.open().verify(3, TimeUnit.SECONDS);
                    try (OutputStream pipedIn = channel.getInvertedIn()) {
                        pipedIn.write("ls -l\n".getBytes());
                        pipedIn.flush();
                    }
                    channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),
                            TimeUnit.SECONDS.toMillis(3));
                    String error = new String(errorStream.toByteArray());
                    if (!error.isEmpty()) {
                        throw new Exception(error);
                    }
                    System.out.println(responseStream.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    channel.close(false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
