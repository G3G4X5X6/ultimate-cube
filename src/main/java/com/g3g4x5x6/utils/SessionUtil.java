package com.g3g4x5x6.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.panels.ssh.SessionInfo;
import com.g3g4x5x6.ui.panels.ssh.SshTabbedPane;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class SessionUtil {
    private SessionUtil() {

    }

    public static void openSshSession(JTabbedPane tabbedPane, String sessionFile) {
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

            SessionInfo sessionInfo = new SessionInfo();
            sessionInfo.setSessionName(session);
            sessionInfo.setSessionAddress(host);
            sessionInfo.setSessionPort(port);
            sessionInfo.setSessionUser(user);
            sessionInfo.setSessionPass(pass);
            sessionInfo.setSessionKeyPath(privateKey);
            sessionInfo.setSessionLoginType(loginType);
            sessionInfo.setSessionProtocol(protocol);
            sessionInfo.setSessionComment(comment);

            if (SshUtil.testConnection(host, port) == 1) {
                String defaultTitle = session.equals("") ? "未命名" : session;
                tabbedPane.addTab(defaultTitle, new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg"),
                        new SshTabbedPane(sessionInfo)
                );
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            }

            // 更新最近会话
            String recentPath;
            if (sessionFile.contains("recent_ssh_")) {
                recentPath = file.getAbsolutePath();
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }else{
                recentPath = ConfigUtil.getWorkPath() + "/sessions/recent_" + file.getName();
            }
            Files.write(Path.of(recentPath), json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
