package com.g3g4x5x6.ui.panels;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
            externalSubMenu.add(new AbstractAction(tool.get("name")) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log.debug("执行：" + tool.get("start"));
                }
            });
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

    public static void main(String[] args) {

    }

}
