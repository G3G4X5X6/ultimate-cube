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

    public SshTabbedPane(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
        this.id = sessionInfo.getSessionId();
        // 等待进度条
        MainFrame.addWaitProgressBar();


        if (this.sessionInfo.getSshPane() == null)
            this.sessionInfo.initComponent();
        if (sessionInfo.getSshPane() != null)
            this.addTab("SSH", this.sessionInfo.getSshPane());
        if (sessionInfo.getSftpBrowser() != null)
            this.addTab("SFTP", this.sessionInfo.getSftpBrowser());
        if (sessionInfo.getEditorPane() != null)
            this.addTab("Editor", this.sessionInfo.getEditorPane());
        if (sessionInfo.getMonitorPane() != null)
            this.addTab("Monitor", this.sessionInfo.getMonitorPane());
        // TabbedPane
        customComponents();

        // 关闭进度条
        MainFrame.removeWaitProgressBar();
        App.sessionInfos.put(sessionInfo.getSessionId(), sessionInfo);
    }

    private void customComponents() {
        JToolBar trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);
        trailing.add(Box.createHorizontalGlue());
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/commit.svg")));
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/diff.svg")));
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/listFiles.svg")));

        this.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

    public void resetSession() {
        new Thread(() -> {
            // 等待进度条
            MainFrame.addWaitProgressBar();

            sessionInfo.initComponent();
            this.removeAll();
            this.addTab("SSH", this.sessionInfo.getSshPane());
            if (sessionInfo.getSftpBrowser() != null)
                this.addTab("SFTP", this.sessionInfo.getSftpBrowser());
            this.addTab("Editor", this.sessionInfo.getEditorPane());
            this.addTab("Monitor", this.sessionInfo.getMonitorPane());
            // 关闭进度条
            MainFrame.removeWaitProgressBar();
        }).start();
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public String getId() {
        return id;
    }
}
