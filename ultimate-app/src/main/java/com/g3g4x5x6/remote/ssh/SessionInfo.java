package com.g3g4x5x6.remote.ssh;

import com.g3g4x5x6.remote.ssh.panel.DropTargetListenerBrowserImpl;
import com.g3g4x5x6.remote.ssh.panel.FilesBrowser;
import com.g3g4x5x6.remote.utils.ShellConfig;
import com.jediterm.terminal.ui.JediTermWidget;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.core.CoreModuleProperties;
import org.apache.sshd.putty.PuttyKeyUtils;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;
import org.apache.sshd.sftp.client.fs.SftpFileSystemProvider;
import org.jetbrains.annotations.NotNull;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
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
    private JediTermWidget sshPane = null;
    private FilesBrowser sftpBrowser = null;
    // SSH 连接信息
    private SshClient client;
    private ClientSession session;
    private SftpFileSystem sftpFileSystem;
    // flag
    private boolean sftpFlag = true;

    public SessionInfo() {
        // pass
    }

    public void initComponent() throws GeneralSecurityException, IOException {
        init();
        this.sshPane = createTerminalWidget();
        if (sftpFlag)
            this.sftpBrowser = new FilesBrowser(this.sftpFileSystem);

        // 注册拖拽监听
        registerDropTarget();
    }

    @SneakyThrows
    private void registerDropTarget() {
        // 创建拖拽目标监听器
        DropTargetListenerBrowserImpl listener = new DropTargetListenerBrowserImpl(sftpFileSystem, sftpFileSystem.getDefaultDir().toString());
        // 在 JTable 上注册拖拽目标监听器
        DropTarget dropTarget = new DropTarget(sshPane, DnDConstants.ACTION_COPY_OR_MOVE, listener, true);
    }

    private @NotNull JediTermWidget createTerminalWidget() {
        SshSettingsProvider sshSettingsProvider = new SshSettingsProvider();
        JediTermWidget widget = new JediTermWidget(sshSettingsProvider);
        widget.setTtyConnector(new DefaultTtyConnector(session));
        widget.start();
        return widget;
    }

    private void init() throws GeneralSecurityException, IOException {
        this.client = SshClient.setUpDefaultClient();
        this.client.start();
        this.session = getSession(client);
        this.sftpFileSystem = getSftpFileSystem(session);
    }

    private ClientSession getSession(SshClient client) throws IOException, GeneralSecurityException {
        // TODO 可设置是否启用、时间周期
        CoreModuleProperties.HEARTBEAT_INTERVAL.set(client,
                Duration.ofSeconds(Long.parseLong(ShellConfig.getProperty("ssh.session.heartbeat.interval"))));

        ClientSession session = client.connect(this.sessionUser, this.sessionAddress, Integer.parseInt(this.sessionPort)).verify(5000, TimeUnit.MILLISECONDS).getSession();
        if (Files.exists(Path.of(sessionKeyPath)) && !sessionKeyPath.equalsIgnoreCase("")) {
            KeyPair keyPair = PuttyKeyUtils.DEFAULT_INSTANCE.loadKeyPairs(null, Path.of(sessionKeyPath), null).iterator().next();
            session.addPublicKeyIdentity(keyPair);
        } else {
            session.addPasswordIdentity(this.sessionPass);
        }
        session.auth().verify(15, TimeUnit.SECONDS);
        session.sendIgnoreMessage("".getBytes(StandardCharsets.UTF_8));
        return session;
    }

    private SftpFileSystem getSftpFileSystem(ClientSession session) throws IOException {
        if (sftpFileSystem == null || !sftpFileSystem.isOpen()) {
            SftpFileSystemProvider provider = new SftpFileSystemProvider();
            return provider.newFileSystem(session);
        } else {
            return sftpFileSystem;
        }
    }

    @SneakyThrows
    public void close() {
        if (sftpFileSystem != null && sftpFileSystem.isOpen())
            sftpFileSystem.close();
        if (session != null && session.isOpen())
            session.close();
        if (client != null && client.isOpen())
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

    public FilesBrowser getSftpBrowser() {
        return sftpBrowser;
    }

    public void setSftpBrowser(FilesBrowser sftpBrowser) {
        this.sftpBrowser = sftpBrowser;
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

    @Override
    public String toString() {
        return "SessionInfo{" +
                "sessionId='" + sessionId + '\'' +
                ", sessionName='" + sessionName + '\'' +
                ", sessionProtocol='" + sessionProtocol + '\'' +
                ", sessionAddress='" + sessionAddress + '\'' +
                ", sessionPort='" + sessionPort + '\'' +
                ", sessionUser='" + sessionUser + '\'' +
                ", sessionPass='" + sessionPass + '\'' +
                ", sessionKeyPath='" + sessionKeyPath + '\'' +
                ", sessionLoginType='" + sessionLoginType + '\'' +
                ", sessionComment='" + sessionComment + '\'' +
                ", sshPane=" + sshPane +
                ", sftpBrowser=" + sftpBrowser +
                ", client=" + client +
                ", session=" + session +
                ", sftpFileSystem=" + sftpFileSystem +
                ", sftpFlag=" + sftpFlag +
                '}';
    }
}
