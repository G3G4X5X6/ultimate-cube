package com.g3g4x5x6.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Utils {
    private Utils() {
    }

    public static void checkEnv() {
        // TODO 检查是否启用自定义工作目录


        // TODO 检查程序工作目录
        String workspace = ConfigUtil.getWorkPath();

        // TODO 检查备忘笔记目录
        File note = new File(workspace + "/note");
        if (!note.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + note.getAbsolutePath().toString() + "”，正在创建中......");
            note.mkdir();
        }
        // TODO 检查会话导出目录
        File export = new File(workspace + "/export");
        if (!export.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + export.getAbsolutePath().toString() + "”，正在创建中......");
            export.mkdir();
        }
        // TODO 检查缓存目录
        File temp = new File(workspace + "/temp");
        if (!temp.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + temp.getAbsolutePath().toString() + "”，正在创建中......");
            temp.mkdir();
        }

        // TODO 检查工具目录
        File tools = new File(workspace + "/tools");
        if (!tools.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + tools.getAbsolutePath().toString() + "”，正在创建中......");
            tools.mkdir();
        }

        // TODO 检查编辑器缓存目录
        File editor = new File(workspace + "/editor");
        if (!editor.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + editor.getAbsolutePath().toString() + "”，正在创建中......");
            editor.mkdir();
        }

        // TODO 检查会话存储目录
        File sessions = new File(workspace + "/sessions/ssh/");
        if (!sessions.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + sessions.getAbsolutePath().toString() + "”，正在创建中......");
            sessions.mkdirs();
        }

        // TODO 检查用户配置

    }
}
