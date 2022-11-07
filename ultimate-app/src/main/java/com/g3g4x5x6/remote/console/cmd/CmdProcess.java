package com.g3g4x5x6.remote.console.cmd;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.pty4j.WinSize;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class CmdProcess {
    public CmdProcess() {
    }

    public abstract boolean isRunning();

    public abstract void setWinSize(WinSize var1);

    @NotNull
    public abstract WinSize getWinSize() throws IOException;

    public long pid() {
        return (long) this.getPid();
    }

    public abstract int getPid();

    public byte getEnterKeyCode() {
        return 13;
    }

    public static PtyProcess exec(String[] command) throws IOException {
        return exec(command, (Map) null);
    }

    public static PtyProcess exec(String[] command, Map<String, String> environment) throws IOException {
        return exec(command, environment, (String) null, false, false, (File) null);
    }

    public static PtyProcess exec(String[] command, Map<String, String> environment, String workingDirectory) throws IOException {
        return exec(command, environment, workingDirectory, false, false, (File) null);
    }


    public static PtyProcess exec(String[] command, Map<String, String> environment, String workingDirectory, boolean console) throws IOException {
        return exec(command, environment, workingDirectory, console, false, (File) null);
    }

    public static PtyProcess exec(String[] command, Map<String, String> environment, String workingDirectory, boolean console, boolean cygwin, File logFile) throws IOException {
        PtyProcessBuilder builder = (new PtyProcessBuilder(command)).setEnvironment(environment).setDirectory(workingDirectory).setConsole(console).setCygwin(cygwin).setLogFile(logFile);
        return builder.start();
    }
}
