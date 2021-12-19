package com.g3g4x5x6.ui.panels.ssh;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.panels.editor.EditorPane;
import com.g3g4x5x6.ui.panels.ssh.monitor.MonitorPane;
import com.g3g4x5x6.ui.panels.ssh.sftp.SftpPane;
import com.jediterm.terminal.ui.JediTermWidget;

import javax.swing.*;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT;

public class SshTabbedPane extends JTabbedPane {

    public SshTabbedPane(JTabbedPane mainTabbedPane, JediTermWidget Ssh, String hostField, String portField, String userField, String passField) {
        // TODO
        this.addTab("SSH", Ssh);
        this.addTab("SFTP", new SftpPane(hostField, portField, userField, passField));
        this.addTab("Monitor", new MonitorPane(hostField, Integer.parseInt(portField), userField, passField));
        this.addTab("Editor", new EditorPane(hostField, Integer.parseInt(portField), userField, passField));

        customComponents();
    }

    private void customComponents() {
        JToolBar leading = null;
        JToolBar trailing = null;

        leading = new JToolBar();
        leading.setFloatable(false);
        leading.setBorder(null);
        leading.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/project.svg")));

        trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/buildLoadChanges.svg")));
        trailing.add(Box.createHorizontalGlue());
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/commit.svg")));
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/diff.svg")));
        trailing.add(new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/listFiles.svg")));

//        this.putClientProperty( TABBED_PANE_LEADING_COMPONENT, leading );
        this.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

}
