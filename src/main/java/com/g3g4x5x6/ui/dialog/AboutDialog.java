package com.g3g4x5x6.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;


/**
 * 关于弹窗
 */
public class AboutDialog extends JDialog {
    private BorderLayout borderLayout = new BorderLayout();
    private JPanel titlePane;
    private JLabel titleLabel;
    private JLabel versionLabel;

    private JPanel contentPane;
    private JLabel aboutLabel;
    private JLabel thankLabel;

    // TODO thanks label



    public AboutDialog() {
        this.setLayout(borderLayout);
        this.setSize(new Dimension(700, 500));
        this.setLocationRelativeTo(null);
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setLocationRelativeTo(null);
                setVisible(false);
            }
        });

        init();
    }

    private void init(){
        // 产品名称、版本
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.CENTER);
        titlePane = new JPanel(flowLayout);
        titleLabel = new JLabel("UltimateShell");
        versionLabel = new JLabel("Home Edition v1.0.0");
        titlePane.add(titleLabel);
        titlePane.add(versionLabel);

        // 产品介绍、致谢
        BorderLayout borderLayout = new BorderLayout();
        contentPane = new JPanel(borderLayout);
        aboutLabel = new JLabel("About");
        thankLabel = new JLabel("Special thanks to...");
        contentPane.add(aboutLabel, BorderLayout.WEST);
        contentPane.add(thankLabel, BorderLayout.EAST);


        this.add(titlePane, BorderLayout.NORTH);
        this.add(contentPane, BorderLayout.CENTER);
    }
}
