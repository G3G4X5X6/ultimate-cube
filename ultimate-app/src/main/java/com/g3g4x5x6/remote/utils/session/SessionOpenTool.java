package com.g3g4x5x6.remote.utils.session;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.App;
import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.MainFrame;
import com.g3g4x5x6.remote.ssh.SessionInfo;
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
public class SessionOpenTool {
    private static final String freeRdpPath = AppConfig.getWorkPath() + "/bin/wfreerdp.exe";

    public static void OpenSessionByProtocol(String sessionPath, String protocol) {
        switch (protocol) {
            case "SSH":
                log.debug("SSH");
                SessionOpenTool.OpenSessionForSSH(sessionPath);
                break;
            case "RDP":
                log.debug("RDP");
                SessionOpenTool.OpenSessionForRDP(sessionPath);
            case "VNC":
                log.debug("VNC");
            case "Telnet":
                log.debug("Telnet");
            default:
                log.debug("default: nothing to do");
        }
    }

    public static void OpenSessionForSSH(String sessionPath) {
        File file = new File(sessionPath);
        if (file.exists()) {
            new Thread(() -> {
                // 等待进度条
                MainFrame.addWaitProgressBar();

                SessionInfo sessionInfo = SessionUtil.openSshSession(file.getAbsolutePath());
                if (SshUtil.testConnection(sessionInfo.getSessionAddress(), sessionInfo.getSessionPort()) == 1) {
                    String defaultTitle = sessionInfo.getSessionName().isEmpty() ? "未命名" : sessionInfo.getSessionName();
                    mainTabbedPane.addTab(defaultTitle, new FlatSVGIcon("icons/consoleRun.svg"), new SshTabbedPane(sessionInfo));
                    mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
                }
                App.sessionInfos.put(sessionInfo.getSessionId(), sessionInfo);

                // 移除等待进度条
                MainFrame.removeWaitProgressBar();
            }).start();
        }
    }

    private static void OpenSessionForRDP(String sessionPath) {

        new Thread(() -> {
            try {
                String json = FileUtils.readFileToString(new File(sessionPath), StandardCharsets.UTF_8);
                JSONObject jsonObject = JSON.parseObject(json);
                String address = jsonObject.getString("sessionAddress");
                String port = jsonObject.getString("sessionPort");
                String username = jsonObject.getString("sessionUser");
                String password = VaultUtil.decryptPasswd(jsonObject.getString("sessionPass"));
                String loginType = jsonObject.getString("sessionLoginType");
                String sessionName = jsonObject.getString("sessionName");
                String sessionComment = jsonObject.getString("sessionComment");
                ArrayList<String> cmdArgs = (ArrayList<String>) jsonObject.getJSONArray("sessionArgs").toJavaList(String.class);

                // 列表头部插入 freerdp 命令
                if (OsInfoUtil.isWindows()) {
                    cmdArgs.add(0, freeRdpPath);
                } else if (OsInfoUtil.isLinux()) {
                    log.debug("isLinux");
                } else if (OsInfoUtil.isMacOS()) {
                    log.debug("isMacOS");
                } else if (OsInfoUtil.isMacOSX()) {
                    log.debug("isMacOSX");
                }

                ArrayList<String> tmpList = (ArrayList<String>) cmdArgs.clone();
                for (String arg : tmpList) {
                    if (arg.contains("/p:")) {
                        cmdArgs.remove(arg);
                        cmdArgs.add("/p:" + password);
                    }
                }

                //创建ProcessBuilder对象
                ProcessBuilder processBuilder = new ProcessBuilder();

                // 设置执行命令及其参数列表
                processBuilder.command(cmdArgs);
                log.debug(cmdArgs.toString());

                //将标准输入流和错误输入流合并
                // 通过标准输入流读取信息就可以拿到第三方程序输出的错误信息、正常信息
                processBuilder.redirectErrorStream(true);

                //启动一个进程
                Process process = processBuilder.start();
                //读取输入流
                InputStream inputStream = process.getInputStream();
                //将字节流转成字符流
                InputStreamReader reader = new InputStreamReader(inputStream, "gbk");
                //字符缓冲区
                char[] chars = new char[1024];
                int len;
                while ((len = reader.read(chars)) != -1) {
                    String string = new String(chars, 0, len);
                    log.debug(string);
                }
                inputStream.close();
                reader.close();

                // 保存最近会话
                Path path = Path.of(sessionPath);
                if (!path.getFileName().toString().startsWith("recent_"))
                    Files.write(Path.of(AppConfig.getWorkPath() + "/sessions/recent_" + path.getFileName()), jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));

            } catch (IOException ioException) {
                log.error(ioException.getMessage());
            }
        }).start();
    }

}