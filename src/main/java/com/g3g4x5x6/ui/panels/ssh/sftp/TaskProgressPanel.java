package com.g3g4x5x6.ui.panels.ssh.sftp;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TaskProgressPanel extends JPanel {
    private JProgressBar progressBar;
    private JLabel taskLabel;

    public TaskProgressPanel(String title, int min, int max, String path){
        this.setLayout(new BorderLayout());
        TitledBorder titledBorder = new TitledBorder(title);

        progressBar = new JProgressBar();
        progressBar.setMinimum(min);
        progressBar.setMaximum(max);
        progressBar.setStringPainted(true);

        taskLabel = new JLabel("未设置");
        taskLabel.setText(path);

        this.setBorder(titledBorder);
        this.add(taskLabel, BorderLayout.CENTER);
        this.add(progressBar, BorderLayout.NORTH);

    }

    public void setProgressBarValue(int value){
        progressBar.setValue(value);
    }
}
