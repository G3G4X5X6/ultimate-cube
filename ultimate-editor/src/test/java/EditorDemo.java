import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.editor.EditorFrame;
import com.g3g4x5x6.editor.EditorPanel;

import javax.swing.*;

public class EditorDemo {
    public static void main(String[] args) {
        initFlatLaf();

        SwingUtilities.invokeLater(()->{
            EditorFrame editorFrame = EditorFrame.getInstance();
            editorFrame.setVisible(true);

            // 初始编辑面板
            if (editorFrame.getTabbedPane().getTabCount() == 0) {
                EditorPanel editorPanel = new EditorPanel();
                editorFrame.getTabbedPane().addTab(editorPanel.getTitle(), editorPanel.getIcon(), editorPanel, editorPanel.getTips());
                editorFrame.getTabbedPane().setSelectedIndex(editorFrame.getTabbedPane().getTabCount() - 1);
            }
        });
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
