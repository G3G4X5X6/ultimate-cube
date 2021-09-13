package com.g3g4x5x6.ui.panels.session;

import javax.swing.*;
import java.awt.*;

public class SerialPane extends JPanel {
    private JTabbedPane basicSettingTabbedPane;
    private String basicSettingPaneTitle;
    private JPanel basicSettingPane;

    private JTabbedPane advancedSettingTabbedPane;
    private String advancedSettingPaneTitle;
    private JPanel advancedSettingPane;

    private JComboBox<String> comComboBox;
    private JComboBox<String> rateComboBox;

    public SerialPane(){
        this.setLayout(new BorderLayout());
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        basicSettingTabbedPane = new JTabbedPane();
        basicSettingPane = new JPanel();
        basicSettingPane.setLayout(flowLayout);
        basicSettingPaneTitle = "Basic Serial Settings";

        advancedSettingTabbedPane = new JTabbedPane();
        advancedSettingPane = new JPanel();
        advancedSettingPane.setLayout(flowLayout);
        advancedSettingPaneTitle = "Advanced Serial Settings";

        initBasicPane();
        initAdvancePane();

        this.add(basicSettingTabbedPane, BorderLayout.NORTH);
        this.add(advancedSettingTabbedPane, BorderLayout.CENTER);
    }

    private void initBasicPane(){
        basicSettingTabbedPane.addTab(basicSettingPaneTitle, basicSettingPane);
        // 串口
        JPanel comPane = new JPanel();
        JLabel comLabel = new JLabel("串口*");
        String[] defaultComs = new String[]{"COM1", "COM2", "COM3", "COM4", "COM5", "COM6"};
        comComboBox = new JComboBox<>(defaultComs);
        comComboBox.setEditable(true);

        // 波特率
        JPanel ratePane = new JPanel();
        JLabel rateLabel = new JLabel("波特率*");
        String[] defaultRates = new String[]{"1200", "2400", "4800", "9600"};
        rateComboBox = new JComboBox<>(defaultRates);
        rateComboBox.setEditable(true);

        // 数据位

        // 停止位

        // 校验位

        // 校验类型

        // Hex/ASCII 发送和显示

        comPane.add(comLabel);
        comPane.add(comComboBox);
        ratePane.add(rateLabel);
        ratePane.add(rateComboBox);
        basicSettingPane.add(comPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(ratePane);

    }

    private void initAdvancePane(){
        advancedSettingTabbedPane.addTab(advancedSettingPaneTitle, advancedSettingPane);
    }
}
