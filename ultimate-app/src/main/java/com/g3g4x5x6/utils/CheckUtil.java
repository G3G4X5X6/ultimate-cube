package com.g3g4x5x6.utils;

import com.g3g4x5x6.AppConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class CheckUtil {
    private CheckUtil() {
    }

    public static void checkEnv() {
        // 检查程序工作目录
        String workspace = AppConfig.getWorkPath();
        String[] pathArray = new String[]{
                workspace + "/bin",                         // 检查第三方可执行文件目录
                workspace + "/note",                        // 检查备忘笔记目录
                workspace + "/export",                      // 检查会话导出目录
                workspace + "/temp",                        // 检查缓存目录
                workspace + "/logs",                        // 检查日志目录
                workspace + "/editor",                      // 检查编辑器缓存目录
                workspace + "/sessions/ssh/",               // 检查SSH会话存储目录
                workspace + "/sessions/freeRdp/",           // 检查RDP会话存储目录
                workspace + "/tools/external_tools",        // 检查外部集成工具目录，重点在于 `settings.json`
        };
        for (String path : pathArray) {
            File temp = new File(path);
            if (!temp.exists()) {
                if (!temp.mkdirs()) {
                    log.debug("目录创建失败：" + path);
                }
            }
        }
    }
}
