import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.App;
import com.g3g4x5x6.ui.MainFrame;
import com.g3g4x5x6.utils.ConfigUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ShowEditorPane {
    public static void main(String[] args) {
        // 启动主界面
        SwingUtilities.invokeLater(() -> {
                    createGUI();
                }
        );
    }

    public static void createGUI() {
        // 配置主题皮肤
        try {
            if (ConfigUtil.isEnableTheme()) {
                Class themeClass = App.class.getClassLoader().loadClass(ConfigUtil.getThemeClass());
                UIManager.setLookAndFeel((LookAndFeel) themeClass.getConstructor().newInstance());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception ex) {

        }

        // 启动主界面
        JFrame mainFrame = new JFrame();
        mainFrame.setLocationRelativeTo(null);

        JButton button = new JButton("TEST");
        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShowPane showPane = new ShowPane();
                showPane.setVisible(true);
                showPane.setText("Linux kali 5.10.0-kali7-amd64 #1 SMP Debian 5.10.28-1kali1 (2021-04-12) x86_64 GNU/Linux");
            }
        });
        mainFrame.getContentPane().add(button);
        mainFrame.setTitle("UltimateShell");
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    static class ShowPane extends JDialog{
        private JEditorPane editorPane;

        public ShowPane(){
            this.setLayout(new BorderLayout());
            editorPane = new JEditorPane();
            editorPane.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(editorPane);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            this.add(scrollPane, BorderLayout.CENTER);
        }

        public void setText(String text){
            editorPane.setText(text);
        }

        public void cleanText(){
            editorPane.setText("");
        }
    }
}
