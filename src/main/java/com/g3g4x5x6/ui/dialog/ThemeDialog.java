package com.g3g4x5x6.ui.dialog;

import com.g3g4x5x6.App;
import com.g3g4x5x6.utils.ConfigUtil;
import com.g3g4x5x6.utils.DbUtil;
import com.g3g4x5x6.utils.DialogUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


@Slf4j
public class ThemeDialog extends JDialog {

    private JCheckBox enableCheckBox;
    private JCheckBox enableTerminalCheckBox;
    private JTable themeTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = {"ID", "主题"};

    private JButton saveButton;
    private JButton closeButton;

    public ThemeDialog() {
        super(App.mainFrame);
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(400, 700));
        this.setSize(new Dimension(400, 700));
        this.setLocationRelativeTo(App.mainFrame);
        this.setModal(true);
        this.setTitle("主题选择器");

        initEnableOption();

        initThemeListTable();

        initControlButton();

        themeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    setTerminalColor();
                }
            }
        });
    }

    private void initEnableOption() {
        // TODO Enable Option
        JPanel enablePanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        enablePanel.setLayout(flowLayout);
        enableCheckBox = new JCheckBox("启用主题插件");
        enablePanel.add(enableCheckBox);
        if (ConfigUtil.isEnableTheme()) {
            enableCheckBox.setSelected(true);
        }
        JLabel tips = new JLabel("下次启动生效");
        tips.setEnabled(false);
        enablePanel.add(tips);

        enableTerminalCheckBox = new JCheckBox("启用终端自定义配色");
        if (ConfigUtil.boolSettingValue("terminal_color_enable")){
            enableTerminalCheckBox.setSelected(true);
        }
        enablePanel.add(enableTerminalCheckBox);
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

        initTable();

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

    private void initTable() {
        // 获取主题数据
        tableModel.setRowCount(0);
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
            DbUtil.close(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        themeTable.setModel(tableModel);
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

        saveButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 启用主题皮肤, theme_enable
                if (enableCheckBox.isSelected()) {
                    ConfigUtil.updateThemeEnableOption("1");
                } else {
                    ConfigUtil.updateThemeEnableOption("0");
                }

                // 启用自定义终端配色, terminal_color_enable
                if (enableTerminalCheckBox.isSelected()) {
                    ConfigUtil.updateSetting("terminal_color_enable", "1");
                } else {
                    ConfigUtil.updateSetting("terminal_color_enable", "0");
                }

                int row = themeTable.getSelectedRow();
                if (row != -1) {
                    String themeId = (String) tableModel.getValueAt(row, 0);
                    log.debug("Selected theme: " + themeId);
                    ConfigUtil.updateThemeOption(themeId);
                } else {
//                    DialogUtil.info("配置已保存");
                    dispose();
                }
                initTable();
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
            DbUtil.close(statement, resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return themeName;
    }

    private String getSelectedTheme() {
        int index = themeTable.getSelectedRow();
        return (String) themeTable.getValueAt(index, 1);
    }

    private String getSelectedThemeId() {
        int index = themeTable.getSelectedRow();
        return (String) themeTable.getValueAt(index, 0);
    }

    private void setTerminalColor() {
        ColorDialog colorDialog = new ColorDialog(App.mainFrame);
        colorDialog.setVisible(true);
    }

    private class ColorDialog extends JDialog {
        private JPanel color;
        private JPanel btnPane;
        private JFormattedTextField foreground;
        private JFormattedTextField background;
        private String tips = "<html><strong>Theme: <font color='red'>" + getSelectedTheme() + "</font></strong</html>";

        public ColorDialog(JFrame frame) {
            super(frame);
            this.setLayout(new BorderLayout());
            this.setSize(new Dimension(400, 120));
            this.setModal(true);
            this.setLocationRelativeTo(frame);
            this.setTitle(tips);
            this.setResizable(false);

            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);

            foreground = new JFormattedTextField();
            foreground.setColumns(7);
            foreground.setValue(getTerminalColor()[0]);
            background = new JFormattedTextField();
            background.setColumns(7);
            background.setValue(getTerminalColor()[1]);
            JPanel forePane = new JPanel();
            forePane.add(new JLabel("Foreground"));
            forePane.add(foreground);
            JPanel backPane = new JPanel();
            backPane.add(new JLabel("Background"));
            backPane.add(background);
            color = new JPanel();
            color.setLayout(flowLayout);
            color.add(forePane);
            color.add(backPane);

            btnPane = new JPanel();
            btnPane.setLayout(flowLayout);
            JButton saveBtn = new JButton("保存");
            JButton cancelBtn = new JButton("关闭");
            saveBtn.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log.debug("保存终端配色");
                    if (isExistTerminalColor()){
                        // 更新配色
                        updateTerminalColor(getSelectedThemeId());
                    }else{
                        // 新增自定义配色
                        addTerminalColor(getSelectedThemeId());
                    }
                    DialogUtil.info("终端配色已更新");
                    dispose();
                }
            });
            cancelBtn.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            btnPane.add(saveBtn);
            btnPane.add(cancelBtn);

            this.add(color, BorderLayout.CENTER);
            this.add(btnPane, BorderLayout.SOUTH);
        }

        private void addTerminalColor(String themId){
            String foregroundColor = foreground.getText();
            String backgroundColor = background.getText();
            try {
                Connection connection = DbUtil.getConnection();
                Statement statement = connection.createStatement();
                int row = statement.executeUpdate("INSERT INTO terminal_color VALUES(null, '" +
                        foregroundColor + "', '" + backgroundColor + "', " + getSelectedThemeId() + ")");
                DbUtil.close(statement);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        private void updateTerminalColor(String themId){
            String foregroundColor = foreground.getText();
            String backgroundColor = background.getText();
            try {
                Connection connection = DbUtil.getConnection();
                Statement statement = connection.createStatement();
                String sql = "UPDATE terminal_color SET foreground='" + foregroundColor +
                        "', background='" + backgroundColor +
                        "' WHERE theme = " + themId;
                log.debug(sql);
                int row = statement.executeUpdate(sql);

                DbUtil.close(statement);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        private Boolean isExistTerminalColor(){
            Boolean flag = false;
            try {
                Connection connection = DbUtil.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * From terminal_color WHERE theme = " + getSelectedThemeId());

                while (resultSet.next()) {
                    flag = true;
                }

                DbUtil.close(statement, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return flag;
        }

        private String[] getTerminalColor() {
            String[] result = {"255,255,255", "0,0,0"};
            try {
                Connection connection = DbUtil.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * From terminal_color WHERE theme = " + getSelectedThemeId());

                while (resultSet.next()) {
                    result[0] = resultSet.getString("foreground");
                    result[1] = resultSet.getString("background");
                }

                DbUtil.close(statement, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return result;
        }
    }
}
