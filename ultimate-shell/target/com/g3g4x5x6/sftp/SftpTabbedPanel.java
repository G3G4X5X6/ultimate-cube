package com.g3g4x5x6.sftp;

import javax.swing.*;

public class SftpTabbedPanel extends JTabbedPane {
    private SftpLogPanel logPanel;
    private SftpTaskPanel taskPanel;

    public SftpTabbedPanel(){
        this.setTabPlacement(JTabbedPane.TOP);  // TODO 可配置

        logPanel = new SftpLogPanel();
        taskPanel = new SftpTaskPanel();

        this.addTab("任务列表", taskPanel);
        this.addTab("操作日志", logPanel);
    }

    public SftpLogPanel getLogPanel() {
        return logPanel;
    }

    public SftpTaskPanel getTaskPanel() {
        return taskPanel;
    }
}
