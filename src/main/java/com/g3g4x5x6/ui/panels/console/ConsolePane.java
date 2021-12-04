package com.g3g4x5x6.ui.panels.console;


import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.g3g4x5x6.ui.MainFrame;
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
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class ConsolePane extends JPanel {

    private BorderLayout borderLayout = new BorderLayout();
    private JToolBar toolBar;

    public ConsolePane() {
        this.setLayout(borderLayout);
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        FlatButton freshBtn = new FlatButton();
        freshBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        freshBtn.setToolTipText("重置本地终端");
        freshBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/refresh.svg"));
        freshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        log.debug("刷新本地终端");
                    }
                }).start();
            }
        });
        toolBar.add(freshBtn);

        CmdSettingsProvider cmdSettingsProvider = new CmdSettingsProvider();
        cmdSettingsProvider.setDefaultStyle(ConfigUtil.getTextStyle());
        JediTermWidget terminalPanel = new JediTermWidget(cmdSettingsProvider);
        terminalPanel.setTtyConnector(createTtyConnector());
        terminalPanel.start();

        this.add(terminalPanel, BorderLayout.CENTER);
        this.add(toolBar, BorderLayout.SOUTH);
        log.info("启动本地终端");
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
            PtyProcess process = new PtyProcessBuilder().setDirectory(ConfigUtil.getWorkPath()).setCommand(command).setEnvironment(envs).start();

            return new PtyProcessTtyConnector(process, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
