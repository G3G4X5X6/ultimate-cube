package com.g3g4x5x6.ui.panels.tools;

import com.g3g4x5x6.App;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;


@Slf4j
public class QRTool extends JDialog {

    public QRTool() {
        super(App.mainFrame);
        this.setTitle("QR Code");
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(550, 300));
        this.setLocationRelativeTo(this);

    }

    public static void main(String[] args) {
//        System.out.println(QrCodeUtil.decode(new File("C:\\Users\\Security\\.ultimateshell\\test.jpg")));
    }
}
