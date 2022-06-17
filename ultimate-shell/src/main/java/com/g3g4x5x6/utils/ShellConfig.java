package com.g3g4x5x6.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


@Slf4j
public class ShellConfig {
    public static Properties properties = loadProperties();

    private ShellConfig() {

    }

    private static Properties loadProperties() {
        // 初始化应用配置
        if (!Files.exists(Path.of(ShellConfig.getPropertiesPath()))) {
            try {
                InputStream nucleiIn = ShellConfig.class.getClassLoader().getResourceAsStream("shell.properties");
                assert nucleiIn != null;
                Files.copy(nucleiIn, Path.of(ShellConfig.getPropertiesPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 加载配置
        Properties properties = new Properties();
        try {
            InputStreamReader inputStream = new InputStreamReader(new FileInputStream(ShellConfig.getPropertiesPath()), StandardCharsets.UTF_8);
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
        return getWorkPath() + "/config/shell.properties";
    }
}