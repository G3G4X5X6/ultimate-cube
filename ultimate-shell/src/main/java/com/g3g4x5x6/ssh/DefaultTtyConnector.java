package com.g3g4x5x6.ssh;

import com.jediterm.terminal.Questioner;
import com.jediterm.terminal.TtyConnector;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.PtyChannelConfiguration;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
public class DefaultTtyConnector implements TtyConnector {
    private ClientSession session;
    private ChannelShell channel;

    private Dimension myPendingTermSize;

    private OutputStream outputStream;
    private BufferedReader reader;

    public DefaultTtyConnector(ClientSession clientSession) {
        this.session = clientSession;
    }

    @Override
    public boolean init(Questioner questioner) {
        try {
            PipedOutputStream out = new PipedOutputStream();
            InputStream channelIn = new PipedInputStream(out);
            PipedOutputStream channelOut = new PipedOutputStream();
            PipedInputStream in = new PipedInputStream(channelOut);

            channel = initClientChannel(session, channelIn, channelOut);

            reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            outputStream = channel.getInvertedIn();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private ChannelShell initClientChannel(ClientSession session, InputStream input, OutputStream output) throws IOException {
        PtyChannelConfiguration ptyConfig = getPtyChannelConfiguration();
        Map<String, ?> env = getEnv();
        ChannelShell channel = session.createShellChannel(ptyConfig, env);
        channel.setIn(input);
        channel.setOut(output);
        channel.setErr(output);
        channel.open().verify(3000, TimeUnit.MILLISECONDS);
        return channel;
    }

    private PtyChannelConfiguration getPtyChannelConfiguration() {
        PtyChannelConfiguration ptyConfig = new PtyChannelConfiguration();
        ptyConfig.setPtyType("xterm");
        return ptyConfig;
    }

    private Map<String, ?> getEnv() {
        Map<String, String> env = new LinkedHashMap<>();
        String lang = System.getenv().get("LANG");
        env.put("LANG", lang != null ? lang : "zh_CN.UTF-8");
        env.put("compression.s2c", "zlib,none");
        env.put("compression.c2s", "zlib,none");
        env.put("StrictHostKeyChecking", "no");
        return env;
    }

    @SneakyThrows
    @Override
    public void close() {
        channel.close();
    }

    @Override
    public String getName() {
        String name = session.getConnectAddress().toString();
        if (name == null)
            name = "SSH";
        return name;
    }

    /**
     * TODO 本地保存会话记录：String.valueOf(chars )
     */
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
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>:::" + channel.isOpen());
        return channel.isOpen();
    }

    /**
     * TODO 本地保存命令历史记录：string
     */
    @Override
    public void write(String string) throws IOException {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>" + string);
        this.write(string.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int waitFor() throws InterruptedException {
        return channel.getExitStatus();
    }

    @Override
    public boolean ready() {
        return true;
    }

    @Override
    public void resize(Dimension termWinSize) {
        log.debug(termWinSize.height + ":" + termWinSize.width);
        this.myPendingTermSize = termWinSize;
        if (this.channel != null) {
            this.resizeImmediately();
        }
    }

    private void resizeImmediately() {
        if (this.myPendingTermSize != null) {
            this.setPtySize(this.myPendingTermSize.width, this.myPendingTermSize.height);
            this.myPendingTermSize = null;
        }
    }

    private void setPtySize(int col, int row) {
        try {
            channel.sendWindowChange(col, row);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
