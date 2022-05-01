package com.g3g4x5x6.sftp.actions;

import com.g3g4x5x6.sftp.SftpBrowser;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.io.File;

@Slf4j
public class DownloadAction extends DefaultAction {
    public DownloadAction(SftpBrowser sftpBrowser) {
        super(sftpBrowser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logOps("下载");
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle("选择保存路径");

        if (!tree.isSelectionEmpty() && table.getSelectedRow() != -1){
            log.debug("下载文件");
            int value = chooser.showOpenDialog(sftpBrowser);
            if (value == JFileChooser.APPROVE_OPTION) {
                File outputFile = chooser.getSelectedFile();
                log.debug(outputFile.getAbsolutePath());

                for (int index : table.getSelectedRows()) {
                    String downloadFileName = table.getValueAt(index, 0).toString();
                    TreePath dstPath = tree.getSelectionPath();

                    String path = sftpBrowser.convertTreePathToString(dstPath) + "/" + downloadFileName;
                    log.info("下载的文件：" + path);

                    // TODO 开始创建任务
                }
            }

        }else if (!tree.isSelectionEmpty() && table.getSelectedRow() == -1) {
            log.debug("下载目录");
            // TODO 下载目录
            String path = sftpBrowser.convertTreePathToString(tree.getSelectionPath());

            int yesOrNo = JOptionPane.showConfirmDialog(sftpBrowser, "是否确认下载目录 [" + path + "] 下的所有文件？", "提示", JOptionPane.YES_NO_OPTION);
            if (yesOrNo == 0) {
                int value = chooser.showOpenDialog(sftpBrowser);
                if (value == JFileChooser.APPROVE_OPTION) {
                    File outputFile = chooser.getSelectedFile();

                    // TODO 开始创建任务
                }
            }
        } else {
            JOptionPane.showMessageDialog(sftpBrowser, "请先选择下载的文件或目录", "警告", JOptionPane.WARNING_MESSAGE);
        }
    }
}
