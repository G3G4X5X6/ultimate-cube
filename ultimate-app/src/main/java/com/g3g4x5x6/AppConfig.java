package com.g3g4x5x6;

import com.g3g4x5x6.utils.DialogUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Slf4j
public class AppConfig {
    private AppConfig() {

    }

    public static String getBinPath() {
        String bin = Path.of(getHomePath() + "/.ultimate-cube/bin").toString();
        File file = new File(bin);
        if (!file.exists()) {
            if (!file.mkdir()) {
                log.debug("文件夹创建失败：" + bin);
            }
        }
        return bin;
    }

    /**
     * 获取程序安装路径（获取当前类所在的JAR文件路径）
     */
    public static String getInstallPath() {
        String path;
        try {
            path = AppConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            Path correctPath = Paths.get(new File(path).toURI());

            path = correctPath.getParent().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        log.debug("Install path: {}", path);
        return path;
    }

    public static String getSessionPath() {
        String bin = Path.of(getHomePath() + "/.ultimate-cube/sessions").toString();
        File file = new File(bin);
        if (!file.exists()) {
            if (!file.mkdir()) {
                log.debug("文件夹创建失败：" + bin);
            }
        }
//        log.debug(session);
        return bin;
    }

    public static String getNotePath() {
        String note = Path.of(getHomePath() + "/.ultimate-cube/note").toString();
        File file = new File(note);
        if (!file.exists()) {
            if (!file.mkdir()) {
                log.debug("文件夹创建失败：" + note);
            }
        }
        return note;
    }

    public static String getWorkPath() {
        String work = Path.of(getHomePath() + "/.ultimate-cube/").toString();
        File file = new File(work);
        if (!file.exists()) {
            if (!file.mkdir()) {
                log.debug("文件夹创建失败：" + work);
            }
        }
        return work;
    }

    public static String getTempPath() {
        String temp = Path.of(getHomePath() + "/.ultimate-cube/temp").toString();
        File file = new File(temp);
        if (!file.exists()) {
            if (!file.mkdir()) {
                log.debug("文件夹创建失败：" + temp);
            }
        }
        return temp;
    }

    public static String getHomePath() {
        return Path.of(System.getProperties().getProperty("user.home")).toString();
    }

    public static String getPropertiesPath() {
        return Path.of(getWorkPath(), "/application.properties").toString();
    }

    public static String getProperty(String key) {
        String[] vars = new String[]{"{home}#" + getHomePath(), "{workspace}#" + getWorkPath(),};
        String value = App.properties.getProperty(key);
        for (String var : vars) {
            if (value.contains(var.split("#")[0])) {
                value = value.replace(var.split("#")[0], var.split("#")[1]);
            }
        }
        return value;
    }

    public static String getProperty(String key, String fill) {
        String value = getProperty(key);
        if (value.isEmpty() || value.isBlank()) return fill;
        return value;
    }

    public static void setProperty(String key, String value) {
        App.properties.setProperty(key, value);
    }

    public static void saveSettingsProperties() {
        try {
            StringBuilder settingsText = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(getPropertiesPath()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#") && !line.isBlank()) {
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
