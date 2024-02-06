package com.g3g4x5x6.remote.ssh.sftp;

import com.g3g4x5x6.editor.util.EditorUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public class SftpLogPanel extends JPanel {
    private final RSyntaxTextArea logPanel;

    public SftpLogPanel() {
        logPanel = EditorUtil.createTextArea();
        logPanel.setEditable(false);
        logPanel.setSyntaxEditingStyle("text/unix");
        logPanel.setCodeFoldingEnabled(true);
        logPanel.setAutoscrolls(true);
        RTextScrollPane sp = new RTextScrollPane(logPanel);
        sp.setBorder(null);

        this.setLayout(new BorderLayout());
        this.add(sp, BorderLayout.CENTER);
    }

    public RSyntaxTextArea getLogPanel() {
        return logPanel;
    }
}
