package com.g3g4x5x6.remote.ssh.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TaskProgressPanel extends JPanel {
    private final JProgressBar progressBar;
    private final JLabel taskLabel;
    private final JButton fileCount;
    @Getter
    private boolean terminate = false;

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
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelBtn.setText("用户已取消任务");
                cancelBtn.setEnabled(false);
                setTerminate(true);

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

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
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
