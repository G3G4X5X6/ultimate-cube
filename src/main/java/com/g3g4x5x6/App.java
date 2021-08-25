package com.g3g4x5x6;

import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.ui.MainFrame;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.*;


public class App {
    static final Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) {
        // TODO 配置日志
        PropertyConfigurator.configure(App.class.getResource("/log4j.properties"));

        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        // 此处处于 事件调度线程
                        createGUI();
                    }
                }
        );
    }

    public static void createGUI() {
        // 此处处于 事件调度线程
        // TODO 配置主题皮肤
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
        } catch( Exception ex ) {
            logger.error("Failed to initialize LaF");
        }

        // TODO 启动主界面
        MainFrame mainFrame = new MainFrame();
        mainFrame.pack();
        mainFrame.setVisible(true);
        logger.info("主线程启动完成");
    }
}
