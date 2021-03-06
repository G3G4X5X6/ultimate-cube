package com.g3g4x5x6.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;


@Slf4j
public class SerialPane extends JPanel {
    private static final int[] FLOW_CONTROL = new int[]{0, 1, 16, 256, 4096, 65536, 1048576};
    private static final int[] STOP_BIT = new int[]{1, 2, 3};
    private static final HashSet<SerialPort> ports = new HashSet<>();

    private final JTabbedPane tabbedPane;
    private final JTabbedPane basicSettingTabbedPane;
    private final String basicSettingPaneTitle;
    private final JPanel basicSettingPane;

    private final JTabbedPane advancedSettingTabbedPane;
    private final String advancedSettingPaneTitle;
    private final JPanel advancedSettingPane;

    private JComboBox<String> comComboBox;
    private JComboBox<String> rateComboBox;
    private JComboBox<String> dataBitComboBox;
    private JComboBox<String> stopBitComboBox;
    private JComboBox<String> parityBitComboBox;
    private JComboBox<String> fcComboBox;

    public SerialPane(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
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
        // ??????
        JPanel comPane = new JPanel();
        JLabel comLabel = new JLabel("??????*");
        String[] defaultComs = new String[]{"COM1  ", "COM2", "COM3", "COM4", "COM5", "COM6"};
        comComboBox = new JComboBox<>(defaultComs);
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

        // ?????????
        JPanel ratePane = new JPanel();
        JLabel rateLabel = new JLabel("?????????*");
        String[] defaultRates = new String[]{"1200", "2400", "4800", "9600"};
        rateComboBox = new JComboBox<>(defaultRates);
        rateComboBox.setEditable(true);
        rateComboBox.setSelectedItem("9600");
        ratePane.add(rateLabel);
        ratePane.add(rateComboBox);

        // ??????
        JPanel btnPane = new JPanel();
        JButton saveBtn = new JButton("????????????");
        saveBtn.setToolTipText("??????????????????");
        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Serial ????????????");
                if (testOpen()) {
                    // TODO ????????????

                    // ????????????
                    tabbedPane.insertTab("Serial-" + comComboBox.getSelectedItem(), new FlatSVGIcon("icons/OpenTerminal_13x13.svg"),
                            createTerminalWidget(),
                            "Serial-" + comComboBox.getSelectedItem(),
                            tabbedPane.getSelectedIndex());
                    tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
                } else {
                    JOptionPane.showMessageDialog(SerialPane.this, "Serial is not Open!", "??????", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        JButton testBtn = new JButton("????????????");
        testBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Serial ????????????");
                if (testOpen()) {
                    JOptionPane.showMessageDialog(SerialPane.this, "Serial is Open!", "??????", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(SerialPane.this, "Serial is not Open!", "??????", JOptionPane.WARNING_MESSAGE);
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

        // ?????????
        JPanel dataBitPane = new JPanel();
        JLabel dataBitLabel = new JLabel("?????????*");
        String[] defaultDataBit = new String[]{"5", "6", "7", "8"};
        dataBitComboBox = new JComboBox<>(defaultDataBit);
        dataBitComboBox.setEditable(true);
        dataBitComboBox.setSelectedItem("8");
        dataBitPane.add(dataBitLabel);
        dataBitPane.add(dataBitComboBox);

        // ?????????
        JPanel stopBitPane = new JPanel();
        JLabel stopBitLabel = new JLabel("?????????*");
        String[] defaultStopBit = new String[]{"1", "1.5", "2"};
        stopBitComboBox = new JComboBox<>(defaultStopBit);
        stopBitComboBox.setEditable(true);
        stopBitPane.add(stopBitLabel);
        stopBitPane.add(stopBitComboBox);

        // ?????????
        JPanel parityBitPane = new JPanel();
        JLabel parityBitLabel = new JLabel("????????????*");
        String[] defaultParityBit = new String[]{"NO", "ODD", "EVEN", "MARK", "SPACE"};
        parityBitComboBox = new JComboBox<>(defaultParityBit);
        parityBitComboBox.setEditable(true);
        parityBitPane.add(parityBitLabel);
        parityBitPane.add(parityBitComboBox);

        // ??????
        JPanel fcPane = new JPanel();
        JLabel fcLabel = new JLabel("??????*");
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

    public static HashSet<SerialPort> getCommPorts() {
        Collections.addAll(ports, SerialPort.getCommPorts());
        return ports;
    }

    private Boolean testOpen() {
        boolean flag = false;
        for (SerialPort comPort : SerialPane.getCommPorts()) {
            if (comPort.getDescriptivePortName().strip().equals(Objects.requireNonNull(comComboBox.getSelectedItem()).toString().strip())) {
                if (comPort.isOpen()) {
                    return true;
                }
                comPort.setBaudRate(Integer.parseInt(Objects.requireNonNull(rateComboBox.getSelectedItem()).toString()));
                comPort.setNumDataBits(Integer.parseInt(Objects.requireNonNull(dataBitComboBox.getSelectedItem()).toString()));
                comPort.setNumStopBits(Integer.parseInt(Objects.requireNonNull(stopBitComboBox.getSelectedItem()).toString()));
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
        for (SerialPort comPort : SerialPane.getCommPorts()) {
            if (comPort.getDescriptivePortName().strip().equals(Objects.requireNonNull(comComboBox.getSelectedItem()).toString().strip())) {
                if (comPort.isOpen()) {
                    return comPort;
                }
                comPort.setBaudRate(Integer.parseInt(Objects.requireNonNull(rateComboBox.getSelectedItem()).toString()));
                comPort.setNumDataBits(Integer.parseInt(Objects.requireNonNull(dataBitComboBox.getSelectedItem()).toString()));
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
        JediTermWidget widget = new JediTermWidget(new com.g3g4x5x6.serial.SerialSettingsProvider());
        widget.setTtyConnector(createTtyConnector());
        widget.start();
        return widget;
    }

    private @NotNull TtyConnector createTtyConnector() {
        return new com.g3g4x5x6.serial.SerialTtyConnector(openComPort());
    }
}
