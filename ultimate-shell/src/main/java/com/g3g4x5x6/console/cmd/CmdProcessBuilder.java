package com.g3g4x5x6.console.cmd;

import com.sun.jna.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CmdProcessBuilder {
    private String[] myCommand;
    private Map<String, String> myEnvironment;
    private String myDirectory;
    private boolean myConsole;
    private boolean myCygwin;
    private File myLogFile;
    private boolean myRedirectErrorStream = false;
    private Integer myInitialColumns;
    private Integer myInitialRows;
    private boolean myWindowsAnsiColorEnabled = false;
    private boolean myUnixOpenTtyToPreserveOutputAfterTermination = false;

    public CmdProcessBuilder() {
    }

    public CmdProcessBuilder(@NotNull String[] command) {
        this.myCommand = command;
    }

    @NotNull
    public CmdProcessBuilder setCommand(@NotNull String[] command) {
        this.myCommand = command;
        return this;
    }

    @NotNull
    public CmdProcessBuilder setEnvironment(@Nullable Map<String, String> environment) {
        this.myEnvironment = environment;
        return this;
    }

    @NotNull
    public CmdProcessBuilder setDirectory(String directory) {
        this.myDirectory = directory;
        return this;
    }

    @NotNull
    public CmdProcessBuilder setConsole(boolean console) {
        this.myConsole = console;
        return this;
    }

    @NotNull
    public CmdProcessBuilder setCygwin(boolean cygwin) {
        this.myCygwin = cygwin;
        return this;
    }

    @NotNull
    public CmdProcessBuilder setLogFile(@Nullable File logFile) {
        this.myLogFile = logFile;
        return this;
    }

    @NotNull
    public CmdProcessBuilder setRedirectErrorStream(boolean redirectErrorStream) {
        this.myRedirectErrorStream = redirectErrorStream;
        return this;
    }

    @NotNull
    public CmdProcessBuilder setInitialColumns(@Nullable Integer initialColumns) {
        this.myInitialColumns = initialColumns;
        return this;
    }

    @NotNull
    public CmdProcessBuilder setInitialRows(@Nullable Integer initialRows) {
        this.myInitialRows = initialRows;
        return this;
    }

    @NotNull
    public CmdProcessBuilder setWindowsAnsiColorEnabled(boolean windowsAnsiColorEnabled) {
        this.myWindowsAnsiColorEnabled = windowsAnsiColorEnabled;
        return this;
    }

    @NotNull
    public CmdProcessBuilder setUnixOpenTtyToPreserveOutputAfterTermination(boolean unixOpenTtyToPreserveOutputAfterTermination) {
        this.myUnixOpenTtyToPreserveOutputAfterTermination = unixOpenTtyToPreserveOutputAfterTermination;
        return this;
    }

    @NotNull
    public Process start() throws IOException {
        if (this.myEnvironment == null) {
            this.myEnvironment = System.getenv();
        }
        if (Platform.isWindows()) {
            return new ProcessBuilder(this.myCommand).start();
        } else {
            return null;
        }
    }
}
