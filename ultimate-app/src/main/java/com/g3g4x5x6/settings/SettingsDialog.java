package com.g3g4x5x6.settings;

import com.g3g4x5x6.App;
import com.g3g4x5x6.AppConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_MINIMUM_TAB_WIDTH;
import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TAB_ALIGNMENT;


@Slf4j
public class SettingsDialog extends JDialog {
    private final BasicPanel basicPanel = new BasicPanel();
    private final BehaviorPanel behaviorPanel = new BehaviorPanel();
    private final TerminalPanel terminalPanel = new TerminalPanel();

    public SettingsDialog() {
        super(App.mainFrame);
        this.setTitle("全局配置");
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(800, 500));
        this.setSize(new Dimension(800, 500));
        this.setModal(true);
        this.setLocationRelativeTo(App.mainFrame);

        initTabbedPane();

    }

    private void initTabbedPane() {

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setTabPlacement(SwingConstants.LEFT);
        initTabAlignment(tabbedPane);

        // TODO 保存、取消
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        JPanel southPane = new JPanel();
        JButton reloadBtn = new JButton("重新加载");
        JButton saveBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        reloadBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("重新加载配置");
            }
        });
        saveBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("保存全局设置");
                // 保存到列表
                basicPanel.save();
                behaviorPanel.save();
                terminalPanel.save();

                // 保存到文件
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                AppConfig.saveSettingsProperties();
            }
        });

        cancelBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("关闭配置窗口");
                dispose();
            }
        });

        southPane.add(Box.createGlue());
        southPane.add(reloadBtn);
        southPane.add(saveBtn);
        southPane.add(cancelBtn);
        southPane.add(Box.createGlue());
        southPane.setLayout(flowLayout);

        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(southPane, BorderLayout.SOUTH);
    }

    private void initTabAlignment(JTabbedPane tabbedPane) {
        boolean vertical = (tabbedPane.getTabPlacement() == JTabbedPane.LEFT || tabbedPane.getTabPlacement() == JTabbedPane.RIGHT);
        tabbedPane.putClientProperty(TABBED_PANE_TAB_ALIGNMENT, SwingConstants.TRAILING);
        if (!vertical)
            tabbedPane.putClientProperty(TABBED_PANE_MINIMUM_TAB_WIDTH, 80);
        tabbedPane.addTab("基本设置", basicPanel);
        if (vertical) {
            tabbedPane.addTab("行为设置", behaviorPanel);
            tabbedPane.addTab("终端设置", terminalPanel);
            tabbedPane.addTab("运行环境", null);
        }
    }
}
