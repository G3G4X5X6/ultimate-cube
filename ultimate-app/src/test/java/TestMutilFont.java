import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestMutilFont extends JPanel {
    private Map<Character.UnicodeBlock, Font> fontMap = new HashMap<>();

    public TestMutilFont() {
        try {
            // 加载中文字体文件
            Font chineseFont = Font.createFont(Font.TRUETYPE_FONT, new File("path/to/chinese-font.ttf"));
            // 加载英文字体文件
            Font englishFont = Font.createFont(Font.TRUETYPE_FONT, new File("path/to/english-font.ttf"));

            // 将字体映射到相应的 UnicodeBlock
            fontMap.put(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS, chineseFont);
            fontMap.put(Character.UnicodeBlock.BASIC_LATIN, englishFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        String text = "Hello, 你好!";
        float x = 10;
        float y = 50;

        for (char ch : text.toCharArray()) {
            Font font = getFontForChar(ch);
            g2d.setFont(font);
            g2d.drawString(String.valueOf(ch), x, y);
            x += g2d.getFontMetrics().charWidth(ch);
        }
    }

    private Font getFontForChar(char ch) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
        Font font = fontMap.get(block);
        return font != null ? font.deriveFont(24f) : getFont().deriveFont(24f);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Custom Font Rendering");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new TestMutilFont());
        frame.setSize(400, 200);
        frame.setVisible(true);
    }
}
