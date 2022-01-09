package com.g3g4x5x6;

import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.DbUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

import com.g3g4x5x6.utils.Utils;


@Slf4j
public class App {
    public static MainFrame mainFrame;

    public static void main(String[] args) {
        // 检查程序运行环境
        Utils.checkEnv();
        // 初始化数据库
        DbUtil.createDatabase();
        // 启动主程序
        SwingUtilities.invokeLater(App::createGUI);
    }

    private static void createGUI() {
        // 配置主题皮肤
        initFlatLaf();

        // 主窗口
        mainFrame = new MainFrame();
        mainFrame.setTitle("UltimateShell");
        mainFrame.pack();
        mainFrame.setVisible(true);
        log.info(">>>>>>>> 程序启动完成！！！");
    }

    /**
     * Class themeClass = App.class.getClassLoader().loadClass(ConfigUtil.getThemeClass());
     * UIManager.setLookAndFeel((LookAndFeel) themeClass.getConstructor().newInstance());
     */
    private static void initFlatLaf() {
        try {
            if (ConfigUtil.isEnableTheme()) {
                UIManager.setLookAndFeel(ConfigUtil.getThemeClass());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            log.info(">>>>>>>> FlatLaf主题加载成功......");
        } catch (Exception ex) {
            log.error("Failed to initialize LaF !!!!!!!! ");
        }
    }
}
