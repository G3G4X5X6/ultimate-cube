package com.g3g4x5x6.remote.ssh.sftp.actions;

import com.g3g4x5x6.remote.ssh.sftp.SftpBrowser;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.nio.file.Files;

public class NewFileAction extends DefaultAction {
    public NewFileAction(SftpBrowser sftpBrowser) {
        super(sftpBrowser);
    }

    @SneakyThrows
    @Override
    public void actionPerformed(ActionEvent e) {
        String file = JOptionPane.showInputDialog(sftpBrowser, "新建文件名");
        TreePath dstPath = tree.getSelectionPath();
        String path = sftpBrowser.convertTreePathToString(dstPath);
        if (file != null) {
            if (Files.exists(fs.getPath(path + "/" + file))) {
                JOptionPane.showMessageDialog(sftpBrowser, "已存在文件：" + path + "/" + file, "警告", JOptionPane.WARNING_MESSAGE);
            } else {
                Files.createFile(fs.getPath(path + "/" + file));
            }
        }
        sftpBrowser.freshTable();
    }
}
