package com.g3g4x5x6.tools;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.remote.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;

@Slf4j
public class PasswordGeneratorPanel extends JPanel {

    private final JToolBar toolBar;
    private final JLabel passTextLabel;

    private JButton statusBtn;
    private String notCopyIcon;
    private String copiedIcon;

    // 数字、大写、小写、特殊字符
    private final JToggleButton dcreditBtn = new JToggleButton("D");
    private final JToggleButton ucreditBtn = new JToggleButton("U");
    private final JToggleButton lcreditBtn = new JToggleButton("L");
    private final JToggleButton screditBtn = new JToggleButton("S");
    private final JToggleButton minlenBtn = new JToggleButton("16");
    private final int minlen = 16;

    public PasswordGeneratorPanel() {
        this.setLayout(new BorderLayout());

        this.toolBar = new JToolBar();
        this.toolBar.setFloatable(false);
        initToolBar();

        this.passTextLabel = new JLabel();
        this.passTextLabel.setToolTipText("双击刷新密码，右键复制密码");
        this.passTextLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) refreshPasswd();

                if (e.getButton() == 3) setClipboardText(passTextLabel.getText());
            }
        });
        refreshPasswd();

        // 为了 JLabel 居中显示
        JPanel panel = new JPanel();
        panel.add(passTextLabel);

        this.add(toolBar, BorderLayout.NORTH);
        this.add(panel, BorderLayout.CENTER);
    }

    private void refreshPasswd() {
        this.passTextLabel.setFont(Font.getFont(Font.MONOSPACED, new Font("宋体", Font.BOLD, 32)));
        this.passTextLabel.setForeground(Color.decode("#228B22"));
        try {
            this.passTextLabel.setText(PasswordGenerator.generatePassword(minlen, ucreditBtn.isSelected(), lcreditBtn.isSelected(), dcreditBtn.isSelected(), screditBtn.isSelected()));
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error(illegalArgumentException.getMessage());
            passTextLabel.setText("最少选择一种字符类型");
        }

        // 设置密码复制状态
        statusBtn.setIcon(new FlatSVGIcon(notCopyIcon));
    }

    private void initToolBar() {
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

        /**
         *             range[0] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
         *             range[1] = "abcdefghijklmnopqrstuvwxyz";
         *             range[2] = "0123456789";
         *             range[3] = "~!@#$%^&*()_+/-=[]{};:'<>?.";
         */
        dcreditBtn.setSelected(true);
        dcreditBtn.setToolTipText("0123456789");
        ucreditBtn.setSelected(true);
        ucreditBtn.setToolTipText("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        lcreditBtn.setSelected(true);
        lcreditBtn.setToolTipText("abcdefghijklmnopqrstuvwxyz");
        screditBtn.setSelected(true);
        screditBtn.setToolTipText("~!@#$%^&*()_+/-=[]{};:'<>?.");
        //
        minlenBtn.setSelected(true);
        minlenBtn.setEnabled(false);
        minlenBtn.setToolTipText("minlen: 16");

        toolBar.add(dcreditBtn);
        toolBar.add(ucreditBtn);
        toolBar.add(lcreditBtn);
        toolBar.add(screditBtn);
        toolBar.addSeparator();
        toolBar.add(minlenBtn);
        toolBar.addSeparator();
        toolBar.add(statusBtn);
        toolBar.addSeparator();
        toolBar.add(copyPassBtn);
        toolBar.add(Box.createGlue());
        toolBar.add(refreshBtn);
    }

    private void setClipboardText(String passwordText) {
        CommonUtil.setClipboardText(passwordText);
        // 设置已复制状态
        statusBtn.setIcon(new FlatSVGIcon(copiedIcon));
    }

    private static class PasswordGenerator {

        private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        private static final String DIGITS = "0123456789";
        private static final String SPECIAL = "~!@#$%^&*()_+/-=[]{};:'<>?.";

        private static final SecureRandom random = new SecureRandom();

        public static String generatePassword(int length, boolean includeUpper, boolean includeLower, boolean includeDigits, boolean includeSpecial) {
            if (length < 1) throw new IllegalArgumentException("Password length must be at least 1");

            StringBuilder charPool = new StringBuilder();
            List<Character> passwordChars = new ArrayList<>();

            if (includeUpper) {
                charPool.append(UPPERCASE);
                passwordChars.add(getRandomCharFromRange(UPPERCASE));
            }
            if (includeLower) {
                charPool.append(LOWERCASE);
                passwordChars.add(getRandomCharFromRange(LOWERCASE));
            }
            if (includeDigits) {
                charPool.append(DIGITS);
                passwordChars.add(getRandomCharFromRange(DIGITS));
            }
            if (includeSpecial) {
                charPool.append(SPECIAL);
                passwordChars.add(getRandomCharFromRange(SPECIAL));
            }

            if (charPool.length() == 0) {
                throw new IllegalArgumentException("At least one character type must be included");
            }

            while (passwordChars.size() < length) {
                passwordChars.add(getRandomCharFromRange(charPool.toString()));
            }

            Collections.shuffle(passwordChars);

            StringBuilder password = new StringBuilder();
            for (Character ch : passwordChars) {
                password.append(ch);
            }

            return password.toString();
        }

        private static char getRandomCharFromRange(String range) {
            return range.charAt(random.nextInt(range.length()));
        }

        public static void main(String[] args) {
            // 生成一个包含大写字母、小写字母、数字和特殊字符的默认16位密码
            String password = generatePassword(16, true, true, true, true);
            System.out.println("Generated Password: " + password);

            // 生成一个包含大写字母和数字的12位密码
            String password2 = generatePassword(12, true, false, true, false);
            System.out.println("Generated Password: " + password2);

            // 生成一个包含所有类型字符的20位密码
            String password3 = generatePassword(20, true, true, true, true);
            System.out.println("Generated Password: " + password3);

            // 生成一个不包含小写字符的20位密码
            String password4 = generatePassword(20, true, false, true, true);
            System.out.println("Generated Password: " + password4);
        }
    }
}
