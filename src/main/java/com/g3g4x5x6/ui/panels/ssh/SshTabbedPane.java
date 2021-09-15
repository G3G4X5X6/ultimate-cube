package com.g3g4x5x6.ui.panels.ssh;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.panels.EditorPane;
import com.g3g4x5x6.ui.panels.sftp.SftpPane;
import com.jediterm.terminal.ui.JediTermWidget;

import javax.swing.*;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT;

public class SshTabbedPane extends JTabbedPane {
    private JTabbedPane mainTabbedPane;

    public SshTabbedPane(JTabbedPane mainTabbedPane, JediTermWidget Ssh, String hostField, String portField, String userField, String passField) {
        this.mainTabbedPane = mainTabbedPane;
        this.addTab("SSH", Ssh);
        this.addTab("SFTP", new SftpPane(hostField, portField, userField, passField));
        this.addTab("Monitor", new JPanel());
        this.addTab("Editor", new EditorPane());

        customComponents();
    }

    private void customComponents() {
        JToolBar leading = null;
        JToolBar trailing = null;
//        if( leadingComponentButton.isSelected() ) {
            leading = new JToolBar();
            leading.setFloatable( false );
            leading.setBorder( null );
            leading.add( new JButton( new FlatSVGIcon( "com/g3g4x5x6/ui/icons/project.svg" ) ) );
//        }
//        if( trailingComponentButton.isSelected() ) {
            trailing = new JToolBar();
            trailing.setFloatable( false );
            trailing.setBorder( null );
            trailing.add( new JButton( new FlatSVGIcon( "com/g3g4x5x6/ui/icons/buildLoadChanges.svg" ) ) );
            trailing.add( Box.createHorizontalGlue() );
            trailing.add( new JButton( new FlatSVGIcon( "com/g3g4x5x6/ui/icons/commit.svg" ) ) );
            trailing.add( new JButton( new FlatSVGIcon( "com/g3g4x5x6/ui/icons/diff.svg" ) ) );
            trailing.add( new JButton( new FlatSVGIcon( "com/g3g4x5x6/ui/icons/listFiles.svg" ) ) );
//        }
//        this.putClientProperty( TABBED_PANE_LEADING_COMPONENT, leading );
        this.putClientProperty( TABBED_PANE_TRAILING_COMPONENT, trailing );
    }

}
