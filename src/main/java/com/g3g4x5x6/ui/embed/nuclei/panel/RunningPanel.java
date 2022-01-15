package com.g3g4x5x6.ui.embed.nuclei.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.embed.nuclei.panel.connector.ProcessTtyConnector;
import com.g3g4x5x6.ui.panels.console.CmdSettingsProvider;
import com.g3g4x5x6.utils.ConfigUtil;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.UIUtil;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.pty4j.WinSize;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class RunningPanel extends JPanel {
    public static String nucleiPath = ConfigUtil.getWorkPath() + "/tools/xpack_tools/nuclei/";
    public static NucleiProcessTtyConnector ttyConnector;

    public RunningPanel() {
        this.setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        this.add(toolBar, BorderLayout.NORTH);

        JButton openBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/menu-open.svg"));
        toolBar.add(openBtn);
        JButton refreshBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/refresh.svg"));
        toolBar.add(refreshBtn);
        toolBar.add(Box.createGlue());
        refreshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTerminal();
            }
        });
        refreshTerminal();
    }

    private void refreshTerminal(){
        this.add(createTerminal(), BorderLayout.CENTER);
    }

    private JediTermWidget createTerminal() {
        CmdSettingsProvider cmdSettingsProvider = new CmdSettingsProvider();
        cmdSettingsProvider.setDefaultStyle(ConfigUtil.getTextStyle());
        JediTermWidget terminalPanel = new JediTermWidget(cmdSettingsProvider);
        terminalPanel.setTtyConnector(createTtyConnector());
        terminalPanel.start();
        ttyConnector = (NucleiProcessTtyConnector) terminalPanel.getTtyConnector();
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

            return new NucleiProcessTtyConnector(process, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static class NucleiProcessTtyConnector extends ProcessTtyConnector {

        private final PtyProcess myProcess;

        public NucleiProcessTtyConnector(@NotNull PtyProcess process, @NotNull Charset charset) {
            super(process, charset);
            this.myProcess = process;
        }

        public void resize(@NotNull Dimension termWinSize) {
            if (this.isConnected()) {
                this.myProcess.setWinSize(new WinSize(termWinSize.width, termWinSize.height));
            }

        }

        public boolean isConnected() {
            return this.myProcess.isRunning();
        }

        public String getName() {
            return "Nuclei-Console";
        }
    }

}
