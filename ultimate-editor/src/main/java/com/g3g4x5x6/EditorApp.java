package com.g3g4x5x6;

import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.editor.EditorFrame;
import com.g3g4x5x6.editor.EditorPanel;
import com.g3g4x5x6.editor.util.EditorConfig;

import javax.swing.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class EditorApp {

    public static void main(String[] args) {
        initFlatLaf();
        EditorFrame editor = new EditorFrame();
        editor.setTitle(EditorConfig.getProperty("editor.title"));
        editor.setDefaultCloseOperation(EXIT_ON_CLOSE);

        editor.addAndSelectPanel(new EditorPanel());

        editor.setVisible(true);
    }


    private static void initFlatLaf() {
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        UIManager.put("TextComponent.arc", 5);
    }
}
