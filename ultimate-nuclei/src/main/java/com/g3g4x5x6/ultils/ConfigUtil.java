package com.g3g4x5x6.ultils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
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
}
