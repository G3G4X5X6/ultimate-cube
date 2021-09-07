package com.g3g4x5x6.ui.panels.dashboard;

import com.g3g4x5x6.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;


/**
 * TODO CPU, memory, disk, system version, system update,
 */
@Slf4j
public class SysinfoPane extends JPanel {
    private BorderLayout borderLayout = new BorderLayout();

    public SysinfoPane() {
        this.setLayout(borderLayout);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        CommonUtil.generateSystemInfo();
                        JTextArea infoTextArea = new JTextArea();
                        infoTextArea.setEditable(false);
                        infoTextArea.setText(CommonUtil.getSystemInfo());
                        JScrollPane scrollPane = new JScrollPane(infoTextArea);
                        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                        add(scrollPane, BorderLayout.CENTER);
                        Thread.sleep(1000*60*10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
