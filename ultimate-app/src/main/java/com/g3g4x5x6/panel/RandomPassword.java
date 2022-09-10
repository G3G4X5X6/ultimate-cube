package com.g3g4x5x6.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Random;

@Slf4j
public class RandomPassword extends JPanel {

    private final JToolBar toolBar;
    private JPanel panel;
    private JLabel passTextLabel;

    public RandomPassword(){
        this.setLayout(new BorderLayout());

        this.toolBar = new JToolBar();
        this.toolBar.setFloatable(false);
        initToolBar();

        this.passTextLabel = new JLabel();
        this.passTextLabel.setText("<html><pre><font size=\"12\" face=\"arial\" color=\"green\">"+ GenPass.generatePassword() + "</font></pre></html>");

        this.panel = new JPanel();
        this.panel.add(passTextLabel);

        this.add(toolBar, BorderLayout.NORTH);
        this.add(panel, BorderLayout.CENTER);
    }

    private void initToolBar(){
        JButton copyPassBtn = new JButton();
        copyPassBtn.setIcon(new FlatSVGIcon("icons/copy.svg"));
        copyPassBtn.setToolTipText("复制密码到粘贴板");
        copyPassBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setClipboardText(passTextLabel.getText());
            }
        });


        JButton refreshBtn = new JButton();
        refreshBtn.setIcon(new FlatSVGIcon("icons/refresh.svg"));
        refreshBtn.setToolTipText("重新生成随机密码");
        refreshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passTextLabel.setText("<html><pre><font size=\"12\" face=\"arial\" color=\"green\">"+ GenPass.generatePassword() + "</font></pre></html>");
                log.debug("Password: " + passTextLabel.getText());
                log.debug("Password: " + getPassFromHtml(passTextLabel.getText()));
            }
        });

        toolBar.add(copyPassBtn);
        toolBar.add(Box.createGlue());
        toolBar.add(refreshBtn);
    }

    private static String getPassFromHtml(String html){
        return html.substring("<html><pre><font size=\"12\" face=\"arial\" color=\"green\">".length(), html.indexOf("</font></pre></html>"));
    }

    private static void setClipboardText(String passwordText) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(getPassFromHtml(passwordText));
        clip.setContents(tText, null);
    }

    private static class GenPass {
        private static final String[] range = new String[5];

        static {
            range[0] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            range[1] = "abcdefghijklmnopqrstuvwxyz";
            range[2] = "0123456789";
            range[3] = "~!@#$%^&*()_+/-=[]{};:'<>?.";
        }

        private static int nextInt(int len) {
            Random random = new Random();
            return Math.abs(random.nextInt(len));
        }

        private static char getChar(int op) {
            return range[op].charAt(nextInt(range[op].length()));
        }

        private static char getRand() {
            int op = nextInt(4);
            return getChar(op);
        }

        /**
         * 默认生成规则：大写字母、小写字母、数字、特殊字符
         *
         * @param len: 生成密码长度
         * @return password： 返回随机密码
         */
        public static String generatePassword(int len) {
            StringBuilder s = new StringBuilder();

            for (int i = 0; i < len; i++) {
                s.append(getRand());
            }
            return s.toString();
        }

        /**
         * 默认生成规则：大写字母、小写字母、数字、特殊字符、长度=16位
         * @return password： 返回16位随机密码
         */
        public static String generatePassword() {
            StringBuilder s = new StringBuilder();

            for (int i = 0; i < 16; i++) {
                s.append(getRand());
            }
            return s.toString();
        }

        public static void main(String[] args) {
            System.out.println(generatePassword(6));
        }
    }
}
