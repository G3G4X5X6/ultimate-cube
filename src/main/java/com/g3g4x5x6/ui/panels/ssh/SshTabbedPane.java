package com.g3g4x5x6.ui.panels.ssh;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.App;
import com.g3g4x5x6.ui.MainFrame;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT;


@Slf4j
public class SshTabbedPane extends JTabbedPane {
    private final String id;
    private final SessionInfo sessionInfo;

    public SshTabbedPane(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
        this.id = sessionInfo.getSessionId();
        // 等待进度条
        MainFrame.addWaitProgressBar();

        this.sessionInfo.initComponent();
        this.addTab("SSH", this.sessionInfo.getSshPane());
        this.addTab("SFTP", this.sessionInfo.getSftpBrowser());
        this.addTab("Editor", this.sessionInfo.getEditorPane());
        this.addTab("Monitor", this.sessionInfo.getMonitorPane());
        customComponents();

        // 关闭进度条
        MainFrame.removeWaitProgressBar();
        App.sessionInfos.put(sessionInfo.getSessionId(), sessionInfo);
    }

    private void customComponents() {
        JToolBar trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/buildLoadChanges.svg")));
        trailing.add(Box.createHorizontalGlue());
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/commit.svg")));
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/diff.svg")));
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/listFiles.svg")));

        this.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

    public void resetSession() {
        // TODO 重连后终端大小不对，需要拉伸窗口重新触发调整终端大小; 或者从 SFTP 窗口跳转回来也好
        // 等待进度条
        MainFrame.addWaitProgressBar();

        new Thread(() -> {
            sessionInfo.initComponent();
        }).start();
        // 关闭进度条
        MainFrame.removeWaitProgressBar();
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public String getId() {
        return id;
    }
}
