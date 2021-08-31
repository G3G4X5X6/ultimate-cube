package com.g3g4x5x6.utils;


import javax.swing.*;
import java.awt.*;

public class DialogUtil {
    private DialogUtil(){

    }

    public static int yesOrNo(Component parent, String msg){
        return JOptionPane.showConfirmDialog(parent, msg, "提示", JOptionPane.YES_NO_OPTION);
    }

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
