package com.g3g4x5x6.ui.panels.ssh;

import com.g3g4x5x6.ui.panels.ssh.editor.EditorPane;
import com.g3g4x5x6.ui.panels.ssh.monitor.MonitorPane;
import com.g3g4x5x6.ui.panels.ssh.sftp.SftpBrowser;
import com.g3g4x5x6.utils.DialogUtil;
import com.jediterm.terminal.ui.JediTermWidget;
import lombok.SneakyThrows;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.putty.PuttyKeyUtils;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;
import org.apache.sshd.sftp.client.fs.SftpFileSystemProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SessionInfo {
    // SessionInfo 实例唯一ID
    private String sessionId = UUID.randomUUID().toString();
    // 会话保存信息
    private String sessionName = "";
    private String sessionProtocol = "SSH";
    private String sessionAddress = "";
    private String sessionPort = "";
    private String sessionUser = "";
    private String sessionPass = "";
    private String sessionKeyPath = "";
    private String sessionLoginType = "";
    private String sessionComment = "";
    // SSH 会话组件
    private JediTermWidget sshPane;
    private SftpBrowser sftpBrowser;
    private MonitorPane monitorPane;
    private EditorPane editorPane;
    // SSH 连接信息
    private SshClient client;
    private ClientSession session;
    private SftpFileSystem sftpFileSystem;

    public SessionInfo() {
        // pass
    }

    public void initComponent(){
        init();
        this.sshPane = createTerminalWidget();
        this.sftpBrowser = new SftpBrowser(this.sftpFileSystem);
        this.editorPane = new EditorPane(this.sftpFileSystem);
        this.monitorPane = new MonitorPane(this.session);
    }

    private @NotNull JediTermWidget createTerminalWidget() {
        SshSettingsProvider sshSettingsProvider = new SshSettingsProvider();
        JediTermWidget widget = new JediTermWidget(sshSettingsProvider);
        widget.setTtyConnector(new DefaultTtyConnector(session));
        widget.start();
        return widget;
    }

    private void init() {
        this.client = SshClient.setUpDefaultClient();
        this.client.start();
        this.session = getSession(client);
        this.sftpFileSystem = getSftpFileSystem(session);
    }

    private ClientSession getSession(SshClient client) {
        ClientSession session;
        try {
            session = client.connect(this.sessionUser, this.sessionAddress, Integer.parseInt(this.sessionPort)).verify(5000, TimeUnit.MILLISECONDS).getSession();
            if (Files.exists(Path.of(sessionKeyPath)) && !sessionKeyPath.equalsIgnoreCase("")) {
                KeyPair keyPair = PuttyKeyUtils.DEFAULT_INSTANCE.loadKeyPairs(null, Path.of(sessionKeyPath), null).iterator().next();
                session.addPublicKeyIdentity(keyPair);
            } else {
                session.addPasswordIdentity(this.sessionPass);
            }
            session.auth().verify(15, TimeUnit.SECONDS);
            session.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, Duration.ofMinutes(3));
            session.sendIgnoreMessage("".getBytes(StandardCharsets.UTF_8));
            return session;
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            DialogUtil.error(e.getMessage());
            return null;
        }
    }

    private SftpFileSystem getSftpFileSystem(ClientSession session) {
        if (sftpFileSystem == null || !sftpFileSystem.isOpen()) {
            SftpFileSystemProvider provider = new SftpFileSystemProvider();
            try {
                return provider.newFileSystem(session);
            } catch (IOException e) {
                e.printStackTrace();
                DialogUtil.error(e.getMessage());
                return null;
            }
        } else {
            return sftpFileSystem;
        }
    }

    @SneakyThrows
    public void close() {
        if (sftpFileSystem.isOpen())
            sftpFileSystem.close();
        if (session.isOpen())
            session.close();
        if (client.isOpen())
            client.close();
    }

    public SessionInfo copy() {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setSessionName(sessionName);
        sessionInfo.setSessionProtocol(sessionProtocol);
        sessionInfo.setSessionAddress(sessionAddress);
        sessionInfo.setSessionPort(sessionPort);
        sessionInfo.setSessionUser(sessionUser);
        sessionInfo.setSessionPass(sessionPass);
        sessionInfo.setSessionKeyPath(sessionKeyPath);
        sessionInfo.setSessionLoginType(sessionLoginType);
        sessionInfo.setSessionComment(sessionComment);
        return sessionInfo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionProtocol() {
        return sessionProtocol;
    }

    public void setSessionProtocol(String sessionProtocol) {
        this.sessionProtocol = sessionProtocol;
    }

    public String getSessionAddress() {
        return sessionAddress;
    }

    public void setSessionAddress(String sessionAddress) {
        this.sessionAddress = sessionAddress;
    }

    public String getSessionPort() {
        return sessionPort;
    }

    public void setSessionPort(String sessionPort) {
        this.sessionPort = sessionPort;
    }

    public String getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(String sessionUser) {
        this.sessionUser = sessionUser;
    }

    public String getSessionPass() {
        return sessionPass;
    }

    public void setSessionPass(String sessionPass) {
        this.sessionPass = sessionPass;
    }

    public String getSessionKeyPath() {
        return sessionKeyPath;
    }

    public void setSessionKeyPath(String sessionKeyPath) {
        this.sessionKeyPath = sessionKeyPath;
    }

    public String getSessionLoginType() {
        return sessionLoginType;
    }

    public void setSessionLoginType(String sessionLoginType) {
        this.sessionLoginType = sessionLoginType;
    }

    public String getSessionComment() {
        return sessionComment;
    }

    public void setSessionComment(String sessionComment) {
        this.sessionComment = sessionComment;
    }

    public JediTermWidget getSshPane() {
        return sshPane;
    }

    public void setSshPane(JediTermWidget sshPane) {
        this.sshPane = sshPane;
    }

    public SftpBrowser getSftpBrowser() {
        return sftpBrowser;
    }

    public void setSftpBrowser(SftpBrowser sftpBrowser) {
        this.sftpBrowser = sftpBrowser;
    }

    public MonitorPane getMonitorPane() {
        return monitorPane;
    }

    public void setMonitorPane(MonitorPane monitorPane) {
        this.monitorPane = monitorPane;
    }

    public EditorPane getEditorPane() {
        return editorPane;
    }

    public void setEditorPane(EditorPane editorPane) {
        this.editorPane = editorPane;
    }

    public SshClient getClient() {
        return client;
    }

    public void setClient(SshClient client) {
        this.client = client;
    }

    public ClientSession getSession() {
        return session;
    }

    public void setSession(ClientSession session) {
        this.session = session;
    }

    public SftpFileSystem getSftpFileSystem() {
        return sftpFileSystem;
    }

    public void setSftpFileSystem(SftpFileSystem sftpFileSystem) {
        this.sftpFileSystem = sftpFileSystem;
    }
}
