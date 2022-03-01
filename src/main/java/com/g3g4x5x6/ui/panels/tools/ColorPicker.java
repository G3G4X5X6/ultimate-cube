package com.g3g4x5x6.ui.panels.tools;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;


@Slf4j
public class ColorPicker extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 450;
    private static final int HEIGHT = 200;

    private JMenuBar menuBar;
    private FlowLayout flowLayout;
    private JPanel mainPanel;
    private JTextField rgbField;
    private JPanel colorPanel;
    private JLabel xLabel;
    private JLabel yLabel;
    private Timer timer;
    private Robot robot;

    private AbstractAction openAction;
    private AbstractAction exitAction;

    public ColorPicker() {
        this.setTitle("ColorPicker");
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(this);
        this.setLayout(new BorderLayout());
        this.setAlwaysOnTop(true);
        flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);

        initAction();
        initColorPanel();
        initMainPanel();

        mouseListener();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                timer.cancel();
            }
        });
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                timer.cancel();
                log.debug("失去焦点");
            }
        });
    }

    private void initColorPanel() {
        colorPanel = new JPanel(new BorderLayout());
        colorPanel.setSize(new Dimension(150, 150));
        colorPanel.setPreferredSize(new Dimension(150, 150));
        colorPanel.setBackground(new Color(0, 0, 0));

        JPanel xPanel = new JPanel();
        JLabel xTitle = new JLabel("X:");
        xLabel = new JLabel("666");
        xPanel.add(xTitle);
        xPanel.add(xLabel);

        JPanel yPanel = new JPanel();
        JLabel yTitle = new JLabel("Y:");
        yLabel = new JLabel("777");
        yPanel.add(yTitle);
        yPanel.add(yLabel);

        GridLayout gridLayout = new GridLayout(1, 2);
        JPanel xy = new JPanel();
        xy.setLayout(gridLayout);
        xy.add(xPanel);
        xy.add(yPanel);

        colorPanel.add(xy, BorderLayout.SOUTH);
        this.add(colorPanel, BorderLayout.WEST);
    }

    private void initMainPanel() {
        mainPanel = new JPanel(new BorderLayout());

        JPanel northPane = new JPanel(flowLayout);
        JLabel rgbLabel = new JLabel("RGB: ");
        rgbField = new JTextField(15);
        northPane.add(rgbLabel);
        northPane.add(rgbField);
        mainPanel.add(northPane, BorderLayout.NORTH);

        this.add(mainPanel, BorderLayout.CENTER);
    }

    private void initAction() {
        openAction = new AbstractAction("打开") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Open");
            }
        };
        exitAction = new AbstractAction("退出") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("退出 ColorPicker");
                timer.cancel();
                dispose();
            }
        };


        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        JMenu viewMenu = new JMenu("视图");
        JMenu optionMenu = new JMenu("选项");
        JMenu aboutMenu = new JMenu("关于");

        JCheckBox checkBox = new JCheckBox("Always on top");
        checkBox.setSelected(true);
        checkBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox.isSelected()) {
                    setAlwaysOnTop(true);
                } else {
                    setAlwaysOnTop(false);
                }
            }
        });

        // 快捷键
        JMenuItem rgbItem = new JMenuItem("取色");
        rgbItem.setAccelerator(KeyStroke.getKeyStroke('D', InputEvent.ALT_DOWN_MASK));
        rgbItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.cancel();
                log.debug("取色");
            }
        });

        fileMenu.add(openAction);
        fileMenu.add(exitAction);
        fileMenu.add(rgbItem);
        optionMenu.add(checkBox);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(optionMenu);
        menuBar.add(aboutMenu);
        this.setJMenuBar(menuBar);
    }

    /**
     * 鼠标监听，利用 timer
     */
    public void mouseListener() {

        try {

            robot = new Robot();

        } catch (AWTException e) {

            e.printStackTrace();

        }

        timer = new Timer();

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                setPanelColor();
            }
        }, 100, 100);

    }

    /**
     * 获取位置和颜色，并设置panel颜色
     */
    private void setPanelColor() {
        Point point = MouseInfo.getPointerInfo().getLocation();
        Color pixel = robot.getPixelColor(point.x, point.y);

        colorPanel.setBackground(pixel);
        xLabel.setText(String.valueOf(point.x));
        yLabel.setText(String.valueOf(point.y));
        rgbField.setText(pixel.getRed() + "," + pixel.getGreen() + "," + pixel.getBlue());
        log.debug("Location:x=" + point.x + ", y=" + point.y + "\t" + pixel);
    }
}
