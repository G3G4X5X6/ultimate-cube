package com.g3g4x5x6.remote.ssh.sftp.actions;

import com.g3g4x5x6.remote.ssh.sftp.SftpBrowser;
import com.g3g4x5x6.remote.utils.SshUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class ForceDelAction extends DefaultAction {
    public ForceDelAction(SftpBrowser sftpBrowser) {
        super(sftpBrowser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreePath dstPath = tree.getSelectionPath();
        String path = sftpBrowser.convertTreePathToString(dstPath);
        int yesOrNo = JOptionPane.showConfirmDialog(sftpBrowser,  "是否强制删除目录: " + path + " ?", "警告", JOptionPane.YES_NO_OPTION);
        if (yesOrNo == 0) {
            try {
                SshUtil.exec(fs.getClientSession(), "rm -rf " + path);
            } catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(sftpBrowser, exception.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            tree.setSelectionPath(Objects.requireNonNull(tree.getSelectionPath()).getParentPath());
            treeModel.removeNodeFromParent(node);
        }
    }
}
