package com.g3g4x5x6.panel.focus;


import com.g3g4x5x6.remote.ssh.SessionInfo;

import javax.swing.*;
import java.awt.*;

public class FocusPanel extends JPanel {
    private SessionInfo sessionInfo;

    private final int screenWidth;
    private final int screenHeight;

    public FocusPanel() {
        this.setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) screenSize.getWidth();//获得屏幕得宽
        screenHeight = (int) screenSize.getHeight();//获得屏幕得高
    }

    public FocusPanel(SessionInfo sessionInfo) {
        this();
        this.sessionInfo = sessionInfo;
        JSplitPane hSplitPane = new JSplitPane();
        JSplitPane vSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane vSplitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        hSplitPane.setDividerLocation(screenWidth / 2);
        vSplitPane1.setDividerLocation(screenHeight / 2);
        vSplitPane2.setDividerLocation(screenHeight / 2);

        hSplitPane.setLeftComponent(vSplitPane1);
        hSplitPane.setRightComponent(vSplitPane2);

        vSplitPane1.setTopComponent(sessionInfo.getSshPane());

        vSplitPane2.setTopComponent(sessionInfo.getSftpBrowser());
        this.add(hSplitPane, BorderLayout.CENTER);
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }
}
