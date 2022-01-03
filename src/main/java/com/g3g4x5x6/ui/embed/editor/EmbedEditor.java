package com.g3g4x5x6.ui.embed.editor;

import javax.swing.*;
import java.awt.*;

public class EmbedEditor extends JFrame {
    private JToolBar toolBar;
    private JTabbedPane tabbedPane;
    private JToolBar statusBar;

    public EmbedEditor(){
        this.setLayout(new BorderLayout());

        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        tabbedPane = new JTabbedPane();

        statusBar = new JToolBar();
        statusBar.setFloatable(false);

        this.add(toolBar, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(statusBar, BorderLayout.SOUTH);
    }
}
