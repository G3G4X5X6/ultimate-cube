package com.g3g4x5x6.remote.ssh.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.remote.ssh.SessionInfo;
import com.g3g4x5x6.remote.utils.CommonUtil;
import com.g3g4x5x6.remote.utils.SshUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT;
import static com.g3g4x5x6.remote.utils.SessionUtil.getLogArea;


@Slf4j
public class SshTabbedPane extends JTabbedPane {
    private final String id;
    private final SessionInfo sessionInfo;

    // ProgressBar for wait to reset
    private JProgressBar progressBar;

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
                e.printStackTrace();
                addTab(e.getCause().toString(), new FlatSVGIcon("icons/balloonError.svg"), getLogArea(e.getCause().toString(), e.getStackTrace()));
            }
        }
        // 选项卡
        if (sessionInfo.getSshPane() != null)
            this.addTab("", new FlatSVGIcon("icons/linux.svg"), this.sessionInfo.getSshPane());
        if (sessionInfo.getSftpBrowser() != null)
            this.addTab("", new FlatSVGIcon("icons/flattenPackages.svg"), this.sessionInfo.getSftpBrowser());

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
                CommonUtil.setClipboardText(sessionInfo.getSessionAddress());
            }
        });

        trailing.add(copyIpBtn);
        trailing.addSeparator();

        // 关机
        JButton shutdownBtn = new JButton(new FlatSVGIcon("icons/suspend.svg"));
        shutdownBtn.setToolTipText("点击3次关机(shutdown -h now)");
        shutdownBtn.addMouseListener(new MouseAdapter() {
            @SneakyThrows
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 3){
                    SshUtil.exec(sessionInfo.getSession(), "shutdown -h now");
                }
            }
        });

        // 重启
        JButton rebootBtn = new JButton(new FlatSVGIcon("icons/stopRefresh.svg"));
        rebootBtn.setToolTipText("点击3次重启(shutdown -r now)");
        rebootBtn.addMouseListener(new MouseAdapter() {
            @SneakyThrows
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 3){
                    SshUtil.exec(sessionInfo.getSession(), "shutdown -h now");
                }
            }
        });

        trailing.add(shutdownBtn);
        trailing.add(rebootBtn);
        trailing.addSeparator();

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

        JButton funcBtn = new JButton(new FlatSVGIcon("icons/listFiles.svg"));
        funcBtn.setToolTipText("功能右键");
        funcBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO 右键功能菜单
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
                this.addTab("", new FlatSVGIcon("icons/linux.svg"), this.sessionInfo.getSshPane());
                if (sessionInfo.getSftpBrowser() != null)
                    this.addTab("", new FlatSVGIcon("icons/flattenPackages.svg"), this.sessionInfo.getSftpBrowser());
                else
                    filesBrowser.updateFs(sessionInfo.getSftpFileSystem());
                // 关闭进度条
                progressBar.setVisible(false);
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }finally {
                progressBar.setVisible(false);
            }
        }).start();
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public String getId() {
        return id;
    }
}
