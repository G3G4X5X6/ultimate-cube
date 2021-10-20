package com.g3g4x5x6.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.panels.ssh.SshTabbedPane;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SessionUtil {
    private SessionUtil(){

    }

    public static void openSshSession(String sessionFile, JTabbedPane mainTabbedPane){
        try {
            File file = new File(sessionFile);
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSON.parseObject(json);

            String session = jsonObject.getString("sessionName");
            String host = jsonObject.getString("sessionAddress");
            String port = jsonObject.getString("sessionPort");
            String user = jsonObject.getString("sessionUser");
            String pass = jsonObject.getString("sessionPass");
            if (SshUtil.testConnection(host, port) == 1) {
                String defaultTitle = session.equals("") ? "未命名" : session;
                mainTabbedPane.addTab(defaultTitle, new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg"),
                        new SshTabbedPane(mainTabbedPane,
                                SshUtil.createTerminalWidget(host, port, user, pass),
                                host, port, user, pass
                        )
                );
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
