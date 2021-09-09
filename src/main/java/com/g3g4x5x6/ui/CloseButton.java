package com.g3g4x5x6.ui;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * 选项卡关闭按钮
 */
@Deprecated
@Slf4j
public class CloseButton extends JButton {
    private JTabbedPane mainTabbedPane;

    public CloseButton(String title, JTabbedPane mainTabbedPane) {
        this.mainTabbedPane = mainTabbedPane;
        this.setText("x");
        this.setContentAreaFilled(false);
        this.setBorder(null);
        this.setMargin(new Insets(0, 0, 0, 0));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                log.info("关闭选项卡：" + title);
                mainTabbedPane.removeTabAt(mainTabbedPane.indexOfTab(title));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setText("X");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setText("x");
            }
        });
    }

}
