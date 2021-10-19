package com.g3g4x5x6.ui.panels.dashboard;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.g3g4x5x6.utils.CommonUtil;
import com.g3g4x5x6.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


@Slf4j
public class ConnectionPane extends JPanel {
    private BorderLayout borderLayout = new BorderLayout();

    private JToolBar toolBar;

    private JScrollPane scrollPane;
    private JTable table;
    private DefaultTableModel tableModel;
    private String[] columnNames = {"Proto", "Local Address", "Foreign Address", "State", "PID"};

    public ConnectionPane() {
        this.setLayout(borderLayout);
        
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        FlatButton freshBtn = new FlatButton();
        freshBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        freshBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/refresh.svg"));
        freshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        flushWinTable();
                    }
                }).start();
            }
        });

        toolBar.add(freshBtn);

        table = new JTable();
        tableModel = new DefaultTableModel() {
            // 不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return false;
                }
                return true;
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        flushWinTable();
                        Thread.sleep(1000 * 60*10);
                        log.debug("刷新网络连接");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        table.setModel(tableModel);
        tableModel.setColumnIdentifiers(columnNames);

        scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/connector.svg"));
        table.getColumn("Proto").setCellRenderer(rightRenderer);

        this.add(toolBar, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void flushWinTable() {
        tableModel.setRowCount(0);
        File temp = new File(ConfigUtil.getWorkPath() + "/temp");
        if (!temp.exists()) {
            temp.mkdir();
        }
        String fileName = temp.getAbsolutePath() + "/netstat.txt";
        String output = CommonUtil.exec("netstat -ano");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(output);
            writer.flush();
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        for (String line : output.split("\n")){
            log.debug(line);
            if (line.strip().startsWith("TCP")){
                // TCP
                String[] row = line.strip().split("\\s+");
                String ip = row[2].split(":")[0];
                if (!ip.strip().equals("0.0.0.0") && !ip.strip().equals("*") && !ip.strip().startsWith("[") && !ip.strip().equals("127.0.0.1")){
                    String ipAndInfo = ip + "(" + CommonUtil.queryIp(ip) + ")";
                    row[2] = ipAndInfo;
                }
                if (ip.strip().equals("127.0.0.1")){
                    String ipAndInfo = ip + "(本机地址,  CZ88.NET)";
                    row[2] = ipAndInfo;
                }
                tableModel.addRow(row);
            }
            if (line.strip().startsWith("UDP")){
                // UDP
                String[] tmpRow = line.strip().split("\\s+");
                String[] row = new String[]{tmpRow[0], tmpRow[1], tmpRow[2], "", tmpRow[3]};
                String ip = row[2].split(":")[0];
                if (!ip.strip().equals("0.0.0.0") && !ip.strip().equals("*") && !ip.strip().startsWith("[")){
                    String ipAndInfo = ip + "(" + CommonUtil.queryIp(ip) + ")";
                    row[2] = ipAndInfo;
                }
                tableModel.addRow(row);
            }
        }
    }
}
