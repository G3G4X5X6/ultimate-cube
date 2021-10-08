package com.g3g4x5x6.ui.panels.tools;


import com.g3g4x5x6.App;

import java.awt.Point;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.AWTException;
import java.awt.Robot;
import javax.swing.*;

import java.util.Timer;
import java.util.TimerTask;


public class ColorPicker extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 200;

    private JPanel mainPanel;
    private Robot robot;

    public ColorPicker() {
        super(App.mainFrame);
        // 窗体标题
        this.setTitle("ColorPicker");
        // 窗体设置
        this.setSize(WIDTH, HEIGHT);
        // 窗体居中
        this.setLocationRelativeTo(this);

        // 添加 panel
        mainPanel = new JPanel();
        this.add(mainPanel);

        // 鼠标监听
        mouseListener();
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

        Timer timer = new Timer();

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

        System.out.println("Location:x=" + point.x + ", y=" + point.y + "\t" + pixel);

        mainPanel.setBackground(pixel);

    }
}
