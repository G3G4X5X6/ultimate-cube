package com.g3g4x5x6.tools.external;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.os.OsInfoUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;


@Slf4j
public class ExternalToolIntegration {
    public static final String settings_path = ConfigUtil.getWorkPath() + "/tools/external_tools/settings.json";
    private final LinkedHashMap<String, LinkedList<JMenuItem>> items = new LinkedHashMap<>();
    private final JSONArray array;


    public ExternalToolIntegration() {
        if (!Files.exists(Path.of(settings_path))) {
            log.debug("找不到外部集成工具配置文件: " + settings_path);
        }
        array = parseSettings();
        initAllMenuItem();
    }

    public void initExternalToolsMenu(JMenu externalSubMenu) {
        for (String category : items.keySet()) {
            JMenu cateMenu = new JMenu(category);
            for (JMenuItem item : items.get(category)) {
                cateMenu.add(item);
            }
            externalSubMenu.add(cateMenu);
        }
    }

    private JSONArray parseSettings() {
        JSONArray array;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(settings_path));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            log.debug(json.toString());
            array = JSONObject.parseArray(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        }
        if (array == null)
            array = new JSONArray();
        return array;
    }

    private void initAllMenuItem() {
        for (Object obj : array) {
            JSONObject tool = (JSONObject) obj;
            // Windows
            if (OsInfoUtil.isWindows() && tool.get("platform").toString().equalsIgnoreCase("windows")) {
                initMenuItem(tool);
            }
            // Linux
            if (OsInfoUtil.isLinux() && tool.get("platform").toString().equalsIgnoreCase("linux")) {
                initMenuItem(tool);
            }
            // MacOS
            if (OsInfoUtil.isMacOS() && (tool.get("platform").toString().equalsIgnoreCase("macos") || tool.get("platform").toString().equalsIgnoreCase("macosx"))) {
                initMenuItem(tool);
            }

        }
    }

    private void initMenuItem(JSONObject tool) {
        JMenuItem tempItem = new JMenuItem(tool.get("name").toString());
        tempItem.addActionListener(e -> {
            log.debug("执行：" + tool.get("start"));
            new Thread(() -> exec(replaceBasePath((String) tool.get("start")), replaceBasePath((String) tool.get("workdir")))).start();
        });
        if (items.containsKey(tool.get("category").toString())) {
            items.get(tool.get("category").toString()).add(tempItem);
        } else {
            LinkedList<JMenuItem> tempList = new LinkedList<>();
            tempList.add(tempItem);
            items.put(tool.get("category").toString(), tempList);
        }
    }

    /**
     * 内置变量替换：%BasePath%
     */
    private String replaceBasePath(String path) {
        return path.replaceAll(
                "%BasePath%",
                Path.of(ConfigUtil.getWorkPath() + "/tools/external_tools").toString().replaceAll("\\\\", "/")
        );
    }

    private void exec(String commandStr, String workDir) {
        try {
            //创建ProcessBuilder对象
            ProcessBuilder processBuilder = new ProcessBuilder();

            //封装执行的第三方程序(命令)
            ArrayList<String> cmdList = new ArrayList<>(Arrays.asList(commandStr.split("\\s+")));
            log.debug(cmdList.toString());

            // 设置执行命令及其参数列表
            processBuilder.command(cmdList);

            // 设置程序工作目录
            processBuilder.directory(new File(workDir));

            //将标准输入流和错误输入流合并
            // 通过标准输入流读取信息就可以拿到第三方程序输出的错误信息、正常信息
            processBuilder.redirectErrorStream(true);

            //启动一个进程
            Process process = processBuilder.start();

            InputStream inputStream = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream, "gbk");
            char[] chars = new char[1024];
            int len;
            while ((len = reader.read(chars)) != -1) {
                String string = new String(chars, 0, len);
                log.debug(string);
            }
            inputStream.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
