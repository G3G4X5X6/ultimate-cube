package com.g3g4x5x6.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiUtil {
    public static void setTabTitle(JTabbedPane tabbedPane, int tabIndex, String title, FlatSVGIcon icon) {
        // 创建一个标签标题组件
        JLabel label = new JLabel(title);
        label.setIcon(icon);

        // 添加一个小按钮作为关闭标签的功能（如果需要的话）
        JButton closeButton = new JButton("x");
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tabbedPane.remove(tabIndex);
            }
        });

        // 创建一个面板来包含标签和按钮
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tabPanel.setOpaque(false);
        tabPanel.add(label);
        tabPanel.add(closeButton);

        // 将自定义面板设置为标签标题
        tabbedPane.setTabComponentAt(tabIndex, tabPanel);
    }
}
