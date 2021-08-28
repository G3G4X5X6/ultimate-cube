package com.g3g4x5x6.utils;


import javax.swing.*;

public class DialogUtils {
    public static void info(String msg){
        JOptionPane.showMessageDialog(null, msg, "信息", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(String msg){
        JOptionPane.showMessageDialog(null, msg, "警告",JOptionPane.WARNING_MESSAGE);
    }

    public static void error(String msg){
        JOptionPane.showMessageDialog(null, msg, "错误",JOptionPane.ERROR_MESSAGE);
    }
}
