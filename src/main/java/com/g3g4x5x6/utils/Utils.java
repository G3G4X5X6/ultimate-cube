package com.g3g4x5x6.utils;

import java.io.File;

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
            note.mkdir();
        }
        // TODO 检查会话导出目录
        File export = new File(workspace + "/export");
        if (!export.exists()) {
            export.mkdir();
        }
        // TODO 检查缓存目录
        File temp = new File(workspace + "/temp");
        if (!temp.exists()) {
            temp.mkdir();
        }

        // TODO 检查工具目录
        File tools = new File(workspace + "/tools");
        if (!tools.exists()) {
            tools.mkdir();
        }

        // TODO 检查编辑器缓存目录
        File editor = new File(workspace + "/editor");
        if (!editor.exists()) {
            editor.mkdir();
        }

        // TODO 检查会话存储目录
        File sessions = new File(workspace + "/sessions/ssh/");
        if (!sessions.exists()) {
            sessions.mkdirs();
        }

        // TODO 检查用户配置

    }
}
