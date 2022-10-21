package com.g3g4x5x6.panel.tool;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

@Slf4j
public class RandomPasswordPane extends JPanel {

    private final JToolBar toolBar;
    private final JLabel passTextLabel;

    private JButton statusBtn;
    private String notCopyIcon;
    private String copiedIcon;

    public RandomPasswordPane(){
        this.setLayout(new BorderLayout());

        this.toolBar = new JToolBar();
        this.toolBar.setFloatable(false);
        initToolBar();

        this.passTextLabel = new JLabel();
        this.passTextLabel.setToolTipText("双击刷新密码，右键复制密码");
        this.passTextLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    refreshPasswd();

                if (e.getButton() == 3)
                    setClipboardText(passTextLabel.getText());
            }
        });
        refreshPasswd();

        // 为了 JLabel 居中显示
        JPanel panel = new JPanel();
        panel.add(passTextLabel);

        this.add(toolBar, BorderLayout.NORTH);
        this.add(panel, BorderLayout.CENTER);
    }

    private void refreshPasswd(){
        this.passTextLabel.setFont(Font.getFont(Font.MONOSPACED, new Font("宋体", Font.BOLD, 32)));
        this.passTextLabel.setForeground(Color.decode("#228B22"));
        this.passTextLabel.setText(GenPass.generatePassword());

        // 设置密码复制状态
        statusBtn.setIcon(new FlatSVGIcon(notCopyIcon));
    }

    private void initToolBar(){
        statusBtn = new JButton(new FlatSVGIcon("icons/intentionBulbGrey.svg"));
        statusBtn.setToolTipText("复制状态");
        notCopyIcon = "icons/intentionBulbGrey.svg";
        copiedIcon = "icons/intentionBulb.svg";

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
                refreshPasswd();
                log.debug("Password: " + passTextLabel.getText());
            }
        });

        toolBar.add(statusBtn);
        toolBar.add(copyPassBtn);
        toolBar.add(Box.createGlue());
        toolBar.add(refreshBtn);
    }

    private void setClipboardText(String passwordText) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(passwordText);
        clip.setContents(tText, null);

        // 设置已复制状态
        statusBtn.setIcon(new FlatSVGIcon(copiedIcon));
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
