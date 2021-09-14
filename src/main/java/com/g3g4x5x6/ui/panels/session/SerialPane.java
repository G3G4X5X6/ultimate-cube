package com.g3g4x5x6.ui.panels.session;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


@Slf4j
public class SerialPane extends JPanel {
    private JTabbedPane basicSettingTabbedPane;
    private String basicSettingPaneTitle;
    private JPanel basicSettingPane;

    private JTabbedPane advancedSettingTabbedPane;
    private String advancedSettingPaneTitle;
    private JPanel advancedSettingPane;

    private JComboBox<String> comComboBox;
    private JComboBox<String> rateComboBox;
    private JComboBox<String> dataBitComboBox;
    private JComboBox<String> stopBitComboBox;
    private JComboBox<String> ccBitComboBox;

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
        comPane.add(comLabel);
        comPane.add(comComboBox);

        // 波特率
        JPanel ratePane = new JPanel();
        JLabel rateLabel = new JLabel("波特率*");
        String[] defaultRates = new String[]{"1200", "2400", "4800", "9600"};
        rateComboBox = new JComboBox<>(defaultRates);
        rateComboBox.setEditable(true);
        ratePane.add(rateLabel);
        ratePane.add(rateComboBox);

        // 数据位
        JPanel dataBitPane = new JPanel();
        JLabel dataBitLabel = new JLabel("数据位*");
        String[] defaultDataBit = new String[]{"5", "6", "7", "8"};
        dataBitComboBox = new JComboBox<>(defaultDataBit);
        dataBitComboBox.setEditable(true);
        dataBitPane.add(dataBitLabel);
        dataBitPane.add(dataBitComboBox);

        // 停止位
        JPanel stopBitPane = new JPanel();
        JLabel stopBitLabel = new JLabel("停止位*");
        String[] defaultStopBit = new String[]{"1", "1.5", "2"};
        stopBitComboBox = new JComboBox<>(defaultStopBit);
        stopBitComboBox.setEditable(true);
        stopBitPane.add(stopBitLabel);
        stopBitPane.add(stopBitComboBox);

        // 校验位
        JPanel ccBitPane = new JPanel();
        JLabel ccBitLabel = new JLabel("校验位*");
        String[] defaultCcBit = new String[]{"None", "0", "1"};
        ccBitComboBox = new JComboBox<>(defaultCcBit);
        ccBitComboBox.setEditable(true);
        ccBitPane.add(ccBitLabel);
        ccBitPane.add(ccBitComboBox);

        // 按钮
        JPanel btnPane = new JPanel();
        JButton saveBtn = new JButton("快速连接");
        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Serial 快速连接");
            }
        });
        JButton testBtn = new JButton("测试通信");
        testBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Serial 测试通信");
            }
        });
        btnPane.add(saveBtn);
        btnPane.add(testBtn);


        basicSettingPane.add(comPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(ratePane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(dataBitPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(stopBitPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(ccBitPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(btnPane);
    }

    private void initAdvancePane(){
        advancedSettingTabbedPane.addTab(advancedSettingPaneTitle, advancedSettingPane);
    }
}
