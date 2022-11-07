package com.g3g4x5x6.remote.sftp.actions;

import com.g3g4x5x6.editor.EditorFrame;
import com.g3g4x5x6.remote.sftp.SftpBrowser;
import com.g3g4x5x6.remote.sftp.SftpLogPanel;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DefaultAction extends AbstractAction {
    protected SftpBrowser sftpBrowser;

    protected JScrollPane taskPanel;
    protected SftpLogPanel logPanel;

    protected SftpClient sftpClient;
    protected SftpFileSystem fs;

    protected JTable table;
    protected DefaultTableModel tableModel;

    protected JTree tree;
    protected DefaultTreeModel treeModel;

    protected final EditorFrame editorFrame = EditorFrame.getInstance();

    public DefaultAction(SftpBrowser sftpBrowser) {
        this.sftpBrowser = sftpBrowser;

        this.taskPanel = sftpBrowser.getSftpTabbedPanel().getTaskPanel();
        this.logPanel = sftpBrowser.getSftpTabbedPanel().getLogPanel();

        this.sftpClient = this.sftpBrowser.getSftpClient();
        this.fs = this.sftpBrowser.getFs();

        this.table = this.sftpBrowser.getMyTable();
        this.tableModel = this.sftpBrowser.getTableModel();

        this.tree = this.sftpBrowser.getMyTree();
        this.treeModel = this.sftpBrowser.getTreeModel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    protected void logOps(String msg){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logPanel.getLogPanel().append("[" + sdf.format(new Date()) + "] " + msg + "\n");
    }
}
