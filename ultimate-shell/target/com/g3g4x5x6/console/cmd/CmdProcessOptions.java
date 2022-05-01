package com.g3g4x5x6.console.cmd;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CmdProcessOptions {
    private final String[] myCommand;
    private final Map<String, String> myEnvironment;
    private final String myDirectory;
    private final boolean myRedirectErrorStream;
    private final Integer myInitialColumns;
    private final Integer myInitialRows;
    private final boolean myWindowsAnsiColorEnabled;
    private final boolean myUnixOpenTtyToPreserveOutputAfterTermination;

    CmdProcessOptions(@NotNull String[] command, @Nullable Map<String, String> environment, @Nullable String directory, boolean redirectErrorStream, @Nullable Integer initialColumns, @Nullable Integer initialRows, boolean windowsAnsiColorEnabled, boolean unixOpenTtyToPreserveOutputAfterTermination) {
        this.myCommand = command;
        this.myEnvironment = environment;
        this.myDirectory = directory;
        this.myRedirectErrorStream = redirectErrorStream;
        this.myInitialColumns = initialColumns;
        this.myInitialRows = initialRows;
        this.myWindowsAnsiColorEnabled = windowsAnsiColorEnabled;
        this.myUnixOpenTtyToPreserveOutputAfterTermination = unixOpenTtyToPreserveOutputAfterTermination;
    }

    @NotNull
    public String[] getCommand() {
        return this.myCommand;
    }

    @Nullable
    public Map<String, String> getEnvironment() {
        return this.myEnvironment;
    }

    @Nullable
    public String getDirectory() {
        return this.myDirectory;
    }

    public boolean isRedirectErrorStream() {
        return this.myRedirectErrorStream;
    }

    @Nullable
    public Integer getInitialColumns() {
        return this.myInitialColumns;
    }

    @Nullable
    public Integer getInitialRows() {
        return this.myInitialRows;
    }

    public boolean isWindowsAnsiColorEnabled() {
        return this.myWindowsAnsiColorEnabled;
    }

    public boolean isUnixOpenTtyToPreserveOutputAfterTermination() {
        return this.myUnixOpenTtyToPreserveOutputAfterTermination;
    }
}
