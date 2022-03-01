package com.g3g4x5x6.ui.panels.focus;

import com.g3g4x5x6.ui.panels.ssh.editor.EditorPane;
import com.g3g4x5x6.ui.panels.ssh.monitor.MonitorPane;
import com.g3g4x5x6.ui.panels.ssh.sftp.SftpBrowser;
import com.jediterm.terminal.ui.JediTermWidget;

import javax.swing.*;
import java.awt.*;

public class FocusPanel extends JPanel {
    private String title;
    private JediTermWidget terminal;
    private SftpBrowser sftp;
    private MonitorPane monitor;
    private EditorPane editor;

    private int screenWidth;
    private int screenHeight;

    public FocusPanel() {
        this.setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) screenSize.getWidth();//获得屏幕得宽
        screenHeight = (int) screenSize.getHeight();//获得屏幕得高
    }

    public FocusPanel(JPanel terminal) {

    }

    public FocusPanel(JPanel terminal, JPanel sftp) {

    }

    public FocusPanel(JPanel terminal, JPanel sftp, JPanel monitor) {

    }

    public FocusPanel(String title, JediTermWidget terminal, SftpBrowser sftp, MonitorPane monitor, EditorPane editor) {
        this();
        this.title = title;
        this.terminal = terminal;
        this.sftp = sftp;
        this.monitor = monitor;
        this.editor = editor;

        JSplitPane hSplitPane = new JSplitPane();
        JSplitPane vSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane vSplitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        hSplitPane.setDividerLocation(screenWidth / 2);
        vSplitPane1.setDividerLocation(screenHeight / 2);
        vSplitPane2.setDividerLocation(screenHeight / 2);

        hSplitPane.setLeftComponent(vSplitPane1);
        hSplitPane.setRightComponent(vSplitPane2);

        vSplitPane1.setTopComponent(terminal);
        vSplitPane1.setBottomComponent(editor);

        vSplitPane2.setTopComponent(sftp);
        vSplitPane2.setBottomComponent(monitor);
        this.add(hSplitPane, BorderLayout.CENTER);
    }

    public String getTitle() {
        return title;
    }

    public JediTermWidget getTerminal() {
        return terminal;
    }

    public SftpBrowser getSftp() {
        return sftp;
    }

    public MonitorPane getMonitor() {
        return monitor;
    }

    public EditorPane getEditor() {
        return editor;
    }
}
