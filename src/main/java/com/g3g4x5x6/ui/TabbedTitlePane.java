package com.g3g4x5x6.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * 选项卡标题面板
 */
@Slf4j
public class TabbedTitlePane extends JPanel {

    public TabbedTitlePane(String title, JTabbedPane mainTabbedPane) {
        super();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        this.setLayout(flowLayout);
        this.setOpaque(false);
        this.setAutoscrolls(true);

        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment( SwingConstants.CENTER );
        iconLabel.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg"));
        iconLabel.setText(title);


        JTextField titleField = new JTextField();
        titleField.setBorder(null);
        titleField.setOpaque(false);
        titleField.setEditable(false);
        titleField.setText(title);
        titleField.setAutoscrolls(true);
        titleField.setToolTipText("双击编辑选项卡名称");
        titleField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
                    titleField.setEditable(true);
                    titleField.setOpaque(true);
                }
            }
        });
        titleField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                titleField.setEditable(false);
                titleField.setOpaque(false);
            }
        });

        this.add(iconLabel);
//        this.add(titleField);
    }

}
