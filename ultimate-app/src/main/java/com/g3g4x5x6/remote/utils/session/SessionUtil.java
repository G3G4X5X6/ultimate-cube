package com.g3g4x5x6.remote.utils.session;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.editor.util.EditorUtil;
import com.g3g4x5x6.remote.ssh.SessionInfo;
import com.g3g4x5x6.remote.utils.VaultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;
import org.apache.sshd.sftp.client.fs.SftpFileSystemProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class SessionUtil {
    private SessionUtil() {

    }

    public static RTextScrollPane getLogArea(String caused, StackTraceElement[] traceElements){
        RSyntaxTextArea logPanel = EditorUtil.createTextArea();
        logPanel.setEditable(false);
        logPanel.setSyntaxEditingStyle("text/unix");
        logPanel.setCodeFoldingEnabled(true);
        logPanel.setAutoscrolls(true);

        logPanel.append("-----------------------------------------------------------------\n");
        logPanel.append("Caused by: " + caused + "\n");
        logPanel.append("-----------------------------------------------------------------\n");

        for(StackTraceElement element : traceElements){
            logPanel.append("\t at " + element.toString() + "\n");
        }

        return new RTextScrollPane(logPanel);
    }

    public static SftpFileSystem getSftpFileSystem(ClientSession session) throws IOException {
        SftpFileSystemProvider provider = new SftpFileSystemProvider();
        return provider.newFileSystem(session);
    }

    public static SessionInfo openSshSession(String sessionFile) {

        SessionInfo sessionInfo = new SessionInfo();

        try {
            File file = new File(sessionFile);

            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSON.parseObject(json);
            String session = jsonObject.getString("sessionName");
            String host = jsonObject.getString("sessionAddress");
            String port = jsonObject.getString("sessionPort");
            String user = jsonObject.getString("sessionUser");
            String pass = jsonObject.getString("sessionPass");
            String privateKey = jsonObject.getString("sessionKeyPath");
            String loginType = jsonObject.getString("sessionLoginType");
            String protocol = jsonObject.getString("sessionProtocol");
            String comment = jsonObject.getString("sessionComment");

            sessionInfo.setSessionName(session);
            sessionInfo.setSessionAddress(host);
            sessionInfo.setSessionPort(port);
            sessionInfo.setSessionUser(user);
            sessionInfo.setSessionPass(VaultUtil.decryptPasswd(pass));
            sessionInfo.setSessionKeyPath(privateKey);
            sessionInfo.setSessionLoginType(loginType);
            sessionInfo.setSessionProtocol(protocol);
            sessionInfo.setSessionComment(comment);

            // 更新最近会话
            String recentPath;
            if (sessionFile.contains("recent_ssh_")) {
                recentPath = file.getAbsolutePath();
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            } else {
                recentPath = AppConfig.getWorkPath() + "/sessions/recent_" + file.getName();
            }
            Files.write(Path.of(recentPath), json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessionInfo;
    }
}
