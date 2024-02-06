package com.g3g4x5x6.remote.ssh.sftp.actions;

import com.g3g4x5x6.remote.ssh.sftp.SftpBrowser;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class DelFileAction extends DefaultAction {
    public DelFileAction(SftpBrowser sftpBrowser) {
        super(sftpBrowser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("删除文件");
        int yesNo = JOptionPane.showConfirmDialog(sftpBrowser,  "确认删除选中文件？", "提示", JOptionPane.YES_NO_OPTION);
        if (yesNo == 0) {
            for (int index : table.getSelectedRows()) {
                String downloadFileName = table.getValueAt(index, 0).toString();
                // TODO 获取下载文件路径
                TreePath dstPath = tree.getSelectionPath();
                String path = sftpBrowser.convertTreePathToString(dstPath) + "/" + downloadFileName;
                log.info("删除的文件：" + path);
                try {
                    Files.delete(fs.getPath(path));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            sftpBrowser.freshTable();
        }

    }
}
