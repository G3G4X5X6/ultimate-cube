package com.g3g4x5x6.ui.panels.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.g3g4x5x6.os.OsInfoUtil;
import com.g3g4x5x6.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


@Slf4j
public class ExternalToolIntegration {
    private String settings_path = ConfigUtil.getWorkPath() + "/tools/settings.json";
    private List<LinkedHashMap<String, String>> tools = new LinkedList<>();

    public ExternalToolIntegration(JMenu externalSubMenu){
        if (Files.exists(Path.of(settings_path))){
            log.debug("解析外部集成工具配置： " + settings_path);
            parseSettings();
            initToolsActionItem(externalSubMenu);
        }
    }

    private void initToolsActionItem(JMenu externalSubMenu) {
        for (LinkedHashMap<String, String> tool : tools){
            log.debug(tool.get("name"));
            if (OsInfoUtil.isWindows() && tool.get("platform").toLowerCase().equals("windows")){
                externalSubMenu.add(new AbstractAction(tool.get("name")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        log.debug("执行：" + tool.get("start"));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                exec(tool.get("start"));
                            }
                        }).start();
                    }
                });
            }
            if (OsInfoUtil.isLinux() && tool.get("platform").toLowerCase().equals("linux")){
                externalSubMenu.add(new AbstractAction(tool.get("name")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        log.debug("执行：" + tool.get("start"));
                    }
                });
            }
            if (OsInfoUtil.isMacOS() && tool.get("platform").toLowerCase().equals("macos")){
                externalSubMenu.add(new AbstractAction(tool.get("name")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        log.debug("执行：" + tool.get("start"));
                    }
                });
            }
            if (OsInfoUtil.isMacOSX() && tool.get("platform").toLowerCase().equals("macosx")){
                externalSubMenu.add(new AbstractAction(tool.get("name")) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        log.debug("执行：" + tool.get("start"));
                    }
                });
            }
        }
    }

    private void parseSettings(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(settings_path)));
            String json = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                json += line;
            }
            log.debug(json);
            JSONArray array = JSONObject.parseArray(json);
            for (int i = 0; i < array.size(); i++){
                LinkedHashMap<String, String> tmpMap = new LinkedHashMap<>();
                JSONObject object = array.getJSONObject(i);
                Set<String> jsonSet = object.keySet();
                Iterator<String> iterator = jsonSet.iterator();
                while (iterator.hasNext()){
                    String key = iterator.next();
                    String value = object.getString(key);
                    tmpMap.put(key, value);
                    log.debug(key + ": " + value);
                }
                tools.add(tmpMap);
                log.debug("===============================================");
            }
            log.debug(String.valueOf(array.size()));
            log.debug(tools.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void saveSettings(){
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(settings_path)));
            outputStream.write(JSON.toJSONString(tools, true).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void exec(String start){
        try{
            //创建ProcessBuilder对象
            ProcessBuilder processBuilder = new ProcessBuilder();

            //封装执行的第三方程序(命令)
            ArrayList<String> cmdList = new ArrayList<>();
            for (String part : start.split("\\s+")){
                cmdList.add(part);
            }

            // 设置执行命令及其参数列表
            processBuilder.command(cmdList);
            log.debug(cmdList.toString());

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
            int len = -1;
            while ((len = reader.read(chars)) != -1) {
                String string = new String(chars, 0, len);
                log.debug(string);
            }
            inputStream.close();
            reader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }

}
