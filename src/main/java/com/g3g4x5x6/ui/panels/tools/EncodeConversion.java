package com.g3g4x5x6.ui.panels.tools;

import com.g3g4x5x6.App;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;


@Slf4j
public class EncodeConversion extends JDialog {

    public EncodeConversion(){
        super(App.mainFrame);
        this.setPreferredSize(new Dimension(500, 300));
        this.setSize(new Dimension(500, 300));
        this.setLocationRelativeTo(null);
        this.setModal(false);
        this.setTitle("文件编码转换工具");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Test"));

        this.add(panel);
    }
}
