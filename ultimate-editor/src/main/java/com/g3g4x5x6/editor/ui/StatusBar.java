package com.g3g4x5x6.editor.ui;


import com.g3g4x5x6.editor.ui.icon.SizeGripIcon;

import javax.swing.*;
import java.awt.*;


public class StatusBar extends JPanel {

    private final JToolBar statusBar;

    public StatusBar() {
        this.setLayout(new BorderLayout());

        this.statusBar = new JToolBar(JToolBar.HORIZONTAL);
        this.statusBar.setFloatable(false);
        this.statusBar.setBorder(null);
        this.statusBar.setMargin(new Insets(0, 0, 0, 0));
        this.add(statusBar, BorderLayout.CENTER);

        // 总是在最后
        this.add(new JLabel(new SizeGripIcon()), BorderLayout.LINE_END);
    }

    public void setComponent(Component component){
        statusBar.add(component);
    }

    public void setSeparator(){
        statusBar.addSeparator();
    }


    public void setGlue(){
        statusBar.add(Box.createGlue());
    }

}
