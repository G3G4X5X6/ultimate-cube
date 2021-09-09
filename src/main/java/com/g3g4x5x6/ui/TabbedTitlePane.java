package com.g3g4x5x6.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
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

        JLabel numLabel = new JLabel();
        numLabel.setHorizontalAlignment( SwingConstants.CENTER );
        numLabel.setText("11");

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
                // 选中标签页
                mainTabbedPane.setSelectedIndex(mainTabbedPane.indexOfTab(title));
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

        FlatButton closeBtn = new FlatButton();
        closeBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        closeBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/close.svg"));
        closeBtn.setBorder(null);
        closeBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("关闭选项卡：" + title);
                mainTabbedPane.removeTabAt(mainTabbedPane.indexOfTab(title));
            }
        });
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/closeHover.svg"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/close.svg"));
            }
        });

        this.add(numLabel);
        this.add(titleField);
        this.add(closeBtn);

    }

}
