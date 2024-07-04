package com.g3g4x5x6.utils;

import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.MainFrame;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 内置工具调用工具类
 */
@Slf4j
public class InternalToolUtils {
    private static final String executablePath = MainFrame.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private static String x11ExecuteFileWinPath = executablePath + "/VcXsrv/" + "vcxsrv.exe";
    public static int DISPLAY = 0;

    static {
        File x11File = new File(x11ExecuteFileWinPath);
        if (!x11File.exists()) {
            x11ExecuteFileWinPath = Path.of(AppConfig.getProperty("vcxsrv.path"), "VcXsrv", "vcxsrv.exe").toString();
        }
    }

    public static Process startX11Process() {
        Process process;
        while (true) {
            ArrayList<String> cmd = new ArrayList<>();
            cmd.add(x11ExecuteFileWinPath);
            cmd.add(":" + DISPLAY);

            cmd.addAll(Arrays.asList(AppConfig.getProperty("vcxsrv.args").split("\\s+")));
            log.debug(String.join(" ", cmd));

            process = execCmd(cmd);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (process != null && process.isAlive()) {
                break;
            }
            DISPLAY += 1;
        }
        return process;
    }

    private static Process execCmd(ArrayList<String> cmd) {
        Process process = null;
        try {
            // 创建 ProcessBuilder 对象并设置命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(cmd);

            // 设置工作目录（可选）
            processBuilder.directory(new File(executablePath).getParentFile());

            // 启动进程
            process = processBuilder.start();

            // 添加关闭钩子
            Process finalProcess = process;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (finalProcess.isAlive()) {
                    finalProcess.destroy();
                    try {
                        finalProcess.waitFor(5, TimeUnit.SECONDS); // 等待进程结束
                        if (finalProcess.isAlive()) {
                            finalProcess.destroyForcibly(); // 强制杀死进程
                        }
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                    System.out.println("External process terminated.");
                }
            }));

            log.debug("process started.");
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        return process;
    }

    public static void destroyProcess(Process process) {
        if (process != null) {
            process.destroy();
        } else {
            log.debug("process is null.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Process process = startX11Process();
        Thread.sleep(10000);
        destroyProcess(process);
    }
}
