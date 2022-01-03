package com.g3g4x5x6.ui.embed.editor;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;

import javax.swing.*;
import java.awt.*;

public class EmbedEditor extends JFrame {
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JTabbedPane tabbedPane;
    private JToolBar statusBar;

    public EmbedEditor(){
        this.setLayout(new BorderLayout());
        this.setTitle("简易编辑器");
        this.setSize(new Dimension(1200, 700));
        this.setPreferredSize(new Dimension(1200, 700));

        menuBar = new JMenuBar();
        menuBar.add(new JMenu("文件"));

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        FlatButton addBtn = new FlatButton();
        addBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        addBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/addFile.svg"));
        toolBar.add(addBtn);

        tabbedPane = new JTabbedPane();

        statusBar = new JToolBar();
        statusBar.setFloatable(false);
        statusBar.add(new JLabel("文本文件"));

        this.setJMenuBar(menuBar);
        this.add(toolBar, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(statusBar, BorderLayout.SOUTH);
    }
}
