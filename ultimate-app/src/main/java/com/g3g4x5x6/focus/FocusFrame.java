package com.g3g4x5x6.focus;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatToggleButton;
import com.g3g4x5x6.App;
import com.g3g4x5x6.MainFrame;
import com.g3g4x5x6.ssh.SessionInfo;
import com.g3g4x5x6.ssh.panel.SshTabbedPane;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

import static com.formdev.flatlaf.FlatClientProperties.*;

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
        this.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.jpg"))).getImage());
        this.setUndecorated(true);                      //去处边框
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);   //最大化
        this.setAlwaysOnTop(true);                      //总在最前面
        this.setResizable(false);                       //不能改变大小
        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);

        removeOld(); // Remove old tab pane from MainFrame
//        initClosableTabs();
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

//    private void initClosableTabs() {
//        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
//        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
//        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK,
//                (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
//                    if (tabbedPane.getComponentAt(tabIndex) instanceof FocusPanel) {
//                        FocusPanel focusPanel = (FocusPanel) tabbedPane.getComponentAt(tabIndex);
//                        focusPanel.getSessionInfo().close();
//                        App.sessionInfos.remove(focusPanel.getSessionInfo().getSessionId());
//                        log.debug(String.valueOf(App.sessionInfos.size()));
//                    }
//                    tabbedPane.removeTabAt(tabIndex);
//                });
//    }

    private void customComponents() {
        JToolBar trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);

//        JButton addBtn = new JButton(new FlatSVGIcon("icons/add.svg"));
//        addBtn.addActionListener(new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                tabbedPane.insertTab("新建选项卡", new FlatSVGIcon("icons/addToDictionary.svg"), new NewTabbedPane(tabbedPane), "新建选项卡", tabbedPane.getTabCount());
//                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
//            }
//        });
//        // swiftPackage.svg
//        JButton sessionManagerBtn = new JButton(new FlatSVGIcon("icons/swiftPackage.svg"));
//        sessionManagerBtn.setToolTipText("会话管理面板");
//        sessionManagerBtn.addActionListener(new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                tabbedPane.insertTab("会话管理", new FlatSVGIcon("icons/addList.svg"), new SessionManagerPanel(tabbedPane), "会话管理", tabbedPane.getTabCount());
//                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
//            }
//        });

        JButton fullScreenBtn = new JButton(new FlatSVGIcon("icons/fitContent.svg"));
        fullScreenBtn.setToolTipText("专注模式");
        fullScreenBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("退出专注模式");
                // TODO 还原会话
                for (String key : App.sessionInfos.keySet()) {
                    MainFrame.mainTabbedPane.addTab(
                            App.sessionInfos.get(key).getSessionName().equals("") ? App.sessionInfos.get(key).getSessionAddress() : App.sessionInfos.get(key).getSessionName(),
                            new FlatSVGIcon("icons/OpenTerminal_13x13.svg"),
                            new SshTabbedPane(App.sessionInfos.get(key))
                    );
                }
                MainFrame.mainTabbedPane.setSelectedIndex(MainFrame.mainTabbedPane.getTabCount() - (tabbedPane.getTabCount() - tabbedPane.getSelectedIndex()));
                dispose();
                log.debug("退出专注模式, over!");
            }
        });

        // 置顶图标按钮
        FlatToggleButton toggleButton = new FlatToggleButton();
        toggleButton.setIcon(new FlatSVGIcon("icons/pinTab.svg"));
        toggleButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        toggleButton.setToolTipText("窗口置顶");
        toggleButton.setFocusable(false);
        toggleButton.setSelected(true);
        toggleButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toggleButton.isSelected()) {
                    setAlwaysOnTop(true);
                    toggleButton.setToolTipText("取消置顶");
                } else {
                    setAlwaysOnTop(false);
                    toggleButton.setToolTipText("窗口置顶");
                }
            }
        });

//        trailing.add(addBtn);
//        trailing.add(sessionManagerBtn);
        trailing.add(Box.createHorizontalGlue());
        trailing.add(fullScreenBtn);
        trailing.add(toggleButton);
        tabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }


    private void initTabbedPane() {
        for (String key : App.sessionInfos.keySet()) {
            SessionInfo sessionInfo = App.sessionInfos.get(key);
            log.debug("sessionInfos.size(): " + App.sessionInfos.size() + " : " + sessionInfo.toString());

            tabbedPane.addTab(
                    sessionInfo.getSessionName().equals("") ? sessionInfo.getSessionAddress() : sessionInfo.getSessionName(),
                    new FlatSVGIcon("icons/OpenTerminal_13x13.svg"),
                    new FocusPanel(sessionInfo)
            );
        }
        tabbedPane.setSelectedIndex(MainFrame.focusIndex);
        MainFrame.focusIndex = 0;
    }

}
