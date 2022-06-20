package com.g3g4x5x6.ssh.panel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.ssh.SessionInfo;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT;
import static com.g3g4x5x6.utils.SessionUtil.getLogArea;


@Slf4j
public class SshTabbedPane extends JTabbedPane {
    private final String id;
    private final SessionInfo sessionInfo;

    //    private JToggleButton pinSftpBtn;
    // ProgressBar for wait to reset
    private JProgressBar progressBar;

    private FilesBrowser filesBrowser = null;
    private JButton filesBrowserBtn;
    private JDialog dialog;

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
        if (sessionInfo.getSshPane() != null)
            this.addTab("", new FlatSVGIcon("icons/linux.svg"), this.sessionInfo.getSshPane());

        // 关闭进度条
        progressBar.setVisible(false);
    }

    private void customComponents() {
        JToolBar trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);


        dialog = new JDialog();
        dialog.setPreferredSize(new Dimension(750, 400));
        dialog.setSize(new Dimension(750, 400));
        dialog.setTitle("FilesBrowser");
        dialog.setLayout(new BorderLayout());
//        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(SshTabbedPane.this);
        dialog.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                dialog.setVisible(false);
                dialog.setAlwaysOnTop(false);
            }
        });

        filesBrowserBtn = new JButton(new FlatSVGIcon("icons/moduleDirectory.svg"));
        filesBrowserBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // show FilesBrowser
                if (filesBrowser == null) {
                    filesBrowser = new FilesBrowser(sessionInfo.getSftpFileSystem());
                    dialog.add(filesBrowser, BorderLayout.CENTER);
                }
                dialog.setVisible(true);
            }
        });

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);


        trailing.add(filesBrowserBtn);
        // 上左下右
        trailing.add(Box.createHorizontalGlue());

        trailing.add(Box.createHorizontalGlue());
        trailing.add(progressBar);
        trailing.add(Box.createHorizontalGlue());

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
//            if (sessionInfo.getSftpBrowser() != null)
//                this.addTab("", new FlatSVGIcon("icons/flattenPackages.svg"), this.sessionInfo.getSftpBrowser());
                if (filesBrowser == null)
                    filesBrowser = new FilesBrowser(sessionInfo.getSftpFileSystem());
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
