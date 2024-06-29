package com.g3g4x5x6.panel.notepad;


import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.editor.ui.icon.SizeGripIcon;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class NotepadDialog extends JDialog {
    private static NotepadDialog notepad = null;

    public NotepadDialog() {
        setTitle("备忘笔记");
        setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("app.png"))).getImage());

        setModal(false);
        this.setSize(new Dimension(1000, 600));
        this.setPreferredSize(new Dimension(1000, 600));
        this.setMinimumSize(new Dimension(900, 550));
        setLocationRelativeTo(null);


        EditorPanel editorPanel = new EditorPanel();
        StatusBar statusBar = new StatusBar();

        setLayout(new BorderLayout());
        add(editorPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

    }

    public static NotepadDialog getInstance() {
        if (notepad == null) notepad = new NotepadDialog();
        return notepad;
    }


    private static class StatusBar extends JPanel {

        private final JToolBar statusBar;

        public StatusBar() {
            this.setLayout(new BorderLayout());

            this.statusBar = new JToolBar(JToolBar.HORIZONTAL);
            this.statusBar.setFloatable(false);
            this.statusBar.setBorder(null);
            this.statusBar.setMargin(new Insets(0, 0, 0, 0));
            this.add(statusBar, BorderLayout.CENTER);

            initStatusBar();

            // 总是在最后
            this.add(new JLabel(new SizeGripIcon()), BorderLayout.LINE_END);
        }

        private void initStatusBar() {
            JLabel statusLabel = new JLabel();
            statusLabel.setIcon(new FlatSVGIcon("icons/green.svg"));
            statusLabel.setToolTipText("状态提示");

            setComponent(new JLabel("  "));
            setComponent(statusLabel);
        }

        public void setComponent(Component component) {
            statusBar.add(component);
        }

        public void setSeparator() {
            statusBar.addSeparator();
        }


        public void setGlue() {
            statusBar.add(Box.createGlue());
        }

    }

}
