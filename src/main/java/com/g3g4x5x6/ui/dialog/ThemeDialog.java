package com.g3g4x5x6.ui.dialog;

import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.DbUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


@Slf4j
public class ThemeDialog extends JDialog {

    private JCheckBox enableCheckBox;
    private JTable themeTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = {"ID", "主题"};

    private JButton saveButton;
    private JButton closeButton;

    public ThemeDialog() {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(400, 700));
        this.setSize(new Dimension(400, 700));
        this.setLocationRelativeTo(null);
        this.setModal(true);

        initEnableOption();

        initThemeListTable();

        initControlButton();
    }

    private void initEnableOption() {
        // TODO Enable Option
        JPanel enablePanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        enablePanel.setLayout(flowLayout);
        enableCheckBox = new JCheckBox("启用主题插件");
        enablePanel.add(enableCheckBox);
        if (ConfigUtil.isEnableTheme()){
            enableCheckBox.setSelected(true);
        }
        JLabel tips = new JLabel("下次启动生效");
        tips.setEnabled(false);
        enablePanel.add(tips);
        this.add(enablePanel, BorderLayout.NORTH);
    }

    private void initThemeListTable() {
        themeTable = new JTable();
        tableModel = new DefaultTableModel() {
            // 不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.setColumnIdentifiers(columnNames);

        // 获取主题数据
        int row = 0;
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * From theme");

            while (resultSet.next()) {
                tableModel.addRow(new String[]{
                        resultSet.getString("id"),
                        resultSet.getString("name").equals(getCurrentTheme()) ?
                                "<html><strong><font color='red'>" + getCurrentTheme() + "</font></strong></html>" :
                                resultSet.getString("name")
                });
                row = tableModel.getRowCount() - 1;
            }
            DbUtil.close(connection, statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        themeTable.setModel(tableModel);
        themeTable.getColumnModel().getColumn(0).setMaxWidth(50);
        themeTable.getColumnModel().getColumn(0).setMinWidth(50);

        JScrollPane tableScroll = new JScrollPane(themeTable);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        themeTable.getColumn("ID").setCellRenderer(centerRenderer);
        this.add(tableScroll, BorderLayout.CENTER);
    }

    private void initControlButton() {
        JPanel controlPane = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        controlPane.setLayout(flowLayout);
        saveButton = new JButton("保存配置");
        closeButton = new JButton("关闭窗口");
        controlPane.add(saveButton);
        controlPane.add(closeButton);
        this.add(controlPane, BorderLayout.SOUTH);

        Connection connection = null;
        Statement statement = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Statement finalStatement = statement;
        saveButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (enableCheckBox.isSelected()){
                        int result = finalStatement.executeUpdate("UPDATE settings SET value='1' WHERE key = 'theme_enable';");
                    } else {
                        int result = finalStatement.executeUpdate("UPDATE settings SET value='0' WHERE key = 'theme_enable';");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                int row = themeTable.getSelectedRow();
                if (row != -1){
                    String themeId = (String) tableModel.getValueAt(row, 0);
                    log.debug("Selected theme: " + themeId);
                    ConfigUtil.updateThemeOption(themeId);
                }
            }
        });

        closeButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private String getCurrentTheme() {
        String themeName = "";
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * From theme WHERE id = (SELECT value FROM settings WHERE key = 'theme')");
            while (resultSet.next()) {
                themeName = resultSet.getString("name");
            }
            DbUtil.close(connection, statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return themeName;
    }

}
