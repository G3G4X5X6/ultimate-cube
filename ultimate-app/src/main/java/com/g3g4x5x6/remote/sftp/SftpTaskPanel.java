package com.g3g4x5x6.remote.sftp;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;


@Slf4j
public class SftpTaskPanel extends JScrollPane {

    public SftpTaskPanel() {
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        Box vBox = Box.createVerticalBox();
        this.add(vBox);
    }

    /**
     * 解析参数，创建下载任务
     * @param path 待下载的文件或者目录
     * @param absolutePath 下载文件要保存到本地的目标目录
     */
    public void addTask(String path, String absolutePath) {
        log.debug("下载 [" + path + "] 到 目录 [" + absolutePath + "]");
    }

    private class TaskInfoPanel extends JPanel{
        private String uuid;
        private String name;
        private String type;
        private JProgressBar progressBar;
        private JButton stopBtn;
        private JButton deleteBtn;


        public TaskInfoPanel(){
            this.setLayout(new FlowLayout());

        }
    }
}
