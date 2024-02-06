package com.g3g4x5x6.remote.ssh.sftp.actions;

import com.g3g4x5x6.editor.EditorPanel;
import com.g3g4x5x6.remote.ssh.sftp.SftpBrowser;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class OpenAction extends DefaultAction {

    public OpenAction(SftpBrowser sftpBrowser) {
        super(sftpBrowser);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("打开文件");
        // TODO 配置编辑面板: 标题、文本、fs、icon、savePath
        new Thread(() -> {
            // 显示等待进度条
            sftpBrowser.addWaitProgressBar();

            String openFileName = this.tableModel.getValueAt(this.table.getSelectedRow(), 0).toString();
            TreePath dstPath = tree.getSelectionPath();
            String savePath = this.sftpBrowser.convertTreePathToString(dstPath) + "/" + openFileName;
            String text = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(sftpClient.read(savePath)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    text += line + "\n";
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
                JOptionPane.showMessageDialog(this.sftpBrowser, ioException.getMessage(), "警告", JOptionPane.WARNING_MESSAGE);
            }

            EditorPanel editorPanel = new EditorPanel(openFileName, savePath);
            editorPanel.setFs(fs);
            editorPanel.setSavePath(savePath);
            editorPanel.setTextArea(text);
            editorFrame.addAndSelectPanel(editorPanel);
            editorFrame.setVisible(true);

            // 关闭等待进度条
            sftpBrowser.removeWaitProgressBar();
        }).start();
    }
}
