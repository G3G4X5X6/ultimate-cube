package com.g3g4x5x6.ui.panels.focus;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.ui.panels.NewTabbedPane;
import com.g3g4x5x6.ui.panels.SessionsManager;
import com.g3g4x5x6.ui.panels.ssh.SshTabbedPane;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.BiConsumer;

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
    private final LinkedList<SshTabbedPane> sshTabbedPanes = new LinkedList<>();

    public FocusFrame() {
        this.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("icon.png"))).getImage());
        this.setUndecorated(true);                      //去处边框
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);   //最大化
        this.setAlwaysOnTop(true);                      //总在最前面
        this.setResizable(false);                       //不能改变大小
        this.setLayout(new BorderLayout());
        this.add(tabbedPane, BorderLayout.CENTER);

        initClosableTabs();
        customComponents();
        initTabbedPane();
    }

    private void initClosableTabs() {
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK,
                (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> tabbedPane.removeTabAt(tabIndex));

    }

    private void customComponents() {
        JToolBar trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);

        JButton addBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/add.svg"));
        addBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.insertTab("新建选项卡", new FlatSVGIcon("com/g3g4x5x6/ui/icons/addToDictionary.svg"), new NewTabbedPane(tabbedPane), "新建选项卡", tabbedPane.getTabCount());
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            }
        });
        // swiftPackage.svg
        JButton sessionManagerBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/swiftPackage.svg"));
        sessionManagerBtn.setToolTipText("会话管理面板");
        sessionManagerBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.insertTab("会话管理", new FlatSVGIcon("com/g3g4x5x6/ui/icons/addList.svg"), new SessionsManager(tabbedPane), "会话管理", tabbedPane.getTabCount());
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            }
        });

        JButton fullScreenBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/fitContent.svg"));
        fullScreenBtn.setToolTipText("专注模式");
        fullScreenBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("退出专注模式");
                // TODO 还原会话

                dispose();
            }
        });

        trailing.add(addBtn);
        trailing.add(sessionManagerBtn);
        trailing.add(Box.createHorizontalGlue());
        trailing.add(fullScreenBtn);
        tabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }


    private void initTabbedPane() {
        getSshTabbedPanes();
        for (SshTabbedPane sshTabbedPane : sshTabbedPanes) {
            tabbedPane.addTab(sshTabbedPane.getTitle(),
                    new FlatSVGIcon("com/g3g4x5x6/ui/icons/OpenTerminal_13x13.svg"),
                    new FocusPanel(
                            sshTabbedPane.getTitle(),
                            sshTabbedPane.getSshPane(),
                            sshTabbedPane.getSftpBrowser(),
                            sshTabbedPane.getMonitorPane(),
                            sshTabbedPane.getEditorPane()
                    ));
        }
    }

    private void getSshTabbedPanes() {
        for (Component component : MainFrame.mainTabbedPane.getComponents()) {
            if (component instanceof SshTabbedPane) {
                sshTabbedPanes.add((SshTabbedPane) component);
                MainFrame.mainTabbedPane.remove(component);
            }
        }
        log.debug("sshTabbedPanes.size(): " + sshTabbedPanes.size());
    }

}
