package com.g3g4x5x6.remote.ssh.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.remote.ssh.SessionInfo;
import com.g3g4x5x6.remote.ssh.sftp.FilesBrowser;
import com.g3g4x5x6.remote.utils.CommonUtil;
import com.g3g4x5x6.remote.utils.SshUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT;
import static com.g3g4x5x6.remote.utils.session.SessionUtil.getLogArea;


@Slf4j
public class SshTabbedPane extends JTabbedPane {
    @Getter
    private final String id;
    private final SessionInfo sessionInfo;

    // ProgressBar for wait to reset
    private JProgressBar progressBar;
    private JPopupMenu otherPopupMenu;

    private final FilesBrowser filesBrowser = null;

    public static JPopupMenu taskPopupMenu;

    public SshTabbedPane(SessionInfo sessionInfo) {
        // TabbedPane
        customComponents();

        this.sessionInfo = sessionInfo;
        this.id = sessionInfo.getSessionId();
        taskPopupMenu = new JPopupMenu();

        // 等待进度条
        progressBar.setVisible(true);
        if (this.sessionInfo.getSshPane() == null) {
            try {
                this.sessionInfo.initComponent();
            } catch (GeneralSecurityException | IOException e) {
                log.debug(e.getMessage());
                addTab(e.getCause().toString(), new FlatSVGIcon("icons/balloonError.svg"), getLogArea(e.getCause().toString(), e.getStackTrace()));
            }
        }
        // 选项卡
        if (sessionInfo.getSshPane() != null)
            this.addTab("终端会话", new FlatSVGIcon("icons/linux.svg"), this.sessionInfo.getSshPane());
        if (sessionInfo.getSftpBrowser() != null)
            this.addTab("文件管理", new FlatSVGIcon("icons/flattenPackages.svg"), this.sessionInfo.getSftpBrowser());

        // 关闭进度条
        progressBar.setVisible(false);
    }

    private void customComponents() {
        JToolBar trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        JButton copyIpBtn = new JButton("IP");
        copyIpBtn.setIcon(new FlatSVGIcon("icons/copy.svg"));
        copyIpBtn.setSelected(true);
        copyIpBtn.setToolTipText("复制当前会话的远程服务器IP地址");
        copyIpBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyIpBtn.setToolTipText("复制当前会话的远程服务器IP地址：" + sessionInfo.getSessionAddress());
                CommonUtil.setClipboardText(sessionInfo.getSessionAddress());
            }
        });

        JButton copyPassBtn = new JButton("PASS");
        copyPassBtn.setIcon(new FlatSVGIcon("icons/copy.svg"));
        copyPassBtn.setToolTipText("复制当前会话密码");
        copyPassBtn.setSelected(true);
        copyPassBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CommonUtil.setClipboardText(sessionInfo.getSessionPass());
            }
        });

        JButton copyToShareBtn = new JButton("SHARE");
        copyToShareBtn.setIcon(new FlatSVGIcon("icons/copy.svg"));
        copyToShareBtn.setSelected(true);
        copyToShareBtn.setToolTipText("复制SSH连接命令行");
        copyToShareBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sshCmd = "ssh " + sessionInfo.getSessionUser() + "@" + sessionInfo.getSessionAddress() + " -p " + sessionInfo.getSessionPort();
                sshCmd += "\nPassword: " + sessionInfo.getSessionPass();
                CommonUtil.setClipboardText(sshCmd);
            }
        });

        JButton transferTaskBtn = new JButton(new FlatSVGIcon("icons/fileTransfer.svg"));
        transferTaskBtn.setToolTipText("任务列表");
        transferTaskBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                taskPopupMenu.show(transferTaskBtn, e.getX(), e.getY());
            }
        });

        // 功能右键 菜单列表
        otherPopupMenu = new JPopupMenu();
        initOtherPopupMenu();
        //
        JButton funcBtn = new JButton(new FlatSVGIcon("icons/groups.svg"));
        funcBtn.setToolTipText("功能右键");
        funcBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                otherPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });


        // 上左下右
        trailing.add(Box.createHorizontalGlue());
        trailing.add(progressBar);
        trailing.add(Box.createHorizontalGlue());
        // 上左下右
        trailing.add(copyIpBtn);
        trailing.add(copyPassBtn);
        trailing.add(copyToShareBtn);
        trailing.addSeparator();
        trailing.add(transferTaskBtn);
        trailing.addSeparator();
        trailing.add(funcBtn);

        this.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

    private void initOtherPopupMenu() {
        JMenuItem refreshItem = new JMenuItem("会话重连");
        refreshItem.setIcon(new FlatSVGIcon("icons/refresh.svg"));
        refreshItem.setToolTipText("重新连接会话");
        refreshItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSession();
                progressBar.setVisible(false);
            }
        });

        otherPopupMenu.add(refreshItem);
    }

    public void resetSession() {
        new Thread(() -> {
            // 等待进度条
            progressBar.setVisible(true);

            try {
                sessionInfo.initComponent();

                this.removeAll();
                this.addTab("终端会话", new FlatSVGIcon("icons/linux.svg"), this.sessionInfo.getSshPane());
                if (sessionInfo.getSftpBrowser() != null)
                    this.addTab("文件管理", new FlatSVGIcon("icons/flattenPackages.svg"), this.sessionInfo.getSftpBrowser());
                else filesBrowser.updateFs(sessionInfo.getSftpFileSystem());
                // 关闭进度条
                progressBar.setVisible(false);
            } catch (GeneralSecurityException | IOException | NullPointerException e) {
                log.debug(e.getMessage());
            } finally {
                progressBar.setVisible(false);
            }
        }).start();
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

}
