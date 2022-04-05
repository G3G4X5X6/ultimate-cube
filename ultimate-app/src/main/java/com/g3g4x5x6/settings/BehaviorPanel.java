package com.g3g4x5x6.settings;

import com.g3g4x5x6.App;

import javax.swing.*;
import java.awt.*;

public class BehaviorPanel extends JPanel implements SettingsInterface {
    private FlowLayout leftFlowLayout = new FlowLayout();
    private Box vBox = Box.createVerticalBox();
    private JPanel panel = new JPanel();
    private JCheckBox quitToTrayCheckBox;

    public BehaviorPanel() {
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
         * 系统托盘设置
         */
        JPanel trayPanel = new JPanel();
        trayPanel.setLayout(leftFlowLayout);
        quitToTrayCheckBox = new JCheckBox("退出时，最小化到托盘");
        quitToTrayCheckBox.setSelected(App.properties.getProperty("app.quit.to.tray").equalsIgnoreCase("true"));
        trayPanel.add(quitToTrayCheckBox);
        vBox.add(trayPanel);

        /**
         * Other
         */
    }


    @Override
    public void save() {
        App.properties.setProperty("app.quit.to.tray", String.valueOf(quitToTrayCheckBox.isSelected()));
    }
}
