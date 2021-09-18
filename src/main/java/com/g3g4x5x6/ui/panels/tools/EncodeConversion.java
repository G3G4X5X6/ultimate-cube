package com.g3g4x5x6.ui.panels.tools;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.App;
import com.g3g4x5x6.utils.CommonUtil;
import com.ibm.icu.text.CharsetMatch;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;


@Slf4j
public class EncodeConversion extends JDialog {
    private JToolBar toolBar;
    private JTable leftTable = null;
    private JTable rightTable = null;
    private DefaultTableModel leftModel;
    private DefaultTableModel rightModel;

    private ArrayList<File> globalFile = new ArrayList<>();
    private JProgressBar progressBar = new JProgressBar();
    private JPanel progressPane;
    private JPanel conversionPane;

    // ToolBar Action
    private JButton conversionBtn;
    private JButton importBtn;
    private JButton cleanBtn;

    public EncodeConversion() {
        super(App.mainFrame);
        this.setPreferredSize(new Dimension(500, 300));
        this.setSize(new Dimension(800, 300));
        this.setLocationRelativeTo(null);
        this.setModal(false);
        this.setTitle("文件编码转换工具");
        this.setLayout(new BorderLayout());
        this.setVisible(true);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        // Action
        conversionBtn = new JButton();
        conversionBtn.setToolTipText("开始转换编码");
        conversionBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/converter.svg"));
        importBtn = new JButton();
        importBtn.setToolTipText("导入待转换文件");
        importBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/newFolder.svg"));
        cleanBtn = new JButton();
        cleanBtn.setToolTipText("清除待转换文件，重新选择");
        cleanBtn.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/delete.svg"));

        progressPane = new JPanel();
        progressPane.add(progressBar);
        progressBar.setVisible(false);

        toolBar.add(importBtn);
        toolBar.addSeparator();
        toolBar.add(conversionBtn);
        toolBar.addSeparator();
        toolBar.add(cleanBtn);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(progressPane);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(400);
        splitPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int with = splitPane.getWidth();
                splitPane.setDividerLocation(with / 2);
            }
        });

        String[] columns = new String[]{"文件名", "文件编码"};

        leftModel = new DefaultTableModel();
        leftModel.setColumnIdentifiers(columns);
        leftTable = new JTable();
        leftTable.setModel(leftModel);
        leftTable.getColumn("文件编码").setMinWidth(110);
        leftTable.getColumn("文件编码").setMaxWidth(110);
        JScrollPane leftScroll = new JScrollPane(leftTable);
        leftScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        leftScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        rightModel = new DefaultTableModel();
        rightModel.setColumnIdentifiers(columns);
        rightTable = new JTable();
        rightTable.setModel(rightModel);
        rightTable.getColumn("文件编码").setMinWidth(110);
        rightTable.getColumn("文件编码").setMaxWidth(110);
        JScrollPane rightScroll = new JScrollPane(rightTable);
        rightScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        splitPane.setLeftComponent(leftScroll);
        splitPane.setRightComponent(rightScroll);

        this.addToolBarActionListener();
        this.add(toolBar, BorderLayout.NORTH);
        this.add(splitPane, BorderLayout.CENTER);
    }

    @SneakyThrows
    private void readDir(File file) {
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (f.isDirectory())    //若是目录，则递归打印该目录下的文件
                readDir(f);
            if (f.isFile()) {
                log.debug("File: " + f.getPath());
                CharsetMatch cm = CommonUtil.checkCharset(new BufferedInputStream(new FileInputStream(f)));
                log.debug("CheckCharset:" + cm.getName());
                leftModel.addRow(new String[]{f.getAbsolutePath(), cm.getName()});
                globalFile.add(f);
                progressBar.setValue(globalFile.size());
            }
        }
    }

    private void addToolBarActionListener() {
        importBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("导入待转换文件");
                // 创建一个默认的文件选取器
                JFileChooser fileChooser = new JFileChooser();
                // 允许多选
                fileChooser.setMultiSelectionEnabled(true);
                // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
                int result = fileChooser.showOpenDialog(App.mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] files = fileChooser.getSelectedFiles();
                    // 设置进度条
                    progressBar.setVisible(true);
                    progressBar.setStringPainted(false);
                    progressBar.setIndeterminate(true);
                    // 创建后台任务
                    SwingWorker<String, Object> task = new SwingWorker<String, Object>() {
                        @Override
                        protected String doInBackground() throws Exception {
                            // 此处处于 SwingWorker 线程池中
                            for (File file : files) {
                                if (file.isDirectory()) {
                                    log.debug("Directory: " + file.getAbsolutePath());
                                    readDir(file);
                                } else {
                                    log.debug("File: " + file.getPath());
                                    CharsetMatch cm = CommonUtil.checkCharset(new BufferedInputStream(new FileInputStream(file)));
                                    log.debug("CheckCharset:" + cm.getName());
                                    leftModel.addRow(new String[]{file.getAbsolutePath(), cm.getName()});
                                    globalFile.add(file);
                                    progressBar.setValue(globalFile.size());
                                }
                            }
                            return "Hello";
                        }

                        @Override
                        protected void done() {
                            // 此方法将在后台任务完成后在事件调度线程中被回调
                            progressBar.setIndeterminate(false);
                            progressBar.setMaximum(globalFile.size());
                            progressBar.setValue(globalFile.size());
                        }
                    };
                    // 启动任务
                    task.execute();
                }
            }
        });

        conversionBtn.addActionListener(new AbstractAction() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("开始转换文件编码");
                // TODO 每次重新转换需考虑缓存
                rightModel.setRowCount(0);
                // 设置进度条
//                JProgressBar rogressBar = new JProgressBar();
                progressPane.add(progressBar);
                progressBar.setMaximum(globalFile.size());
                progressBar.setValue(0);
                progressBar.setVisible(true);
                progressBar.setStringPainted(true);
                // 创建后台任务
                SwingWorker<String, Object> task = new SwingWorker<String, Object>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        // 此处处于 SwingWorker 线程池中
                        Iterator<File> iterator = globalFile.iterator();
                        int i = 1;
                        // TODO 转换保存文件编码
                        while (iterator.hasNext()) {
                            File file = iterator.next();
                            CharsetMatch cm = CommonUtil.checkCharset(new BufferedInputStream(new FileInputStream(file)));
                            log.debug("CheckCharset:" + cm.getName());
                            rightModel.addRow(new String[]{file.getAbsolutePath(), cm.getName()});
                            progressBar.setValue(i);
                            i += 1;
                        }
                        return "Hello";
                    }

                    @Override
                    protected void done() {
                        // 此方法将在后台任务完成后在事件调度线程中被回调
                        log.debug("文件编码转换完成");
                    }
                };
                // 启动任务
                task.execute();
            }
        });

        cleanBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("清除缓存");
                globalFile.clear();
                leftModel.setRowCount(0);
                rightModel.setRowCount(0);
                progressBar.setValue(0);
                progressBar.setVisible(false);
            }
        });
    }
}
