package com.g3g4x5x6.ui.embed.nuclei.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ui.embed.nuclei.panel.connector.ConsolePanel;
import com.g3g4x5x6.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;

import static com.formdev.flatlaf.FlatClientProperties.*;


@Slf4j
public class RunningPanel extends JPanel {
    public static String nucleiPath = ConfigUtil.getWorkPath() + "/tools/xpack_tools/nuclei/";
    public static JTabbedPane tabbedPane;
    private int count = 0;

    public RunningPanel() {
        this.setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("Default", new ConsolePanel());
        initClosableTabs(tabbedPane);
        customComponents();

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    private void initClosableTabs(JTabbedPane tabbedPane) {
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_CALLBACK,
                (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
                    if (tabIndex >= 1) {
                        tabbedPane.removeTabAt(tabIndex);
                    }
                });
    }

    private void customComponents() {
        JToolBar trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);

        JButton addBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/add.svg"));
        addBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                count++;
                tabbedPane.addTab("#" + String.valueOf(count), new ConsolePanel());
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            }
        });

        JButton refreshBtn = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/refresh.svg"));
        refreshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConsolePanel consolePanel = (ConsolePanel) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
                consolePanel.refreshTerminal();
            }
        });

        trailing.add(addBtn);
        trailing.add(refreshBtn);
        trailing.add(Box.createHorizontalGlue());
        tabbedPane.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }
}
