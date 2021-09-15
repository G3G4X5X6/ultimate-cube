package com.g3g4x5x6.ui.panels.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.utils.CommonUtil;
import com.g3g4x5x6.utils.DialogUtil;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


@Slf4j
public class SerialPane extends JPanel {
    private static final int[] FLOW_CONTROL = new int[]{0, 1, 16, 256, 4096, 65536, 1048576};
    private static final int[] STOP_BIT = new int[]{1, 2, 3};

    private JTabbedPane mainTabbedPane;
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
    private JComboBox<String> parityBitComboBox;
    private JComboBox<String> fcComboBox;

    public SerialPane(JTabbedPane mainTabbedPane) {
        this.mainTabbedPane = mainTabbedPane;
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

    private void initBasicPane() {
        basicSettingTabbedPane.addTab(basicSettingPaneTitle, basicSettingPane);
        // 串口
        JPanel comPane = new JPanel();
        JLabel comLabel = new JLabel("串口*");
        String[] defaultComs = new String[]{"COM1", "COM2", "COM3", "COM4", "COM5", "COM6"};
        comComboBox = new JComboBox<>(defaultComs);
        comComboBox.setMinimumSize(new Dimension(200, 20));
        if (SerialPort.getCommPorts().length >= 1) {
            comComboBox.removeAllItems();
            for (SerialPort comPort : SerialPort.getCommPorts()) {
                log.debug(comPort.getDescriptivePortName());
                comComboBox.addItem(comPort.getDescriptivePortName());
            }
        }
        comComboBox.setEditable(true);
        comPane.add(comLabel);
        comPane.add(comComboBox);

        // 波特率
        JPanel ratePane = new JPanel();
        JLabel rateLabel = new JLabel("波特率*");
        String[] defaultRates = new String[]{"1200", "2400", "4800", "9600"};
        rateComboBox = new JComboBox<>(defaultRates);
        rateComboBox.setEditable(true);
        rateComboBox.setSelectedItem("9600");
        ratePane.add(rateLabel);
        ratePane.add(rateComboBox);

        // 按钮
        JPanel btnPane = new JPanel();
        JButton saveBtn = new JButton("快速连接");
        saveBtn.setToolTipText("自动保存会话");
        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Serial 快速连接");
                if (testOpen()) {
                    // TODO 保存会话

                    // 打开会话
                    mainTabbedPane.insertTab("Serial-" + comComboBox.getSelectedItem(), new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg"),
                            createTerminalWidget(),
                            "Serial-" + comComboBox.getSelectedItem(),
                            mainTabbedPane.getSelectedIndex());
                    mainTabbedPane.removeTabAt(mainTabbedPane.getSelectedIndex());
                } else {
                    DialogUtil.warn("Serial is not Open!");
                }
            }
        });
        JButton testBtn = new JButton("测试通信");
        testBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Serial 测试通信");
                if (testOpen()) {
                    DialogUtil.info("Serial is Open!");
                } else {
                    DialogUtil.warn("Serial is not Open!");
                }
            }
        });
        btnPane.add(saveBtn);
        btnPane.add(testBtn);


        basicSettingPane.add(comPane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(ratePane);
        basicSettingPane.add(Box.createHorizontalGlue());
        basicSettingPane.add(btnPane);
    }

    private void initAdvancePane() {
        advancedSettingTabbedPane.addTab(advancedSettingPaneTitle, advancedSettingPane);

        // 数据位
        JPanel dataBitPane = new JPanel();
        JLabel dataBitLabel = new JLabel("数据位*");
        String[] defaultDataBit = new String[]{"5", "6", "7", "8"};
        dataBitComboBox = new JComboBox<>(defaultDataBit);
        dataBitComboBox.setEditable(true);
        dataBitComboBox.setSelectedItem("8");
        dataBitPane.add(dataBitLabel);
        dataBitPane.add(dataBitComboBox);

        // 停止位
//        public static final int ONE_STOP_BIT = 1;
//        public static final int ONE_POINT_FIVE_STOP_BITS = 2;
//        public static final int TWO_STOP_BITS = 3;
        JPanel stopBitPane = new JPanel();
        JLabel stopBitLabel = new JLabel("停止位*");
        String[] defaultStopBit = new String[]{"1", "1.5", "2"};
        stopBitComboBox = new JComboBox<>(defaultStopBit);
        stopBitComboBox.setEditable(true);
        stopBitPane.add(stopBitLabel);
        stopBitPane.add(stopBitComboBox);

        // 校验位
//        public static final int NO_PARITY = 0;
//        public static final int ODD_PARITY = 1;
//        public static final int EVEN_PARITY = 2;
//        public static final int MARK_PARITY = 3;
//        public static final int SPACE_PARITY = 4;
        JPanel parityBitPane = new JPanel();
        JLabel parityBitLabel = new JLabel("奇偶校验*");
        String[] defaultParityBit = new String[]{"NO", "ODD", "EVEN", "MARK", "SPACE"};
        parityBitComboBox = new JComboBox<>(defaultParityBit);
        parityBitComboBox.setEditable(true);
        parityBitPane.add(parityBitLabel);
        parityBitPane.add(parityBitComboBox);

        // 流控
//        public static final int FLOW_CONTROL_DISABLED = 0;
//        public static final int FLOW_CONTROL_RTS_ENABLED = 1;
//        public static final int FLOW_CONTROL_CTS_ENABLED = 16;
//        public static final int FLOW_CONTROL_DSR_ENABLED = 256;
//        public static final int FLOW_CONTROL_DTR_ENABLED = 4096;
//        public static final int FLOW_CONTROL_XONXOFF_IN_ENABLED = 65536;
//        public static final int FLOW_CONTROL_XONXOFF_OUT_ENABLED = 1048576;
        JPanel fcPane = new JPanel();
        JLabel fcLabel = new JLabel("流控*");
        String[] defaultFc = new String[]{"DISABLED", "RTS", "CTS", "DSR", "DTR", "XONXOFF_IN", "XONXOFF_OUT"};
        fcComboBox = new JComboBox<>(defaultFc);
        fcComboBox.setEditable(true);
        fcPane.add(fcLabel);
        fcPane.add(fcComboBox);

        advancedSettingPane.add(dataBitPane);
        advancedSettingPane.add(Box.createHorizontalGlue());
        advancedSettingPane.add(stopBitPane);
        advancedSettingPane.add(Box.createHorizontalGlue());
        advancedSettingPane.add(parityBitPane);
        advancedSettingPane.add(Box.createHorizontalGlue());
        advancedSettingPane.add(fcPane);
    }

    private Boolean testOpen() {
        Boolean flag = false;
        for (SerialPort comPort : CommonUtil.getCommPorts()) {
            if (comPort.getDescriptivePortName().strip().equals(comComboBox.getSelectedItem().toString().strip())) {
                if (comPort.isOpen()){
                    return true;
                }
                comPort.setBaudRate(Integer.valueOf(rateComboBox.getSelectedItem().toString()));
                comPort.setNumDataBits(Integer.valueOf(dataBitComboBox.getSelectedItem().toString()));
                comPort.setNumStopBits(Integer.valueOf(stopBitComboBox.getSelectedItem().toString()));
                comPort.setParity(parityBitComboBox.getSelectedIndex());
                comPort.setFlowControl(FLOW_CONTROL[fcComboBox.getSelectedIndex()]);
                comPort.openPort();
                flag = comPort.isOpen();
                comPort.closePort();
            }
        }
        return flag;
    }

    private SerialPort openComPort() {
        SerialPort port = null;
        for (SerialPort comPort : CommonUtil.getCommPorts()) {
            if (comPort.getDescriptivePortName().strip().equals(comComboBox.getSelectedItem().toString().strip())) {
                if (comPort.isOpen()){
                    return comPort;
                }
                comPort.setBaudRate(Integer.valueOf(rateComboBox.getSelectedItem().toString()));
                comPort.setNumDataBits(Integer.valueOf(dataBitComboBox.getSelectedItem().toString()));
                comPort.setNumStopBits(STOP_BIT[stopBitComboBox.getSelectedIndex()]);
                comPort.setParity(parityBitComboBox.getSelectedIndex());
                comPort.setFlowControl(FLOW_CONTROL[fcComboBox.getSelectedIndex()]);
                comPort.openPort();
                comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                port = comPort;
            }
        }
        return port;
    }

    private @NotNull JediTermWidget createTerminalWidget() {
        JediTermWidget widget = new JediTermWidget(new SerialSettingsProvider());
        widget.setTtyConnector(createTtyConnector());
        widget.start();
        return widget;
    }

    private @NotNull TtyConnector createTtyConnector() {
        return new SerialTtyConnector(openComPort());
    }
}
