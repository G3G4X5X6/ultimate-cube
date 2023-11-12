package com.g3g4x5x6.panel.focus;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.App;
import com.g3g4x5x6.MainFrame;
import com.g3g4x5x6.remote.ssh.SessionInfo;
import com.g3g4x5x6.remote.ssh.panel.SshTabbedPane;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT;

/**
 * this.setUndecorated(true);                      //去处边框
 * this.setExtendedState(JFrame.MAXIMIZED_BOTH);   //最大化
 * this.setAlwaysOnTop(true);                      //总在最前面
 * this.setResizable(false);                       //不能改变大小
 * this.setLayout(null);
 */
@Slf4j
public class FocusFrame extends JFrame {
    private final JTabbedPane tabbedPane = new JTabbedPane();

    public FocusFrame() {
        this.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.png"))).getImage());
        this.setUndecorated(true);                      //去处边框
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);   //最大化
        this.setAlwaysOnTop(true);                      //总在最前面
        this.setResizable(false);                       //不能改变大小
        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);

        removeOld();
        customComponents();
        initTabbedPane();
    }

    private void removeOld() {
        for (int i = MainFrame.mainTabbedPane.getTabCount() - 1; i >= 0; i--) {
            if (MainFrame.mainTabbedPane.getComponentAt(i) instanceof SshTabbedPane) {
                MainFrame.mainTabbedPane.removeTabAt(i);
            }
        }
    }

    private void customComponents() {
        JToolBar trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);

        JButton fullScreenBtn = new JButton(new FlatSVGIcon("icons/cwmScreenOff.svg"));
        fullScreenBtn.setToolTipText("退出专注模式");
        fullScreenBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("退出专注模式");
                // TODO 还原会话
                for (String key : App.sessionInfos.keySet()) {
                    MainFrame.mainTabbedPane.addTab(
                            App.sessionInfos.get(key).getSessionName().isEmpty() ? App.sessionInfos.get(key).getSessionAddress() : App.sessionInfos.get(key).getSessionName(),
                            new FlatSVGIcon("icons/consoleRun.svg"),
                            new SshTabbedPane(App.sessionInfos.get(key))
                    );
                }
                MainFrame.mainTabbedPane.setSelectedIndex(MainFrame.mainTabbedPane.getTabCount() - (tabbedPane.getTabCount() - tabbedPane.getSelectedIndex()));
                dispose();
                log.debug("退出专注模式, over!");
            }
        });

        trailing.add(Box.createHorizontalGlue());
        trailing.add(fullScreenBtn);
        tabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }


    private void initTabbedPane() {
        for (String key : App.sessionInfos.keySet()) {
            SessionInfo sessionInfo = App.sessionInfos.get(key);
            log.debug("sessionInfos.size(): " + App.sessionInfos.size() + " : " + sessionInfo.toString());

            tabbedPane.addTab(
                    sessionInfo.getSessionName().isEmpty() ? sessionInfo.getSessionAddress() : sessionInfo.getSessionName(),
                    new FlatSVGIcon("icons/consoleRun.svg"),
                    new FocusPanel(sessionInfo)
            );
        }
        if (tabbedPane.getTabCount() != 0){
            tabbedPane.setSelectedIndex(MainFrame.focusIndex);
            MainFrame.focusIndex = 0;
        }
    }

}
