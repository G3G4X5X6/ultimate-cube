import com.jediterm.terminal.Questioner;
import com.jediterm.terminal.TtyConnector;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TelnetTtyConnector implements TtyConnector {
    private String host;
    private int port;
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private OutputStream outputStream;

    private TelnetClient telnet;

    public TelnetTtyConnector(String host, int port) {
        this.host = host;
        this.port = port;
        this.telnet = new TelnetClient();
        final TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);

        try {
            this.telnet.connect(this.host, this.port);
            this.telnet.setKeepAlive(true);
            this.telnet.addOptionHandler(ttopt);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidTelnetOptionException e) {
            e.printStackTrace();
        }
        this.inputStream = telnet.getInputStream();
        this.outputStream = telnet.getOutputStream();
        this.inputStreamReader = new InputStreamReader(this.inputStream);
    }

    @Override
    public boolean init(Questioner questioner) {

        return true;
    }

    @Override
    public void close() {
        try {
            this.telnet.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resize(@NotNull Dimension termWinSize) {
        TtyConnector.super.resize(termWinSize);
    }

    @Override
    public String getName() {
        return this.host != null ? this.host : "Remote";
    }

    @Override
    public int read(char[] chars, int i, int i1) throws IOException {
        return this.inputStreamReader.read(chars, i, i1);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        if (this.outputStream != null) {
            this.outputStream.write(bytes);
            this.outputStream.flush();
        }
    }

    @Override
    public boolean isConnected() {
        return this.telnet.isConnected();
    }

    @Override
    public void write(String s) throws IOException {
        this.write(s.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int waitFor() throws InterruptedException {
        return 0;
    }

    @Override
    public boolean ready() throws IOException {
        return true;
    }
}
