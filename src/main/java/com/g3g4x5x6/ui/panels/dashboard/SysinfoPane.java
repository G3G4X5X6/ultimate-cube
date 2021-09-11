package com.g3g4x5x6.ui.panels.dashboard;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.g3g4x5x6.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * TODO CPU, memory, disk, system version, system update,
 */
@Slf4j
public class SysinfoPane extends JPanel {
    private BorderLayout borderLayout = new BorderLayout();

    private JToolBar toolBar;

    private JScrollPane scrollPane;
    private JTable table;
    private DefaultTableModel tableModel;
    private String[] columnNames = { "Key", "Value"};

    public SysinfoPane() {
        this.setLayout(borderLayout);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        FlatButton flushBtn = new FlatButton();
        flushBtn.setButtonType(FlatButton.ButtonType.toolBarButton);
        flushBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/buildLoadChanges.svg"));
        flushBtn.addActionListener(new AbstractAction() {
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

        toolBar.add(flushBtn);

        table = new JTable();
        tableModel = new DefaultTableModel(){
            // 不可编辑
            @Override
            public boolean isCellEditable(int row,int column){
                if (column == 0){
                    return false;
                }
                return true;
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        flushWinTable();
                        Thread.sleep(1000*60*10);
                        log.debug("刷新系统信息");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        table.setModel(tableModel);
        tableModel.setColumnIdentifiers(columnNames);
        table.getColumn("Key").setMinWidth(300);
        table.getColumn("Key").setMaxWidth(300);
        table.setDragEnabled(false);

        scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        DefaultTableCellRenderer rightRenderer  =  new DefaultTableCellRenderer();
        rightRenderer.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/intersystemCache.svg"));
        table.getColumn("Key").setCellRenderer(rightRenderer );

        DefaultTableCellRenderer leftRenderer  =  new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JTextField.LEFT);
        table.getColumn("Value").setCellRenderer(leftRenderer );

        this.add(toolBar, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void flushWinTable(){
        tableModel.setRowCount(0);
        CommonUtil.generateSystemInfo();
        String info = CommonUtil.getSystemInfo();
        for (String line : info.split("\n")){
            if (!line.strip().equals("")){
                String[] row = line.split(":");
                if (row[0].strip().startsWith("[") || row.length != 2){
                    continue;
                }
                String tmpKey = "<html><strong>" + row[0].strip() + "</strong</html>";
                tableModel.addRow(new String[]{tmpKey, row[1].strip()});
            }
        }
    }
}
