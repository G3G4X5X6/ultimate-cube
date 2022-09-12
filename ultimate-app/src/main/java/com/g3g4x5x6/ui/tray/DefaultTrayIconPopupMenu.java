package com.g3g4x5x6.ui.tray;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.App;
import com.g3g4x5x6.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class DefaultTrayIconPopupMenu extends JPopupMenu {
    public DefaultTrayIconPopupMenu(){
        initMenuItem();
    }

    private void initMenuItem(){

        JMenuItem openMenuItem = new JMenuItem("打开");
        openMenuItem.setIcon(new FlatSVGIcon("icons/start.svg"));
        openMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.openApp();
            }
        });

        JMenuItem exitMenuItem = new JMenuItem("退出");
        exitMenuItem.setIcon(new FlatSVGIcon("icons/exit.svg"));
        exitMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JMenuItem editorMenuItem = new JMenuItem("内置编辑器");
        editorMenuItem.setIcon(new FlatSVGIcon("icons/editScheme.svg"));
        editorMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.editorFrame.setVisible(true);
            }
        });

        this.add(openMenuItem);
        this.addSeparator();
        this.add(editorMenuItem);
        this.addSeparator();
        this.add(exitMenuItem);
    }
}
