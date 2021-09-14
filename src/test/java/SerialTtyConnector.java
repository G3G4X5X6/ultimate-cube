import com.fazecast.jSerialComm.SerialPort;
import com.jcraft.jsch.Session;
import com.jediterm.terminal.Questioner;
import com.jediterm.terminal.TtyConnector;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class SerialTtyConnector implements TtyConnector{

    public static final int DEFAULT_PORT = 23;
    private InputStream myInputStream;
    private OutputStream myOutputStream;
    private Session mySession;
    private AtomicBoolean isInitiated;
    private int myPort;
    private String myUser;
    private String myHost;
    private String myPassword;
    private Dimension myPendingTermSize;
    private InputStreamReader myInputStreamReader;
    private OutputStreamWriter myOutputStreamWriter;

    private SerialPort comPort;


    public SerialTtyConnector() {
        this.myInputStream = null;
        this.myOutputStream = null;
        this.myPort = 23;
        this.myUser = null;
        this.myHost = null;
        this.myPassword = null;
    }

    @Override
    public boolean init(Questioner questioner) {
        comPort = SerialPort.getCommPorts()[1];
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        this.myInputStream = comPort.getInputStream();
        this.myInputStreamReader = new InputStreamReader(comPort.getInputStream());
        this.myOutputStream = comPort.getOutputStream();
        return true;
    }

    @Override
    public void close() {

    }

    @Override
    public void resize(@NotNull Dimension termWinSize) {
        TtyConnector.super.resize(termWinSize);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int read(char[] chars, int i, int i1) throws IOException {
        return this.myInputStreamReader.read(chars, i, i1);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        this.myOutputStream.write(bytes);
        this.myOutputStream.flush();
    }

    @Override
    public boolean isConnected() {
        return comPort.isOpen();
    }

    @Override
    public void write(String s) throws IOException {
        this.myOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int waitFor() throws InterruptedException {
        return 0;
    }

    @Override
    public boolean ready() throws IOException {
        return false;
    }
}
