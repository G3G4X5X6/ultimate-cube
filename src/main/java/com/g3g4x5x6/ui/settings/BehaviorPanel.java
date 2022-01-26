package com.g3g4x5x6.ui.settings;

import com.g3g4x5x6.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BehaviorPanel extends JPanel implements SettingsInterface {
    private FlowLayout leftFlowLayout = new FlowLayout();
    private JPanel panel = new JPanel();
    private JCheckBox quitToTrayCheckBox;

    public BehaviorPanel(){
        leftFlowLayout.setAlignment(FlowLayout.LEFT);

        panel.setLayout(leftFlowLayout);
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
         * 系统托盘设置
         */
        quitToTrayCheckBox = new JCheckBox("退出时，最小化到托盘");
        quitToTrayCheckBox.setSelected(App.properties.getProperty("app.quit.to.tray").equalsIgnoreCase("true"));
        panel.add(quitToTrayCheckBox);
    }


    @Override
    public void save() {
        App.properties.setProperty("app.quit.to.tray", String.valueOf(quitToTrayCheckBox.isSelected()));
    }
}
