package com.g3g4x5x6.ui.dialog;

import com.g3g4x5x6.App;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_MINIMUM_TAB_WIDTH;
import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TAB_ALIGNMENT;


@Slf4j
public class SettingsDialog extends JDialog {
    private BorderLayout borderLayout;
    private JPanel panel;
    private JTabbedPane tabbedPane;

    public SettingsDialog() {
        super(App.mainFrame);
        this.setTitle("全局配置");
        borderLayout = new BorderLayout();
        this.setLayout(borderLayout);
        this.setPreferredSize(new Dimension(700, 500));
        this.setSize(new Dimension(700, 500));
        this.setModal(true);
        this.setLocationRelativeTo(App.mainFrame);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);

        initPanel();
    }

    private void initPanel(){

        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(SwingConstants.LEFT);
        initTabAlignment(tabbedPane, SwingConstants.TRAILING);

        // TODO 保存、取消
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        JPanel southPane = new JPanel();
        JButton saveBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("保存全局设置");
            }
        });

        cancelBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("关闭配置窗口");
                dispose();
            }
        });

        southPane.add(Box.createGlue());
        southPane.add(saveBtn);
        southPane.add(cancelBtn);
        southPane.add(Box.createGlue());
        southPane.setLayout(flowLayout);

        panel.add(tabbedPane, BorderLayout.CENTER);
        panel.add(southPane, BorderLayout.SOUTH);
    }

    private void initTabAlignment( JTabbedPane tabbedPane, int tabAlignment ) {
        boolean vertical = (tabbedPane.getTabPlacement() == JTabbedPane.LEFT || tabbedPane.getTabPlacement() == JTabbedPane.RIGHT);
        tabbedPane.putClientProperty( TABBED_PANE_TAB_ALIGNMENT, tabAlignment );
        if( !vertical )
            tabbedPane.putClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH, 80 );
        tabbedPane.addTab( "基本设置", null );
        if( vertical ) {
            tabbedPane.addTab( "行为设置", null );
            tabbedPane.addTab( "终端设置", null );
        }
    }
}
