package com.g3g4x5x6.settings;

import com.g3g4x5x6.App;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Objects;


@Slf4j
public class TerminalPanel extends JPanel implements SettingsInterface {

    private final FlowLayout leftFlowLayout = new FlowLayout();
    private final Box vBox = Box.createVerticalBox();
    private JPanel panel = new JPanel();
    private JComboBox<String> colorSchemeComboBox;
    private LinkedList<String> colorSchemeList = new LinkedList<>();

    public TerminalPanel() {
        leftFlowLayout.setAlignment(FlowLayout.LEFT);

        panel.setLayout(new BorderLayout());
        panel.add(vBox);
        panel.setBorder(null);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        initPanel();

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }


    private void initPanel() {
        /**
         * 主题配置
         */
        JPanel colorSchemePanel = new JPanel();
        colorSchemePanel.setLayout(leftFlowLayout);

        colorSchemeComboBox = new JComboBox<>();
        colorSchemeComboBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug(Objects.requireNonNull(colorSchemeComboBox.getSelectedItem()).toString());
                // TODO ColorScheme 预览效果
                refreshTheme();
            }
        });

        colorSchemePanel.add(new JLabel("配色方案:"));
        colorSchemePanel.add(colorSchemeComboBox);
        initColorSchemeList();
        vBox.add(colorSchemePanel);

        /**
         * what?
         */
        JPanel whatPanel = new JPanel();
        whatPanel.setLayout(leftFlowLayout);
        // TODO 下一个配置项
        vBox.add(whatPanel);
    }

    @SneakyThrows
    private void initColorSchemeList() {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("theme/colorschemes.txt"))));
        for (Object line : reader.lines().toArray()) {
            String scheme = ((String) line).strip();
            colorSchemeList.add(scheme);
            colorSchemeComboBox.addItem(scheme);
            if (App.properties.getProperty("terminal.color.scheme").equalsIgnoreCase(scheme)) {
                colorSchemeComboBox.setSelectedIndex(colorSchemeComboBox.getItemCount() - 1);
            }
        }
        log.debug(colorSchemeList.toString());
    }

    private void refreshTheme() {
        // TODO 实时预览终端配色变化
    }

    @Override
    public void save() {
        App.properties.setProperty("terminal.color.scheme", (String) colorSchemeComboBox.getSelectedItem());
    }
}
