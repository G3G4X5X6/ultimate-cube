import org.apache.commons.net.telnet.TelnetClient;
import org.codehaus.plexus.util.IOUtil;


import java.io.IOException;

public class TestTelnet {
    public static void main(String[] args) throws IOException {
        TelnetClient telnet = new TelnetClient();
        String remoteip = "172.17.200.12";
        int remoteport = 23;
        telnet.connect(remoteip, remoteport);
        System.out.println(telnet.isAvailable());
        System.out.println(telnet.isConnected());

        telnet.disconnect();
        System.exit(0);
    }
}
