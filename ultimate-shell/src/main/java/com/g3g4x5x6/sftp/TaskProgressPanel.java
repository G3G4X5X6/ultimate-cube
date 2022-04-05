package com.g3g4x5x6.sftp;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TaskProgressPanel extends JPanel {
    private JProgressBar progressBar;
    private JLabel taskLabel;
    private JToolBar statusBar;
    private JButton fileCount;

    public TaskProgressPanel(String title, int min, int max, String path) {
        this.setLayout(new BorderLayout());
        TitledBorder titledBorder = new TitledBorder(title);

        progressBar = new JProgressBar();
        progressBar.setMinimum(min);
        progressBar.setMaximum(max);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskLabel = new JLabel("未设置");
        taskLabel.setText(path);

        statusBar = new JToolBar();
        statusBar.setFloatable(false);
        fileCount = new JButton();
        fileCount.setSelected(true);
        fileCount.setText("文件数：UNKNOWN");
        statusBar.add(fileCount);

        this.setBorder(titledBorder);
        this.add(taskLabel, BorderLayout.CENTER);
        this.add(progressBar, BorderLayout.NORTH);
        this.add(statusBar, BorderLayout.SOUTH);

    }

    public void setMax(int max) {
        progressBar.setMaximum(max);
    }

    public void setMin(int min) {
        progressBar.setMinimum(min);
    }

    public void setProgressBarValue(int value) {
        progressBar.setValue(value);
    }

    public void setTaskLabel(String path) {
        taskLabel.setText(path);
    }

    public void setFileCount(int count) {
        fileCount.setText("文件数：" + count);
    }

    public void setFileCount(String countLabel) {
        fileCount.setText(countLabel);
    }
}
