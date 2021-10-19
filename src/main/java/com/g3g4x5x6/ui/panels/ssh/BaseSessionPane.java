package com.g3g4x5x6.ui.panels.ssh;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Deprecated
@Slf4j
public class BaseSessionPane extends JPanel {
    private BorderLayout borderLayout = new BorderLayout();

    // TODO
    protected JTabbedPane basicSettingTabbedPane = new JTabbedPane();
    protected String basicSettingPaneTitle = "";
    protected BasicSettingPane basicSettingPane = new BasicSettingPane();

    protected JTabbedPane advancedSettingTabbedPane = new JTabbedPane();
    protected String advancedSettingPaneTitle = "";
    protected AdvancedSettingPane advancedSettingPane = new AdvancedSettingPane();

    private JPanel btnPane = new JPanel();
    private JButton saveBtn = new JButton("保存");
    private JButton cancelBtn = new JButton("取消");


    public BaseSessionPane() {
        this.setLayout(borderLayout);
    }

    protected void initSettingPane(){

        basicSettingTabbedPane.addTab(getBasicSettingPaneTitle(), basicSettingPane);
        advancedSettingTabbedPane.addTab(getAdvancedSettingPaneTitle(), advancedSettingPane);

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        btnPane.setLayout(flowLayout);

        saveBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("保存会话");
            }
        });
        cancelBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("取消");
            }
        });
        cancelBtn.setVisible(false);
        btnPane.add(saveBtn);
        btnPane.add(cancelBtn);

        this.add(basicSettingTabbedPane, BorderLayout.NORTH);
        this.add(advancedSettingTabbedPane, BorderLayout.CENTER);
        this.add(btnPane, BorderLayout.SOUTH);
    }

    public void saveSession(){

    }

    public BasicSettingPane getBasicSettingPane() {
        return basicSettingPane;
    }

    public AdvancedSettingPane getAdvancedSettingPane() {
        return advancedSettingPane;
    }

    protected String getBasicSettingPaneTitle() {
        return basicSettingPaneTitle;
    }

    protected void setBasicSettingPaneTitle(String basicSettingPaneTitle) {
        this.basicSettingPaneTitle = basicSettingPaneTitle;
    }

    public String getAdvancedSettingPaneTitle() {
        return advancedSettingPaneTitle;
    }

    public void setAdvancedSettingPaneTitle(String advancedSettingPaneTitle) {
        this.advancedSettingPaneTitle = advancedSettingPaneTitle;
    }

    protected class BasicSettingPane extends JPanel{
        private FlowLayout flowLayout = new FlowLayout();

        public BasicSettingPane() {
            flowLayout.setAlignment(FlowLayout.LEFT);
            this.setLayout(flowLayout);
        }
    }

    protected class AdvancedSettingPane extends JPanel{
        private FlowLayout flowLayout = new FlowLayout();

        public AdvancedSettingPane() {

        }
    }
}
