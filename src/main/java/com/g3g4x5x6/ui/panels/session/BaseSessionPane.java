package com.g3g4x5x6.ui.panels.session;

import javax.swing.*;
import java.awt.*;

public class BaseSessionPane extends JPanel {
    private BorderLayout borderLayout = new BorderLayout();

    // TODO
    protected JTabbedPane basicSettingTabbedPane = new JTabbedPane();
    protected String basicSettingPaneTitle = "";
    protected BasicSettingPane basicSettingPane = new BasicSettingPane();

    protected JTabbedPane advancedSettingTabbedPane = new JTabbedPane();
    protected String advancedSettingPaneTitle = "";
    protected AdvancedSettingPane advancedSettingPane = new AdvancedSettingPane();


    public BaseSessionPane() {
        this.setLayout(borderLayout);
    }

    protected void initSettingPane(){

        basicSettingTabbedPane.addTab(getBasicSettingPaneTitle(), basicSettingPane);
        advancedSettingTabbedPane.addTab(getAdvancedSettingPaneTitle(), advancedSettingPane);

        this.add(basicSettingTabbedPane, BorderLayout.NORTH);
        this.add(advancedSettingTabbedPane, BorderLayout.CENTER);
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
