package com.g3g4x5x6.ui.panels.ssh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;

public class MyJSchShellTtyConnector extends MyJSchTtyConnector<ChannelShell> {
    public MyJSchShellTtyConnector() {
    }

    public MyJSchShellTtyConnector(String host, String user, String password) {
        super(host, 22, user, password);
    }

    public MyJSchShellTtyConnector(String host, int port, String user, String password) {
        super(host, port, user, password);
    }

    public MyJSchShellTtyConnector(String host, int port, String username) {
        super(host, port, username);
    }

    public MyJSchShellTtyConnector(String host, int port) {
        super(host, port);
    }

    protected ChannelShell openChannel(Session session) throws JSchException {
        return (ChannelShell)session.openChannel("shell");
    }

    protected void configureChannelShell(ChannelShell channel) {
        String lang = (String)System.getenv().get("LANG");
        channel.setEnv("LANG", lang != null ? lang : "zh_CN.UTF-8");
        channel.setPtyType("xterm");
    }

    protected void setPtySize(ChannelShell channel, int col, int row, int wp, int hp) {
        channel.setPtySize(col, row, wp, hp);
    }

    @Override
    public boolean ready() throws IOException {
        return false;
    }
}
