package com.g3g4x5x6.remote.ssh.sftp.actions;

import com.g3g4x5x6.remote.ssh.sftp.SftpBrowser;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.ActionEvent;

@Slf4j
public class RefreshAction extends DefaultAction {
    public RefreshAction(SftpBrowser sftpBrowser) {
        super(sftpBrowser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        sftpBrowser.freshTable();
        logOps("刷新");
        log.debug("刷新");
    }
}
