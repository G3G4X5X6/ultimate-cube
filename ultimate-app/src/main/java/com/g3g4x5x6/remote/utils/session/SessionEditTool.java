package com.g3g4x5x6.remote.utils.session;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.App;
import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.MainFrame;
import com.g3g4x5x6.remote.rdp.RdpPane;
import com.g3g4x5x6.remote.ssh.SessionInfo;
import com.g3g4x5x6.remote.ssh.panel.NewSshPane;
import com.g3g4x5x6.remote.ssh.panel.SshTabbedPane;
import com.g3g4x5x6.remote.utils.SshUtil;
import com.g3g4x5x6.remote.utils.VaultUtil;
import com.g3g4x5x6.utils.os.OsInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static com.g3g4x5x6.MainFrame.mainTabbedPane;


@Slf4j
public class SessionEditTool {
    private static final String freeRdpPath = AppConfig.getWorkPath() + "/bin/wfreerdp.exe";

    public static void EditSessionByProtocol(String sessionPath, String protocol) {
        try {
            switch (protocol) {
                case "SSH":
                    log.debug("SSH");
                    SessionEditTool.EditSessionForSSH(sessionPath);
                    break;
                case "RDP":
                    log.debug("RDP");
                    SessionEditTool.EditSessionForRDP(sessionPath);
                case "VNC":
                    log.debug("VNC");
                case "Telnet":
                    log.debug("Telnet");
                default:
                    log.debug("default: nothing to do");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void EditSessionForSSH(String sessionPath) throws IOException {
        File file = new File(sessionPath);
        if (file.exists()) {
            JSONObject jsonObject = JSONObject.parseObject(Files.readString(file.toPath()));

            new Thread(() -> {
                NewSshPane sshPane = new NewSshPane(mainTabbedPane);
                sshPane.setHostField(jsonObject.getString("sessionAddress"));
                sshPane.setPortField(jsonObject.getString("sessionPort"));
                sshPane.setUserField(jsonObject.getString("sessionUser"));
                sshPane.setPassField(VaultUtil.decryptPasswd(jsonObject.getString("sessionPass")));
                sshPane.setPukKey(jsonObject.getString("sessionPukKey"));
                sshPane.setSessionName(jsonObject.getString("sessionName"));
                sshPane.setCommentText(jsonObject.getString("sessionComment"));
                sshPane.setAuthType(jsonObject.getString("sessionLoginType"));
                sshPane.setCategory(jsonObject.getString("sessionCategory"));
                sshPane.setEditPath(jsonObject.getString("sessionFilePath"));
                mainTabbedPane.insertTab("编辑-SSH", new FlatSVGIcon("icons/addToDictionary.svg"), sshPane, "编辑会话", mainTabbedPane.getTabCount());
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            }).start();
        }
    }

    private static void EditSessionForRDP(String sessionPath) throws IOException {
        File file = new File(sessionPath);
        if (file.exists()) {
            JSONObject jsonObject = JSONObject.parseObject(Files.readString(file.toPath()));

            new Thread(() -> {
                RdpPane rdpPane = new RdpPane(mainTabbedPane);
                rdpPane.setHostField(jsonObject.getString("sessionAddress"));
                rdpPane.setPortField(jsonObject.getString("sessionPort"));
                rdpPane.setUserField(jsonObject.getString("sessionUser"));
                rdpPane.setPassField(VaultUtil.decryptPasswd(jsonObject.getString("sessionPass")));
                rdpPane.setSessionName(jsonObject.getString("sessionName"));
                // TODO 待实现
                rdpPane.setCommentText(jsonObject.getString("sessionComment"));
                rdpPane.setAuthType(jsonObject.getString("sessionLoginType"));
                rdpPane.setCategory(jsonObject.getString("sessionCategory"));
                rdpPane.setEditPath(sessionPath);
                mainTabbedPane.insertTab("编辑-RDP", new FlatSVGIcon("icons/addToDictionary.svg"), rdpPane, "编辑会话", mainTabbedPane.getTabCount());
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            }).start();
        } else {
            log.debug("文件不存在：{}", file.getAbsolutePath());
        }
    }

}