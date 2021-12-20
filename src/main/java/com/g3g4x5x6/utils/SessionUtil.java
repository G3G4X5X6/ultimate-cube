package com.g3g4x5x6.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.ui.panels.ssh.SshTabbedPane;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.swing.tree.TreePath;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class SessionUtil {
    private SessionUtil(){

    }

    public static void openSshSession(String sessionFile){
        try {
            File file = new File(sessionFile);
            if (!sessionFile.contains("recent_ssh_")){
                String recentPath = ConfigUtil.getWorkPath() + "/sessions/recent_" + file.getName();
                if (!Files.exists(Path.of(recentPath))){
                    Files.copy(new BufferedInputStream(new FileInputStream(file)), Path.of(recentPath));
                } else {
                    try{
                        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(recentPath));
                        BufferedReader fileReader = new BufferedReader(new FileReader(file));
                        fileWriter.write(fileReader.readLine());
                        fileWriter.flush();
                        fileWriter.close();
                        fileReader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSON.parseObject(json);
            String session = jsonObject.getString("sessionName");
            String host = jsonObject.getString("sessionAddress");
            String port = jsonObject.getString("sessionPort");
            String user = jsonObject.getString("sessionUser");
            String pass = jsonObject.getString("sessionPass");
            if (SshUtil.testConnection(host, port) == 1) {
                String defaultTitle = session.equals("") ? "未命名" : session;
                MainFrame.mainTabbedPane.addTab(defaultTitle, new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg"),
                        new SshTabbedPane(host, port, user, pass )
                );
                MainFrame.mainTabbedPane.setSelectedIndex(MainFrame.mainTabbedPane.getTabCount() - 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertPathToTag(TreePath treePath) {
        StringBuilder tempPath = new StringBuilder("");
        if (treePath == null) {
            return "";
        }

        String path = treePath.toString();
        String[] paths = path.substring(1, path.length() - 1).split(",");
        for (String temp : paths) {
            temp = temp.strip();
            tempPath.append(temp);
            tempPath.append("/");
        }
        tempPath.deleteCharAt(tempPath.length() - 1);

        log.debug(String.valueOf(tempPath));
        return tempPath.toString();
    }
}
