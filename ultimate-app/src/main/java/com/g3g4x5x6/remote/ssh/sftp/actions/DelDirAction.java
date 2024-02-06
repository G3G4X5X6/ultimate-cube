package com.g3g4x5x6.remote.ssh.sftp.actions;

import com.g3g4x5x6.remote.ssh.sftp.SftpBrowser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class DelDirAction extends DefaultAction {
    public DelDirAction(SftpBrowser sftpBrowser) {
        super(sftpBrowser);
    }

    @SneakyThrows
    public void actionPerformed(ActionEvent e) {
        log.debug("删除目录");
        if (tree.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(sftpBrowser, "请选择删除目录", "警告", JOptionPane.WARNING_MESSAGE);
        } else {
            TreePath[] dstPath = tree.getSelectionPaths();
            for (TreePath treePath : dstPath) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                String path = sftpBrowser.convertTreePathToString(treePath);
                if (Files.exists(fs.getPath(path))) {
                    int yesNo = JOptionPane.showConfirmDialog(sftpBrowser, "确认删除目录：\n", "提示", JOptionPane.YES_NO_OPTION);
                    if (yesNo == 0) {
                        try {
                            Files.delete(fs.getPath(path));
                            tree.setSelectionPath(tree.getSelectionPath().getParentPath());
                            treeModel.removeNodeFromParent(treeNode);
                        } catch (IOException ioException) {
                            JOptionPane.showMessageDialog(sftpBrowser, ioException.getMessage() + "\n文件夹不为空，无法删除！", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }
    }
}
