package com.g3g4x5x6.ui.embed.nuclei.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.panels.console.CmdSettingsProvider;
import com.g3g4x5x6.utils.ConfigUtil;
import com.jediterm.pty.PtyProcessTtyConnector;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.UIUtil;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class RunningPanel extends JPanel {
    public static String nucleiPath = ConfigUtil.getWorkPath() + "/tools/xpack_tools/nuclei/";
    private JToolBar toolBar = new JToolBar();
    private JButton openBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/menu-open.svg"));
    private JButton refreshBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/refresh.svg"));

    public RunningPanel() {
        this.setLayout(new BorderLayout());
        this.add(toolBar, BorderLayout.NORTH);

        toolBar.setFloatable(false);
        toolBar.add(openBtn);
        toolBar.add(refreshBtn);
        toolBar.add(Box.createGlue());

        this.add(createTerminal(), BorderLayout.CENTER);
    }

    private JediTermWidget createTerminal() {
        CmdSettingsProvider cmdSettingsProvider = new CmdSettingsProvider();
        cmdSettingsProvider.setDefaultStyle(ConfigUtil.getTextStyle());
        JediTermWidget terminalPanel = new JediTermWidget(cmdSettingsProvider);
        terminalPanel.setTtyConnector(createTtyConnector());
        terminalPanel.start();
        return terminalPanel;
    }

    private static @NotNull TtyConnector createTtyConnector() {
        try {
            Map<String, String> envs = System.getenv();
            log.debug(envs.toString());
            String[] command;
            if (UIUtil.isWindows) {
                String path = envs.get("Path") + ";" + RunningPanel.nucleiPath;
                envs = new HashMap<>(System.getenv());
                envs.put("Path", path);
                command = new String[]{"cmd.exe"};
            } else {
                command = new String[]{"/bin/bash", "--login"};
                envs = new HashMap<>(System.getenv());
                envs.put("TERM", "xterm-256color");
            }
            PtyProcess process = new PtyProcessBuilder().setDirectory(ConfigUtil.getWorkPath()).setCommand(command).setEnvironment(envs).start();

            return new PtyProcessTtyConnector(process, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
