package com.g3g4x5x6.ui.settings;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;


@Slf4j
public class BasicPanel extends JPanel implements SettingsInterface {
    private FlowLayout leftFlowLayout = new FlowLayout();
    private Box vBox = Box.createVerticalBox();
    private JCheckBox themeEnableBtn;
    private JComboBox<String> themeClass;

    public BasicPanel() {
        JScrollPane scrollPane = new JScrollPane(vBox);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftFlowLayout.setAlignment(FlowLayout.LEFT);
        vBox.setBorder(null);

        initVBox();

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void initVBox() {
        /**
         * 主题配置
         */
        Box themeBox = Box.createHorizontalBox();
        themeBox.setBorder(null);
        themeBox.setAutoscrolls(true);
        vBox.add(themeBox);

        themeEnableBtn = new JCheckBox("是否启用主题");
        themeEnableBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        themeBox.add(themeEnableBtn);

        themeClass = new JComboBox<>();
        themeClass.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug(Objects.requireNonNull(themeClass.getSelectedItem()).toString());
            }
        });
    }

    @Override
    public void save() {

    }
}
