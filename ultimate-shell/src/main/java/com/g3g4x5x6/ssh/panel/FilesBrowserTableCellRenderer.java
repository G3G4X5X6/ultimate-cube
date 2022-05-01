package com.g3g4x5x6.ssh.panel;

import com.formdev.flatlaf.icons.FlatTreeLeafIcon;
import com.formdev.flatlaf.icons.FlatTreeOpenIcon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class FilesBrowserTableCellRenderer extends DefaultTableCellRenderer {
    private final int count;

    public FilesBrowserTableCellRenderer(int dirCount){
        this.count = dirCount;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        if (row < count && column == 0){
//            renderer.setIcon(new FlatTreeOpenIcon());
//        }else {
//            renderer.setIcon(new FlatTreeLeafIcon());
//        }
        String[] values = value.toString().split(":");
        if (values[0].equals("DIR")){
            renderer.setIcon(new FlatTreeOpenIcon());
            renderer.setText(values[1]);
        }else {
            renderer.setIcon(new FlatTreeLeafIcon());
        }

        return renderer;
    }
}
