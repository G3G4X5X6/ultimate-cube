package com.g3g4x5x6.editor.util;

import com.g3g4x5x6.editor.EditorFrame;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


@Slf4j
public class EditorConfig {
    public static Properties properties = loadProperties();

    private EditorConfig() {

    }

    private static Properties loadProperties() {
        // 初始化应用配置
        if (!Files.exists(Path.of(EditorConfig.getPropertiesPath()))) {
            try {
                InputStream nucleiIn = EditorFrame.class.getClassLoader().getResourceAsStream("editor.properties");
                assert nucleiIn != null;
                Files.copy(nucleiIn, Path.of(EditorConfig.getPropertiesPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 加载配置
        Properties properties = new Properties();
        try {
            InputStreamReader inputStream = new InputStreamReader(new FileInputStream(EditorConfig.getPropertiesPath()), StandardCharsets.UTF_8);
            properties.load(inputStream);
        } catch (Exception ignored) {

        }
        return properties;
    }

    public static String getProperty(String key) {
        String[] vars = new String[]{
                "{home}#" + getHomePath(),
                "{workspace}#" + getWorkPath(),
        };
        String value = properties.getProperty(key);
        log.debug(value + ">>>>>>>>>>>>>>");
        for (String var : vars) {
            if (value.contains(var.split("#")[0])) {
                value = value.replace(var.split("#")[0], var.split("#")[1]);
            }
        }
        return value;
    }

    public static String getHomePath() {
        return Path.of(System.getProperties().getProperty("user.home")).toString();
    }

    public static String getWorkPath() {
        String work = Path.of(getHomePath() + "/.ultimate-cube/").toString();
        File file = new File(work);
        if (!file.exists()) {
            if (!file.mkdir()) {
                log.debug("文件夹创建失败：" + work);
            }
        }
        log.debug(work);
        return work;
    }

    public static String getPropertiesPath() {
        File file = new File(Path.of(getWorkPath(), "config").toString());
        if (!file.exists()) {
            if (!file.mkdir()) {
                log.debug("文件夹创建失败：" + Path.of(getWorkPath(), "config"));
            }
        }
        return getWorkPath() + "/config/editor.properties";
    }
}
