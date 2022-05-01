package com.g3g4x5x6.sftp;

import javax.swing.*;

public class SftpTabbedPanel extends JTabbedPane {
    private final SftpLogPanel logPanel;
    private final JScrollPane taskPanel;
    private Box vBox = Box.createVerticalBox();

    public SftpTabbedPanel(){
        this.setTabPlacement(JTabbedPane.TOP);  // TODO 可配置


        taskPanel = new JScrollPane(vBox);
        taskPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        taskPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        logPanel = new SftpLogPanel();

        this.addTab("任务列表", taskPanel);
        this.addTab("操作日志", logPanel);
    }

    public SftpLogPanel getLogPanel() {
        return logPanel;
    }

    public JScrollPane getTaskPanel() {
        return taskPanel;
    }

    public void addTask(TaskProgressPanel taskPanel){
        vBox.add(taskPanel);
    }
}
