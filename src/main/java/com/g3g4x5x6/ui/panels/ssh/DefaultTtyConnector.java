package com.g3g4x5x6.ui.panels.ssh;

import com.jediterm.terminal.Questioner;
import com.jediterm.terminal.TtyConnector;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.session.ClientSession;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


@Slf4j
public class DefaultTtyConnector implements TtyConnector {
    private ClientSession session;
    private ChannelShell channel;

    private PipedOutputStream channelOut;
    private InputStream channelIn;
    private OutputStream outputStream;
    private BufferedReader reader;
    private BufferedWriter writer;

    public DefaultTtyConnector(ClientSession clientSession){
        this.session = clientSession;
    }

    @Override
    public boolean init(Questioner questioner) {
        try {
            PipedOutputStream out = new PipedOutputStream();
            channelIn = new PipedInputStream(out);
            channelOut = new PipedOutputStream();
            PipedInputStream in = new PipedInputStream(channelOut);
            reader = new BufferedReader(new InputStreamReader(in));
            writer = new BufferedWriter(new OutputStreamWriter(out));

            channel = initClientChannel(session, channelIn, channelOut);

            outputStream = channel.getInvertedIn();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static ChannelShell initClientChannel(ClientSession session, InputStream input,
                                                   OutputStream output) throws IOException {
        ChannelShell channel = session.createShellChannel();
        String lang = (String)System.getenv().get("LANG");
        channel.setEnv("LANG", lang != null ? lang : "zh_CN.UTF-8");
        channel.setPtyType("xterm");
        channel.setIn(input);
        channel.setOut(output);
        channel.setErr(output);
        channel.open().verify(3000, TimeUnit.MILLISECONDS);

        return channel;
    }

    @SneakyThrows
    @Override
    public void close() {
        channel.close();
    }

    @Override
    public String getName() {
        return "SSH";
    }

    @Override
    public int read(char[] chars, int i, int i1) throws IOException {
        return reader.read(chars, i, i1);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        outputStream.write(bytes);
        outputStream.flush();
    }

    @Override
    public boolean isConnected() {
        return channel.isOpen();
    }

    @Override
    public void write(String s) throws IOException {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>" + s);
        this.write(s.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int waitFor() throws InterruptedException {
        return channel.getExitStatus();
    }

    @Override
    public boolean ready() throws IOException {
        return true;
    }
}
