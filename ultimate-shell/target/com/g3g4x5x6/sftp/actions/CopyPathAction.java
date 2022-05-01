package com.g3g4x5x6.sftp.actions;

import com.g3g4x5x6.sftp.SftpBrowser;

import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

public class CopyPathAction extends DefaultAction {
    public CopyPathAction(SftpBrowser sftpBrowser) {
        super(sftpBrowser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String openFileName = table.getValueAt(table.getSelectedRow(), 0).toString();
        TreePath dstPath = tree.getSelectionPath();
        String copyPath = sftpBrowser.convertTreePathToString(dstPath) + "/" + openFileName;

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //获得系统剪贴板
        Transferable transferable = new StringSelection(copyPath);
        clipboard.setContents(transferable, null);
    }
}
