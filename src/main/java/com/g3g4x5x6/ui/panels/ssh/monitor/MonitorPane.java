package com.g3g4x5x6.ui.panels.ssh.monitor;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.g3g4x5x6.utils.SshUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.session.ClientSession;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


@Slf4j
public class MonitorPane extends JPanel {

    private JToolBar toolBar;
    private JTextField searchField = new JTextField();
    private TableRowSorter<DefaultTableModel> sorter;

    private JScrollPane scrollPane;
    private JTable table;
    private DefaultTableModel tableModel;
    private String[] columnNames = {"USER", "PID", "%CPU", "%MEM", "VSZ", "RSS", "TTY", "STAT", "START", "TIME", "COMMAND"};

    private ClientSession session;
    private String host;
    private int port;
    private String user;
    private String pass;

    public MonitorPane() {
        this.setLayout(new BorderLayout());

        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        initToolBarAction();

        table = new JTable();
        tableModel = new DefaultTableModel() {
            // 不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
//                if (column == 0) {
//                    return false;
//                }
                return true;
            }
        };

        table.setModel(tableModel);
        tableModel.setColumnIdentifiers(columnNames);
        table.getColumn("USER").setMaxWidth(70);
        table.getColumn("PID").setMaxWidth(70);
        table.getColumn("%CPU").setMaxWidth(70);
        table.getColumn("%MEM").setMaxWidth(70);
        table.getColumn("VSZ").setMaxWidth(70);
        table.getColumn("RSS").setMaxWidth(70);
        table.getColumn("TTY").setMaxWidth(70);
        table.getColumn("STAT").setMaxWidth(70);
        table.getColumn("START").setMaxWidth(70);
        table.getColumn("TIME").setMaxWidth(70);
        // 搜索功能
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer iconRenderer = new DefaultTableCellRenderer();
        iconRenderer.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/intersystemCache.svg"));

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JTextField.LEFT);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JTextField.RIGHT);

        table.getColumn("USER").setCellRenderer(leftRenderer);
        table.getColumn("PID").setCellRenderer(rightRenderer);

        this.add(scrollPane, BorderLayout.CENTER);
        this.add(toolBar, BorderLayout.NORTH);
    }

    @Deprecated
    public MonitorPane(String host, int port, String user, String pass) {
        this();

        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public MonitorPane(ClientSession session) {
        this();
        this.session = session;

        new Thread(() -> {
            while (true) {
                try {
                    flushWinTable();
                    Thread.sleep(1000 * 60 * 10);
                    log.debug("刷新系统进程信息");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initToolBarAction() {
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search, Enter");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, new FlatSearchIcon());
        searchField.registerKeyboardAction(e -> {
                    String searchKeyWord = searchField.getText().strip();
                    sorter.setRowFilter(RowFilter.regexFilter(searchKeyWord));
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
        toolBar.add(searchField);

        JButton freshBtn = new JButton();
        freshBtn.setToolTipText("十分钟自动刷新");
        freshBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/refresh.svg"));
        freshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Refresh");
                flushWinTable();
            }
        });

        JButton unameBtn = new JButton("uname");
        unameBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = SshUtil.exec(session, "uname -a").strip();
                ShowEditorPane showPane = new ShowEditorPane();
                showPane.setTitle("uname -a");
                showPane.setSize(new Dimension(600, 100));
                showPane.setText(text);
                showPane.setLocationRelativeTo(MonitorPane.this);
            }
        });

        toolBar.addSeparator();
        toolBar.add(freshBtn);
        toolBar.add(unameBtn);
    }

    private void flushWinTable() {
        log.debug("flushWinTable");
        try {
            boolean flag = true;
            tableModel.setRowCount(0);
            for (String line : SshUtil.exec(session, "ps aux").split("\n")) {
                if (flag) {
                    flag = false;
                    continue;
                }
                String[] row = new String[11];
                int i = 0;
                for (String column : line.split("\\s+")) {
                    if (i >= 10) {
                        if (i == 10)
                            row[10] = "";
                        row[10] += column + " ";
                    } else {
                        row[i] = column;
                    }
                    i++;
                }
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClientSession getSession() {
        return session;
    }

    public void setSession(ClientSession session) {
        this.session = session;
    }
}
