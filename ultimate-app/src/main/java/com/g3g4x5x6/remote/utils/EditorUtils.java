package com.g3g4x5x6.remote.utils;

import com.g3g4x5x6.editor.EditorPanel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.g3g4x5x6.MainFrame.editorFrame;

public class EditorUtils {
    private EditorUtils() {
    }

    public static void openFileInEditor(String openFileName, String filePath) {
        EditorPanel editorPanel = new EditorPanel(openFileName, filePath);
        editorPanel.setSavePath(filePath);
        try {
            editorPanel.setTextArea(Files.readString(Path.of(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        editorFrame.addAndSelectPanel(editorPanel);
        editorFrame.setVisible(true);
    }


}
