package com.g3g4x5x6.panel.settings;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.g3g4x5x6.App;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Objects;


@Slf4j
public class BasicPanel extends JPanel implements SettingsInterface {
    private FlowLayout leftFlowLayout = new FlowLayout();
    private Box vBox = Box.createVerticalBox();
    private JPanel panel = new JPanel();
    private JCheckBox themeEnableBtn;
    private JComboBox<String> themeClass;
    private LinkedList<String> themeClassList = new LinkedList<>();

    public BasicPanel() {
        leftFlowLayout.setAlignment(FlowLayout.LEFT);

        panel.setLayout(new BorderLayout());
        panel.add(vBox);
        panel.setBorder(null);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        initPanel();

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void initPanel() {
        /**
         * 主题配置
         */
        JPanel themePanel = new JPanel();
        themePanel.setLayout(leftFlowLayout);
        themeEnableBtn = new JCheckBox("是否启用主题");
        if (App.properties.getProperty("app.theme.enable").equalsIgnoreCase("false")) {
            themeEnableBtn.setSelected(false);
        } else {
            themeEnableBtn.setSelected(true);
        }
        themeEnableBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themeClass.setEnabled(themeEnableBtn.isSelected());
                if (!themeEnableBtn.isSelected()) {
                    try {
                        UIManager.setLookAndFeel(new FlatLightLaf());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        log.error("Failed to initialize LaF");
                    }
                    // update all components
                    FlatLaf.updateUI();
                    FlatAnimatedLafChange.hideSnapshotWithAnimation();
                } else {
                    refreshTheme();
                }
            }
        });
        themePanel.add(themeEnableBtn);

        themeClass = new JComboBox<>();
        themeClass.setEnabled(Boolean.parseBoolean(App.properties.getProperty("app.theme.enable")));
        themeClass.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug(Objects.requireNonNull(themeClass.getSelectedItem()).toString());
                // 主题预览效果
                if (themeEnableBtn.isSelected())
                    refreshTheme();
            }
        });
        themePanel.add(themeClass);
        initThemeList();
        vBox.add(themePanel);

        /**
         * what?
         */
        JPanel whatPanel = new JPanel();
        whatPanel.setLayout(leftFlowLayout);
        // TODO 下一个配置项
        vBox.add(whatPanel);
    }


    private void initThemeList() {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("ThemeClass.txt"))));
        for (Object line : reader.lines().toArray()) {
            String theme = ((String) line).strip();
            themeClassList.add(theme);
            themeClass.addItem(theme.replace("com.formdev.flatlaf.intellijthemes.", ""));
            if (App.properties.getProperty("app.theme.class").equalsIgnoreCase(theme)) {
                themeClass.setSelectedIndex(themeClass.getItemCount() - 1);
            }
        }
        log.debug(themeClassList.toString());
    }

    private void refreshTheme() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.intellijthemes." + themeClass.getSelectedItem());
        } catch (Exception ex) {
            log.error("Failed to initialize LaF");
        }
        // update all components
        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    @Override
    public void save() {
        App.properties.setProperty("app.theme.enable", String.valueOf(themeEnableBtn.isSelected()));
        App.properties.setProperty("app.theme.class", "com.formdev.flatlaf.intellijthemes." + themeClass.getSelectedItem());
    }
}
