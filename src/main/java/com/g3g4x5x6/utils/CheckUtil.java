package com.g3g4x5x6.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class CheckUtil {
    private CheckUtil() {
    }

    public static void checkEnv() {
        // 检查程序工作目录
        String workspace = ConfigUtil.getWorkPath();

        // TODO 检查是否启用自定义工作目录


        // 应用配置文件


        // 检查备忘笔记目录
        File note = new File(workspace + "/note");
        if (!note.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + note.getAbsolutePath() + "”，正在创建中......");
            boolean bool = note.mkdir();
        }

        // 检查会话导出目录
        File export = new File(workspace + "/export");
        if (!export.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + export.getAbsolutePath() + "”，正在创建中......");
            export.mkdir();
        }

        // 检查缓存目录
        File temp = new File(workspace + "/temp");
        if (!temp.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + temp.getAbsolutePath() + "”，正在创建中......");
            temp.mkdir();
        }

        // 检查编辑器缓存目录
        File editor = new File(workspace + "/editor");
        if (!editor.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + editor.getAbsolutePath() + "”，正在创建中......");
            editor.mkdir();
        }

        // 检查会话存储目录
        File sshSessions = new File(workspace + "/sessions/ssh/");
        if (!sshSessions.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + sshSessions.getAbsolutePath() + "”，正在创建中......");
            sshSessions.mkdirs();
        }

        // 检查工具目录
        File tools = new File(workspace + "/tools/freerdp");
        if (!tools.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + tools.getAbsolutePath() + "”，正在创建中......");
            tools.mkdirs();
        }
        // 检查RDP运行环境
        File freeRdpDir = new File(ConfigUtil.getWorkPath() + "/freerdp");
        if (!freeRdpDir.exists()) {
            freeRdpDir.mkdir();
        }
        File rdpSessions = new File(workspace + "/sessions/freeRdp/");
        if (!rdpSessions.exists()) {
            log.info(">>>>>>>> 找不到路径 “" + rdpSessions.getAbsolutePath() + "”，正在创建中......");
            rdpSessions.mkdirs();
        }

    }
}
