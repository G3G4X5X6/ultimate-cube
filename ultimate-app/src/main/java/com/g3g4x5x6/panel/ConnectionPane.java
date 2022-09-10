package com.g3g4x5x6.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatToggleButton;
import com.g3g4x5x6.utils.AppConfig;
import com.g3g4x5x6.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


@Slf4j
public class ConnectionPane extends JPanel {
    private BorderLayout borderLayout = new BorderLayout();
    private FlowLayout flowLayout = new FlowLayout();

    private JToolBar toolBar;
    private FlatToggleButton netBtn;

    private JScrollPane scrollPane;
    private JTable table;
    private DefaultTableModel tableModel;
    private String[] columnNames = {"Proto", "Local Address", "Foreign Address", "State", "PID"};

    public ConnectionPane() {
        this.setLayout(borderLayout);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        netBtn = new FlatToggleButton();
        netBtn.setIcon(new FlatSVGIcon("icons/activity.svg"));
        netBtn.setSelected(true);
        netBtn.addActionListener(new ActionListener() {
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

        FlatButton freshBtn = new FlatButton();
        freshBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        freshBtn.setIcon(new FlatSVGIcon("icons/refresh.svg"));
        freshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> flushWinTable()).start();
            }
        });

        toolBar.add(netBtn);
        toolBar.add(Box.createHorizontalGlue());
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

        new Thread(this::flushWinTable).start();

        table.setModel(tableModel);
        tableModel.setColumnIdentifiers(columnNames);

        scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setIcon(new FlatSVGIcon("icons/connector.svg"));
        table.getColumn("Proto").setCellRenderer(rightRenderer);

        this.add(toolBar, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void flushWinTable() {
        tableModel.setRowCount(0);
        File temp = new File(AppConfig.getWorkPath() + "/temp");
        if (!temp.exists()) {
            temp.mkdir();
        }
        String fileName = temp.getAbsolutePath() + "/netstat.txt";
        String output = CommonUtil.exec("netstat -ano");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
            writer.write(output);
            writer.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        for (String line : output.split("\n")) {
//            log.debug(line);
            if (line.strip().startsWith("TCP")) {
                // TCP
                String[] row = line.strip().split("\\s+");
                String ip = row[2].split(":")[0];
                if (!ip.strip().equals("0.0.0.0") && !ip.strip().equals("*") && !ip.strip().startsWith("[") && !ip.strip().equals("127.0.0.1")) {
                    String ipAndInfo = ip + "(" + CommonUtil.queryIp(ip) + ")";
                    row[2] = ipAndInfo;
                } else if (netBtn.isSelected()) {
//                    log.debug("TCP ALL: continue");
                    continue;
                }
                if (ip.strip().equals("127.0.0.1")) {
                    String ipAndInfo = ip + "(本机地址,  CZ88.NET)";
                    row[2] = ipAndInfo;
                }
                tableModel.addRow(row);
            }
            if (line.strip().startsWith("UDP")) {
                // UDP
                String[] tmpRow = line.strip().split("\\s+");
                String[] row = new String[]{tmpRow[0], tmpRow[1], tmpRow[2], "", tmpRow[3]};
                String ip = row[2].split(":")[0];
                if (!ip.strip().equals("0.0.0.0") && !ip.strip().equals("*") && !ip.strip().startsWith("[") && netBtn.isSelected()) {
                    String ipAndInfo = ip + "(" + CommonUtil.queryIp(ip) + ")";
                    row[2] = ipAndInfo;
//                    log.debug("UDP: " + ipAndInfo);
                } else {
                    continue;
                }
                tableModel.addRow(row);
            }
        }
    }
}
