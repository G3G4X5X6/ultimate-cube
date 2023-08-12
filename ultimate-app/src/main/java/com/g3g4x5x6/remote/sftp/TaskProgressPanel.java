package com.g3g4x5x6.remote.sftp;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.g3g4x5x6.exception.UserStopException;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TaskProgressPanel extends JPanel {
    private final JProgressBar progressBar;
    private final JLabel taskLabel;
    private final JButton fileCount;

    public TaskProgressPanel(String title, int min, int max, String path) {
        this.setLayout(new BorderLayout());
        TitledBorder titledBorder = new TitledBorder(title);

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);

        // 1
        progressBar = new JProgressBar();
        progressBar.setMinimum(min);
        progressBar.setMaximum(max);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        // 2
        JPanel filePanel = new JPanel(flowLayout);

        taskLabel = new JLabel("未设置");
        taskLabel.setText(path);
        filePanel.add(taskLabel);

        // 3
        JPanel statusPane = new JPanel(flowLayout);

        fileCount = new JButton();
        fileCount.setSelected(true);
        fileCount.setEnabled(false);
        fileCount.setText("剩余文件数：UNKNOWN");

        FlatButton cancelBtn = new FlatButton();
        cancelBtn.setText("取消任务");
        cancelBtn.setIcon(new FlatSVGIcon("icons/cancel.svg"));
        cancelBtn.setToolTipText("取消任务");
        cancelBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelBtn.setText("已取消");
                cancelBtn.setEnabled(false);

                throw new UserStopException("用户取消任务");

            }
        });

        statusPane.add(cancelBtn);
        statusPane.add(fileCount);

        this.setBorder(titledBorder);
        this.add(filePanel, BorderLayout.EAST);
        this.add(progressBar, BorderLayout.NORTH);
        this.add(statusPane, BorderLayout.WEST);

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
        fileCount.setText("剩余文件数：" + count);
    }

    public void setFileCount(String countLabel) {
        fileCount.setText(countLabel);
    }
}
