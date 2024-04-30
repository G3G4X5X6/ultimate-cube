package com.g3g4x5x6.remote.ssh.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.remote.ssh.SessionInfo;
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

        // 上左下右
        trailing.add(Box.createHorizontalGlue());

        trailing.add(Box.createHorizontalGlue());
        trailing.add(progressBar);
        trailing.add(Box.createHorizontalGlue());

        JButton copyIpBtn = new JButton(new FlatSVGIcon("icons/copy.svg"));
        copyIpBtn.setToolTipText("复制当前会话的远程服务器IP地址");
        copyIpBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyIpBtn.setToolTipText("复制当前会话的远程服务器IP地址：" + sessionInfo.getSessionAddress());
                CommonUtil.setClipboardText(sessionInfo.getSessionAddress());
            }
        });

        JMenuItem copyPassBtn = new JMenuItem("复制密码");
        copyPassBtn.setIcon(new FlatSVGIcon("icons/copy.svg"));
        copyPassBtn.setToolTipText("复制当前会话密码");
        copyPassBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CommonUtil.setClipboardText(sessionInfo.getSessionPass());
            }
        });

        trailing.add(copyIpBtn);
        trailing.addSeparator();

        // 关机
        JMenuItem shutdownBtn = new JMenuItem("点击3次关机");
        shutdownBtn.setIcon(new FlatSVGIcon("icons/suspend.svg"));
        shutdownBtn.setToolTipText("点击3次关机(shutdown -h now)");
        shutdownBtn.addMouseListener(new MouseAdapter() {
            @SneakyThrows
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 3) {
                    SshUtil.exec(sessionInfo.getSession(), "shutdown -h now");
                }
            }
        });

        // 重启
        JMenuItem rebootBtn = new JMenuItem("点击3次重启");
        rebootBtn.setIcon(new FlatSVGIcon("icons/stopRefresh.svg"));
        rebootBtn.setToolTipText("点击3次重启(shutdown -r now)");
        rebootBtn.addMouseListener(new MouseAdapter() {
            @SneakyThrows
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 3) {
                    SshUtil.exec(sessionInfo.getSession(), "shutdown -h now");
                }
            }
        });

//        trailing.add(shutdownBtn);
//        trailing.add(rebootBtn);
//        trailing.addSeparator();

//        trailing.add(pinSftpBtn);
        JButton refreshBtn = new JButton(new FlatSVGIcon("icons/refresh.svg"));
        refreshBtn.setToolTipText("重新连接");
        refreshBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSession();
                progressBar.setVisible(false);
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

        otherPopupMenu = new JPopupMenu();
        otherPopupMenu.add(copyPassBtn);
        otherPopupMenu.addSeparator();
        otherPopupMenu.add(shutdownBtn);
        otherPopupMenu.add(rebootBtn);
        JButton funcBtn = new JButton(new FlatSVGIcon("icons/groups.svg"));
        funcBtn.setToolTipText("功能右键");
        funcBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                otherPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        trailing.add(refreshBtn);
        trailing.add(transferTaskBtn);
        trailing.add(funcBtn);

        this.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
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
