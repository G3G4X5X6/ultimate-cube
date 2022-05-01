package com.g3g4x5x6.sftp.actions;

import com.g3g4x5x6.sftp.SftpBrowser;
import lombok.extern.slf4j.Slf4j;

import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.io.IOException;


@Slf4j
public class UploadAction extends DefaultAction {
    public UploadAction(SftpBrowser sftpBrowser) {
        super(sftpBrowser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String path;
        if (tree.isSelectionEmpty()) {
            try {
                // TODO 默认上传至用户目录 getDefaultDir()
                path = fs.getDefaultDir().toRealPath().toString();
                log.debug("默认用户目录：" + path);
                log.debug(path);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            TreePath dstPath = tree.getSelectionPath();
            path = sftpBrowser.convertTreePathToString(dstPath);
        }

    }

}
