package com.g3g4x5x6.sftp.actions;

import com.g3g4x5x6.sftp.SftpBrowser;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.nio.file.Files;

public class NewDirAction extends DefaultAction {
    public NewDirAction(SftpBrowser sftpBrowser) {
        super(sftpBrowser);
    }

    @SneakyThrows
    @Override
    public void actionPerformed(ActionEvent e) {
        String dir = JOptionPane.showInputDialog(sftpBrowser, "新建目录名");
        TreePath dstPath = tree.getSelectionPath();
        String path = sftpBrowser.convertTreePathToString(dstPath);
        if (dir != null) {
            if (Files.exists(fs.getPath(path + "/" + dir))) {
                JOptionPane.showMessageDialog(sftpBrowser, "已存在目录：" + path + "/" + dir, "警告", JOptionPane.WARNING_MESSAGE);
            } else {
                Files.createDirectories(fs.getPath(path + "/" + dir));

                DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir);
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                treeModel.insertNodeInto(node, parent, 0);
            }
        }
    }
}
