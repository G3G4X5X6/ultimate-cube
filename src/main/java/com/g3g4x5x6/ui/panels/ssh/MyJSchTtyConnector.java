package com.g3g4x5x6.ui.panels.ssh;

import com.google.common.net.HostAndPort;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jediterm.terminal.Questioner;
import com.jediterm.terminal.TtyConnector;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;


import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;


@Deprecated
@Slf4j
public abstract class MyJSchTtyConnector<T extends Channel> implements TtyConnector{

    public static final int DEFAULT_PORT = 22;
    private InputStream myInputStream;
    private OutputStream myOutputStream;
    private Session mySession;
    private T myChannelShell;
    private AtomicBoolean isInitiated;
    private int myPort;
    private String myUser;
    private String myHost;
    private String myPassword;
    private Dimension myPendingTermSize;
    private InputStreamReader myInputStreamReader;
    private OutputStreamWriter myOutputStreamWriter;


    public MyJSchTtyConnector() {
        this.myInputStream = null;
        this.myOutputStream = null;
        this.isInitiated = new AtomicBoolean(false);
        this.myPort = 22;
        this.myUser = null;
        this.myHost = null;
        this.myPassword = null;
    }

    public MyJSchTtyConnector(String host, String user, String password) {
        this(host, 22, user, password);
    }

    public MyJSchTtyConnector(String host, int port, String user) {
        this(host, port, user, null);
    }

    public MyJSchTtyConnector(String host, int port) {
        this.myInputStream = null;
        this.myOutputStream = null;
        this.isInitiated = new AtomicBoolean(false);
        this.myPort = port;
        this.myUser = null;
        this.myHost = host;
        this.myPassword = null;
    }

    public MyJSchTtyConnector(String host, int port, String user, String password) {
        this.myInputStream = null;
        this.myOutputStream = null;
        this.isInitiated = new AtomicBoolean(false);
        this.myPort = 22;
        this.myUser = null;
        this.myHost = null;
        this.myPassword = null;
        this.myHost = host;
        this.myPort = port;
        this.myUser = user;
        this.myPassword = password;
    }

    public void resize(@NotNull Dimension termSize) {
        this.myPendingTermSize = termSize;
        if (this.myChannelShell != null) {
            this.resizeImmediately();
        }

    }

    protected abstract void setPtySize(T var1, int var2, int var3, int var4, int var5);

    private void resizeImmediately() {
        if (this.myPendingTermSize != null) {
            this.setPtySize(this.myChannelShell, this.myPendingTermSize.width, this.myPendingTermSize.height, 0, 0);
            this.myPendingTermSize = null;
        }

    }

    public void close() {
        if (this.mySession != null) {
            this.mySession.disconnect();
            this.mySession = null;
            this.myInputStream = null;
            this.myOutputStream = null;
        }

    }

    protected abstract T openChannel(Session var1) throws JSchException;

    protected abstract void configureChannelShell(T var1);

    public boolean init(Questioner q) {
        this.getAuthDetails(q);

        boolean var3 = true;
        try {
            this.mySession = this.connectSession(q);
            this.myChannelShell = this.openChannel(this.mySession);
            this.configureChannelShell(this.myChannelShell);
            this.myInputStream = this.myChannelShell.getInputStream();
            this.myOutputStream = this.myChannelShell.getOutputStream();
            this.myInputStreamReader = new InputStreamReader(this.myInputStream, StandardCharsets.UTF_8);
            this.myChannelShell.connect();
            this.resizeImmediately();
            boolean var2 = true;
            return var2;
        } catch (IOException var8) {
            q.showMessage(var8.getMessage());
            log.error("Error opening channel", var8);
            var3 = false;
            return var3;
        } catch (JSchException var9) {
            q.showMessage(var9.getMessage());
            log.error("Error opening session or channel", var9);
            var3 = false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.isInitiated.set(true);
        }

        return var3;
    }

    private Session connectSession(Questioner questioner) throws Exception {
        JSch jsch = new JSch();
        this.configureJSch(jsch);
        Session session = jsch.getSession(this.myUser, this.myHost, this.myPort);
        QuestionerUserInfo ui = new QuestionerUserInfo(questioner);
        if (this.myPassword != null) {
            session.setPassword(this.myPassword);
            ui.setPassword(this.myPassword);
        }

        session.setUserInfo(ui);
        Properties config = new Properties();
        config.put("compression.s2c", "zlib,none");
        config.put("compression.c2s", "zlib,none");
        config.put("StrictHostKeyChecking", "no");
        this.configureSession(session, config);
        session.connect();
        session.setTimeout(6000);
        session.sendKeepAliveMsg();
        return session;
    }

    protected void configureJSch(JSch jsch) throws JSchException {
    }

    protected void configureSession(Session session, Properties config) throws JSchException {
        session.setConfig(config);
        session.setTimeout(5000);
    }

    private void getAuthDetails(Questioner q) {
        while (true) {
            if (this.myHost == null) {
                this.myHost = q.questionVisible("host: ", "localhost");
            }

            if (this.myHost != null && this.myHost.length() != 0) {
                try {
                    HostAndPort hostAndPort = HostAndPort.fromString(this.myHost);
                    this.myHost = hostAndPort.getHost();
                    this.myPort = hostAndPort.getPortOrDefault(this.myPort);
                } catch (IllegalArgumentException var3) {
                    q.showMessage(var3.getMessage());
                    this.myHost = q.questionVisible("host: ", this.myHost);
                    continue;
                }

                if (this.myUser == null) {
                    this.myUser = q.questionVisible("Username: ", "");
//                    this.myUser = q.questionVisible("user: ", System.getProperty("user.name").toLowerCase());
                }

                if (this.myUser != null && this.myUser.length() != 0) {
                    return;
                }
            }
        }
    }

    public String getName() {
        return this.myHost != null ? this.myHost : "Remote";
    }

    public int read(char[] buf, int offset, int length) throws IOException {
        return this.myInputStreamReader.read(buf, offset, length);
    }

    public int read(byte[] buf, int offset, int length) throws IOException {
        return this.myInputStream.read(buf, offset, length);
    }

    public void write(byte[] bytes) throws IOException {
        if (this.myOutputStream != null) {
            this.myOutputStream.write(bytes);
            this.myOutputStream.flush();
        }
    }

    public boolean isConnected() {
        return this.myChannelShell != null && this.myChannelShell.isConnected();
    }

    public void write(String string) throws IOException {
        this.write(string.getBytes(StandardCharsets.UTF_8));
    }

    public int waitFor() throws InterruptedException {
        while (!this.isInitiated.get() || isRunning(this.myChannelShell)) {
            Thread.sleep(100L);
        }

        return this.myChannelShell.getExitStatus();
    }

    private static boolean isRunning(Channel channel) {
        return channel != null && channel.getExitStatus() < 0 && channel.isConnected();
    }
}
