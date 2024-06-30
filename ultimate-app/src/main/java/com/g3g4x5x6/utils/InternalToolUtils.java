package com.g3g4x5x6.utils;

import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.MainFrame;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * 内置工具调用工具类
 */
@Slf4j
public class InternalToolUtils {
    private static final String executablePath = MainFrame.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private static String x11ExecuteFileWinPath = executablePath + "/VcXsrv/" + "vcxsrv.exe";

    static {
        File file = new File(x11ExecuteFileWinPath);
        if (!file.exists()) {
            x11ExecuteFileWinPath = Path.of(AppConfig.getProperty("vcxsrv.path"), "vcxsrv", "vcxsrv.exe").toString();
        }
    }

    public static Process startX11Process() {
        ArrayList<String> cmd = new ArrayList<>();
        cmd.add(x11ExecuteFileWinPath);
        cmd.add("-multiwindow");    // Run the server in multiwindow mode.  Not to be used together with -rootless or -fullscreen.
        cmd.add("-clipboard");      // -[no]clipboard        Enable [disable] the clipboard integration. Default is enabled.
        cmd.add("-ac");             // disable access control restrictions

        return execCmd(cmd);
    }

    private static Process execCmd(ArrayList<String> cmd) {
        Process process = null;
        try {
            // 创建 ProcessBuilder 对象并设置命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(cmd);

            // 设置工作目录（可选）
            processBuilder.directory(new File(executablePath));

            // 启动进程
            process = processBuilder.start();

            log.debug("process started.");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return process;
    }

    public static void destroyProcess(Process process) {
        if (process != null) {
            process.destroy();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Process process = startX11Process();
        Thread.sleep(10000);
        destroyProcess(process);
    }
}
