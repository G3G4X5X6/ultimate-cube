package com.g3g4x5x6.panel.session;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.formdev.flatlaf.icons.FlatTreeLeafIcon;
import com.g3g4x5x6.remote.NewTabbedPane;
import com.g3g4x5x6.remote.ssh.panel.NewSshPane;
import com.g3g4x5x6.remote.utils.CommonUtil;
import com.g3g4x5x6.remote.utils.SshUtil;
import com.g3g4x5x6.remote.utils.VaultUtil;
import com.g3g4x5x6.remote.utils.session.SessionOpenTool;
import com.g3g4x5x6.ui.ToolBar;
import com.g3g4x5x6.utils.DialogUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import static com.g3g4x5x6.MainFrame.mainTabbedPane;

@Slf4j
public class NewSessionManagerPanel extends JPanel {
    private final ToolBar toolBar = new ToolBar();

    private JTable sessionTable;
    private DefaultTableModel tableModel;
    private final String[] columnNames = {"会话名称", "协议", "地址", "端口", "登录用户", "认证类型", "会话保存路径"}; // 添加<创建时间>
    private TableRowSorter<DefaultTableModel> sorter;

    private HashMap<String, ArrayList<JSONObject>> protocolsMap = new HashMap<>();
    private final String defaultCategory = "ALL";

    private final JMenuItem refreshTableItem = new JMenuItem("刷新列表");
    private final JMenuItem addSessionItem = new JMenuItem("新增会话");
    private final JMenuItem delSessionItem = new JMenuItem("删除会话");
    private final JMenuItem openSessionItem = new JMenuItem("打开会话");
    private final JMenuItem testSessionItem = new JMenuItem("测试会话");
    private final JMenuItem editSessionItem = new JMenuItem("编辑会话");
    private final JMenuItem copySessionItem = new JMenuItem("复制会话");
    private final JMenuItem copyPassItem = new JMenuItem("复制密码");
    private final JMenuItem copyPathItem = new JMenuItem("复制会话文件绝对路径");

    private final JButton refreshTableBtn = new JButton(new FlatSVGIcon("icons/refresh.svg"));
    private final JToggleButton selectSshBtn = new JToggleButton("SSH");
    private final JToggleButton selectRdpBtn = new JToggleButton("RDP");
    private final JToggleButton selectVncBtn = new JToggleButton("VNC");
    private final JToggleButton selectTelnetBtn = new JToggleButton("Telnet");
    private final JComboBox<String> categoryComboBox = new JComboBox<>();
    private final JTextField searchField = new JTextField();


    /**
     * TODO 已展开的节点添加目录时无法添加节点。
     */
    public NewSessionManagerPanel() {
        this.setLayout(new BorderLayout());

        // 初始化工具栏
        initToolBar();

        // 初始化会话列表
        initTable();

        // 初始化右键动作菜单
        initPopupMenuItem();
    }

