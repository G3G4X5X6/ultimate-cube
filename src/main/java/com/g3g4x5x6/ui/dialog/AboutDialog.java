package com.g3g4x5x6.ui.dialog;

import com.g3g4x5x6.App;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Properties;


/**
 * 关于弹窗
 */
@Slf4j
public class AboutDialog extends JDialog {
    private BorderLayout borderLayout = new BorderLayout();
    private String product;
    private String version;
    private String build;
    private String author;
    private String license;
    private String source;

    // TODO thanks label



    public AboutDialog() {
        super(App.mainFrame);
        this.setModal(true);
        this.setLayout(borderLayout);
        this.setSize(new Dimension(350, 200));
        this.setLocationRelativeTo(App.mainFrame);
    }
}
