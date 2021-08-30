package com.g3g4x5x6.ui.panels.console;


import com.g3g4x5x6.ui.MainFrame;
import com.jediterm.pty.PtyProcessTtyConnector;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.UIUtil;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConsolePane extends JPanel {

    private BorderLayout borderLayout = new BorderLayout();

    public ConsolePane() {
        this.setLayout(borderLayout);

        JediTermWidget terminalPanel = new JediTermWidget(new DefaultSettingsProvider());
        terminalPanel.setTtyConnector(createTtyConnector());
        terminalPanel.start();

        this.add(terminalPanel, BorderLayout.CENTER);
    }


    private static @NotNull TtyConnector createTtyConnector() {
        try {
            Map<String, String> envs = System.getenv();
            String[] command;
            if (UIUtil.isWindows) {
                command = new String[]{"cmd.exe"};
            } else {
                command = new String[]{"/bin/bash", "--login"};
                envs = new HashMap<>(System.getenv());
                envs.put("TERM", "xterm-256color");
            }
            PtyProcess process = new PtyProcessBuilder().setDirectory(MainFrame.getWorkDir()).setCommand(command).setEnvironment(envs).start();

            return new PtyProcessTtyConnector(process, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
