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

        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("about.properties"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        log.debug(properties.getProperty("product"));
        product = properties.getProperty("product");
        version = properties.getProperty("version");
        build = properties.getProperty("build");
        author = properties.getProperty("author");
        license = properties.getProperty("license");
        source = properties.getProperty("source");
        init();
    }


    /**
     * About
     * product = UltimateShell
     * version = 0.8.7
     * build = Sat 04 Sep 2021
     * author = G3G4X5X6
     * license = MIT
     * source = https://github.com/G3G4X5X6/ultimateshell
     */
    private void init(){
        String productInfo  = "<html>Product:  " + product + " " + version + "</html>";
        String buildInfo    = "<html>Build on: " + build + "</html>";
        String licenseInfo  = "<html>License:  MIT © 2021 G3G4X5X6</html>";
        String sourceInfo   = "<html>Build on: " + build + "</html>";
        String authorInfo   = "<html>Powered by <a href='https://github.com/G3G4X5X6'>" + author + "</a></html>";
        // 创建一个垂直箱容器
        Box vBox = Box.createVerticalBox();
        vBox.add(new JLabel(productInfo));
        vBox.add(new JLabel(buildInfo));
        vBox.add(new JLabel(licenseInfo));
        vBox.add(new JLabel(sourceInfo));
        vBox.add(new JLabel(authorInfo));
        this.add(vBox);
    }
}
