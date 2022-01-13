package com.g3g4x5x6.ui.embed.nuclei;

import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.ui.embed.nuclei.panel.RunningPanel;
import com.g3g4x5x6.ui.embed.nuclei.panel.SettingsPanel;
import com.g3g4x5x6.ui.embed.nuclei.panel.TemplatesPanel;
import com.g3g4x5x6.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

@Slf4j
public class NucleiFrame extends JFrame {
    private JMenu fileMenu = new JMenu("文件");
    private JMenu editMenu = new JMenu("编辑");
    private JMenu searchMenu = new JMenu("搜索");
    private JMenu viewMenu = new JMenu("视图");
    private JMenu encodeMenu = new JMenu("编码");
    private JMenu langMenu = new JMenu("语言");
    private JMenu settingsMenu = new JMenu("设置");
    private JMenu macroMenu = new JMenu("宏");
    private JMenu runMenu = new JMenu("运行");
    private JMenu pluginMenu = new JMenu("插件");
    private JMenu winMenu = new JMenu("窗口");
    private JMenu aboutMenu = new JMenu("关于");

    private JTabbedPane tabbedPane;

    public NucleiFrame(){
        this.setLayout(new BorderLayout());
        this.setTitle("Nuclei");
        this.setSize(new Dimension(1200, 700));
        this.setPreferredSize(new Dimension(1200, 700));
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.png"))).getImage());

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(searchMenu);
        menuBar.add(viewMenu);
        menuBar.add(encodeMenu);
        menuBar.add(langMenu);
        menuBar.add(settingsMenu);
        menuBar.add(macroMenu);
        menuBar.add(runMenu);
        menuBar.add(pluginMenu);
        menuBar.add(winMenu);
        menuBar.add(aboutMenu);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Templates", new TemplatesPanel());
        tabbedPane.addTab("Settings", new SettingsPanel());
        tabbedPane.addTab("Running", new RunningPanel());

        this.setJMenuBar(menuBar);
        this.add(tabbedPane, BorderLayout.CENTER);
    }


    public static void main(String[] args) {
        initFlatLaf();
        NucleiFrame nuclei = new NucleiFrame();
        nuclei.setDefaultCloseOperation(NucleiFrame.EXIT_ON_CLOSE);
        nuclei.setVisible(true);
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