    private void initToolBar() {
        selectSshBtn.setSelected(true);
        selectRdpBtn.setSelected(true);
        selectVncBtn.setSelected(true);
        selectTelnetBtn.setSelected(true);

        categoryComboBox.addItem("ALL");
        categoryComboBox.setSelectedItem("ALL");

        // 事件监听
        refreshTableBtn.addActionListener((event) -> filterAction());
        selectSshBtn.addActionListener((event) -> filterAction());
        selectRdpBtn.addActionListener((event) -> filterAction());
        selectVncBtn.addActionListener((event) -> filterAction());
        selectTelnetBtn.addActionListener((event) -> filterAction());
        categoryComboBox.addActionListener((event) -> filterAction());

        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search, Enter");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, new FlatSearchIcon());
        searchField.registerKeyboardAction(e -> {
            String searchKeyWord = searchField.getText().strip();
            sorter.setRowFilter(RowFilter.regexFilter(searchKeyWord));
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);

        toolBar.add(selectSshBtn);
        toolBar.add(selectRdpBtn);
        toolBar.add(selectVncBtn);
        toolBar.add(selectTelnetBtn);
        toolBar.addSeparator();
        toolBar.add(categoryComboBox);
        toolBar.addSeparator();
        toolBar.add(searchField);
        toolBar.addSeparator();
        toolBar.add(Box.createGlue());
        toolBar.add(refreshTableBtn);

        this.add(toolBar, BorderLayout.NORTH);
    }


    private void initTable() {
        sessionTable = new JTable();
        sessionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSession();
                }
            }
        });
        tableModel = new DefaultTableModel() {
            // 不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.setColumnIdentifiers(columnNames);

        sessionTable.setModel(tableModel);

        // 初始化数据
        resetTableModelData();
        resetComboBoxList();

        // 移除并隐藏
        TableColumn idColumn = sessionTable.getColumnModel().getColumn(6);
        sessionTable.removeColumn(idColumn);

        // 排序
        sorter = new TableRowSorter<>(tableModel);
        sessionTable.setRowSorter(sorter);

        JScrollPane tableScrollPanel = new JScrollPane(sessionTable);
        tableScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 设置列宽
        TableColumn sessionNameColumn = sessionTable.getColumnModel().getColumn(0);
        sessionNameColumn.setPreferredWidth(150); // 设置第一列的宽度
        sessionNameColumn.setMinWidth(150);

        DefaultTableCellRenderer sessionNameRenderer = new DefaultTableCellRenderer();
        sessionNameRenderer.setIcon(new FlatTreeLeafIcon());
        sessionTable.getColumn("会话名称").setCellRenderer(sessionNameRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JTextField.CENTER);
        sessionTable.getColumn("协议").setCellRenderer(centerRenderer);
//        sessionTable.getColumn("地址").setCellRenderer(centerRenderer);
        sessionTable.getColumn("端口").setCellRenderer(centerRenderer);
        sessionTable.getColumn("登录用户").setCellRenderer(centerRenderer);
        sessionTable.getColumn("认证类型").setCellRenderer(centerRenderer);

        this.add(tableScrollPanel, BorderLayout.CENTER);
    }

    /**
     * 初始化右键菜单
     */
    private void initPopupMenuItem() {
        AbstractAction refreshAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("刷新");
                filterAction();
            }
        };

        AbstractAction addSession = new AbstractAction("新增会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("新增会话");

                mainTabbedPane.insertTab("新建会话", new FlatSVGIcon("icons/addToDictionary.svg"), new NewTabbedPane(mainTabbedPane), "新建会话", mainTabbedPane.getTabCount());
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            }
        };

        AbstractAction delSession = new AbstractAction("删除会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 支持批量删除(多选)
                int[] indexes = sessionTable.getSelectedRows();
                int bool = JOptionPane.showConfirmDialog(NewSessionManagerPanel.this, "是否确认删除会话", "删除会话", JOptionPane.YES_NO_OPTION);
                if (bool == 0) {
                    for (int index : indexes) {
                        File file = new File(Objects.requireNonNull(getSessionObject(index)).getString("sessionFilePath"));
                        if (file.delete()) {
                            // 移除出列表
                            tableModel.removeRow(sessionTable.getSelectedRow());
                        } else {
                            DialogUtil.warn("删除会话失败");
                        }
                    }
                }
            }
        };

        AbstractAction openSession = new AbstractAction("打开会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 默认打开 SSH 会话, 未来实现会话自动类型鉴别
                openSession();
            }
        };

        AbstractAction testSession = new AbstractAction("测试会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = "";
                String port = "";
                int[] indices = sessionTable.getSelectedRows();
                for (int index : indices) {
                    host = (String) tableModel.getValueAt(index, 2);
                    port = (String) tableModel.getValueAt(index, 3);

                    if (SshUtil.testConnection(host, port) == 1) {
                        DialogUtil.info("连接成功: SSH://" + host + ":" + port);
                    } else {
                        DialogUtil.warn("连接失败: SSH://" + host + ":" + port);
                    }
                }
            }
        };

        AbstractAction editSession = new AbstractAction("编辑会话") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] indexes = sessionTable.getSelectedRows();
                for (int index : indexes) {
                    JSONObject jsonObject = getSessionObject(index);

                    new Thread(() -> {
                        NewSshPane sshPane = new NewSshPane(mainTabbedPane);
                        sshPane.setHostField(jsonObject.getString("sessionAddress"));
                        sshPane.setPortField(jsonObject.getString("sessionPort"));
                        sshPane.setUserField(jsonObject.getString("sessionUser"));
                        sshPane.setPassField(VaultUtil.decryptPasswd(jsonObject.getString("sessionPass")));
                        sshPane.setPukKey(jsonObject.getString("sessionPukKey"));
                        sshPane.setSessionName(jsonObject.getString("sessionName"));
                        sshPane.setCommentText(jsonObject.getString("sessionComment"));
                        sshPane.setAuthType(jsonObject.getString("sessionLoginType"));
                        sshPane.setCategory(jsonObject.getString("sessionCategory"));
                        sshPane.setEditPath(jsonObject.getString("sessionFilePath"));
                        mainTabbedPane.insertTab("编辑选项卡", new FlatSVGIcon("icons/addToDictionary.svg"), sshPane, "编辑会话", mainTabbedPane.getTabCount());
                        mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
                    }).start();
                }
            }
        };

        AbstractAction copyPassAction = new AbstractAction("复制密码") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JSONObject jsonObject = getSessionObject(sessionTable.getSelectedRow());
                CommonUtil.setClipboardText(VaultUtil.decryptPasswd(Objects.requireNonNull(jsonObject).getString("sessionPass")));
            }
        };

        AbstractAction copyPathAction = new AbstractAction("复制会话文件绝对路径") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject jsonObject = getSelectedRowJSONObject();
                CommonUtil.setClipboardText(Objects.requireNonNull(jsonObject).getString("sessionFilePath"));
            }
        };

        refreshTableItem.addActionListener(refreshAction);
        openSessionItem.addActionListener(openSession);
        testSessionItem.addActionListener(testSession);
        editSessionItem.addActionListener(editSession);
        addSessionItem.addActionListener(addSession);
        delSessionItem.addActionListener(delSession);
        copyPassItem.addActionListener(copyPassAction);
        copyPathItem.addActionListener(copyPathAction);

        JPopupMenu popupMenu = createPopupMenu();
        sessionTable.setComponentPopupMenu(popupMenu);
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        popupMenu.add(refreshTableItem);
        popupMenu.addSeparator();
        popupMenu.add(openSessionItem);
        popupMenu.add(testSessionItem);
        popupMenu.add(editSessionItem);
        popupMenu.add(copySessionItem);
        popupMenu.addSeparator();
        popupMenu.add(addSessionItem);
        popupMenu.add(delSessionItem);
        popupMenu.addSeparator();
        popupMenu.add(copyPassItem);
        popupMenu.add(copyPathItem);

        return popupMenu;
    }

    private void openSession() {
        int[] indexes = sessionTable.getSelectedRows();
        for (int index : indexes) {
            SessionOpenTool.OpenSessionByProtocol(Objects.requireNonNull(getSessionObject(index)).getString("sessionFilePath"), (String) tableModel.getValueAt(index, 1));
        }
    }

    private JSONObject getSessionObject(int index) {
        String protocol = (String) tableModel.getValueAt(index, 1);
        String address = (String) tableModel.getValueAt(index, 2);
        String port = (String) tableModel.getValueAt(index, 3);
        String user = (String) tableModel.getValueAt(index, 4);
        String authType = (String) tableModel.getValueAt(index, 5);

        HashMap<String, ArrayList<JSONObject>> protocolsMap = SessionFileUtil.getProtocolsMap();
        for (JSONObject jsonObject : protocolsMap.get(protocol)) {
            if (jsonObject.getString("sessionAddress").equals(address) && jsonObject.getString("sessionPort").equals(port) && jsonObject.getString("sessionUser").equals(user) && jsonObject.getString("sessionLoginType").equals(authType)) {
                return jsonObject;
            }
        }
        return null;
    }

    private JSONObject getSelectedRowJSONObject() {
        int index = sessionTable.getSelectedRow();
        return getSessionObject(index);
    }

    private ArrayList<String> getSelectedCategories() {
        ArrayList<String> selectedCategories = new ArrayList<>();
        if (selectSshBtn.isSelected()) selectedCategories.add("SSH");
        if (selectRdpBtn.isSelected()) selectedCategories.add("RDP");
        if (selectVncBtn.isSelected()) selectedCategories.add("VNC");
        if (selectTelnetBtn.isSelected()) selectedCategories.add("Telnet");

        return selectedCategories;
    }

    private ArrayList<JSONObject> getSessionInfoLists() {
        ArrayList<JSONObject> sessionInfoList = new ArrayList<>();

        protocolsMap = SessionFileUtil.getProtocolsMap();
        for (String category : getSelectedCategories()) {
            if (protocolsMap.get(category) != null) sessionInfoList.addAll(protocolsMap.get(category));
        }

        Object category = categoryComboBox.getSelectedItem();
        if (category != null && category.equals(defaultCategory)) {
            return sessionInfoList;
        } else {
            for (int i = sessionInfoList.size() - 1; i >= 0; i--) {
                JSONObject jsonObject = sessionInfoList.get(i);
                if (jsonObject != null && jsonObject.getString("sessionCategory") != null && !jsonObject.getString("sessionCategory").equals(category)) {
                    sessionInfoList.remove(i);
                }
            }
        }

        return sessionInfoList;
    }

    private void resetTableModelData() {
        tableModel.setRowCount(0);

        ArrayList<JSONObject> sessionInfoList = getSessionInfoLists();
        for (JSONObject jsonObject : sessionInfoList) {
            tableModel.addRow(new String[]{jsonObject.getString("sessionName"), jsonObject.getString("sessionProtocol"), jsonObject.getString("sessionAddress"), jsonObject.getString("sessionPort"), jsonObject.getString("sessionUser"), jsonObject.getString("sessionLoginType"), jsonObject.getString("sessionFilePath")});
        }
    }

    private ArrayList<String> getSessionCategoryLists() {
        ArrayList<JSONObject> sessionInfoList = new ArrayList<>();

        protocolsMap = SessionFileUtil.getProtocolsMap();
        for (String category : getSelectedCategories()) {
            if (protocolsMap.get(category) != null) sessionInfoList.addAll(protocolsMap.get(category));
        }

        Set<String> categorySet = new HashSet<>(); // 使用HashSet去重

        for (JSONObject jsonObject : sessionInfoList) {
            if (jsonObject.get("sessionCategory") != null) { // 确保键存在
                categorySet.add(jsonObject.getString("sessionCategory"));
            }
        }

        return new ArrayList<>(categorySet); // 将HashSet转换为List

    }

    private void resetComboBoxList() {
        Object currentCategory = categoryComboBox.getSelectedItem();
        categoryComboBox.removeAllItems();
        categoryComboBox.addItem(defaultCategory);

        ArrayList<String> categoryList = getSessionCategoryLists();
        for (String category : categoryList) {
            categoryComboBox.addItem(category);
        }
        categoryComboBox.setSelectedItem(Objects.requireNonNullElse(currentCategory, defaultCategory));
    }

    private void filterAction() {
        resetTableModelData();
        resetComboBoxList();
    }

}
