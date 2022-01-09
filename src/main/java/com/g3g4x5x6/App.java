package com.g3g4x5x6;

import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.DbUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

import com.g3g4x5x6.utils.CheckUtil;


@Slf4j
public class App {
    public static MainFrame mainFrame;

    public static void main(String[] args) {
        // 检查程序运行环境
        CheckUtil.checkEnv();
        // 初始化数据库
        DbUtil.createDatabase();
        // 启动主程序
        SwingUtilities.invokeLater(App::createGUI);
    }

    private static void createGUI() {
        // 配置主题皮肤
        initFlatLaf();

        // 程序主窗口
        mainFrame = new MainFrame();
        mainFrame.setTitle("UltimateShell");
        mainFrame.pack();
        mainFrame.setVisible(true);
        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.info("<<<<<<<<<<<<<<<<<<<<<程序启动完成>>>>>>>>>>>>>>>>>>>>>");
        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    private static void initFlatLaf() {
        try {
            if (ConfigUtil.isEnableTheme()) {
                UIManager.setLookAndFeel(ConfigUtil.getThemeClass());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception ex) {
            log.error("Failed to initialize LaF !!!!!!!! \n" + ex.getMessage());
        }
    }
}
