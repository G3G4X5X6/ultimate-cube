package com.g3g4x5x6;

import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.ui.MainFrame;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class App {

    public static void main(String[] args) {
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
            log.error("Failed to initialize LaF");
        }

        // TODO 启动主界面
        MainFrame mainFrame = new MainFrame();
        mainFrame.pack();
        mainFrame.setVisible(true);
        log.info("主线程启动完成");
    }
}
