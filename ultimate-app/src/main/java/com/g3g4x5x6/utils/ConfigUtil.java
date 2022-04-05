package com.g3g4x5x6.utils;

import com.g3g4x5x6.App;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class ConfigUtil {
    private ConfigUtil() {

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

    public static String getThemePath() {
        return Path.of(getWorkPath() + "/config/theme/").toString();
    }

    public static String getHomePath() {
        return Path.of(System.getProperties().getProperty("user.home")).toString();
    }

    public static String getPropertiesPath() {
        return getWorkPath() + "/application.properties";
    }

    public static String getProperty(String key) {
        String[] vars = new String[]{
                "{home}#" + getHomePath(),
                "{workspace}#" + getWorkPath(),
        };
        String value = App.properties.getProperty(key);
        for (String var : vars) {
            if (value.contains(var.split("#")[0])) {
                value = value.replace(var.split("#")[0], var.split("#")[1]);
            }
        }
        return value;
    }

    public static void saveSettingsProperties() {
        try {
            StringBuilder settingsText = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(getPropertiesPath()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#") && !line.strip().equals("")) {
                    String key = line.strip().split("=")[0];
                    line = key + "=" + (App.properties.getProperty(key) != null ? App.properties.getProperty(key) : line.strip().split("=")[1]);
                }
                settingsText.append(line).append("\n");
            }
            Files.write(Path.of(getPropertiesPath()), settingsText.toString().getBytes(StandardCharsets.UTF_8));
            DialogUtil.info("保存配置成功!");
        } catch (Exception e) {
            DialogUtil.error("保存配置失败：" + e.getMessage());
        }
    }
}
