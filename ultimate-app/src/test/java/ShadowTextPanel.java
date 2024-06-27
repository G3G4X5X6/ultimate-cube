import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ShadowTextPanel extends JPanel {
    private String text = "Hello, World!";
    private BufferedImage backgroundImage;
    private int shadowOffsetX;
    private int shadowOffsetY;

    public ShadowTextPanel(String imagePath, int shadowOffsetX, int shadowOffsetY) {
        this.shadowOffsetX = shadowOffsetX;
        this.shadowOffsetY = shadowOffsetY;
        setPreferredSize(new Dimension(400, 300));
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            // 调整背景图像大小以适应面板
            BufferedImage resizedImage = resizeImage(backgroundImage, getWidth(), getHeight());

            // 绘制背景图像
            g.drawImage(resizedImage, 0, 0, null);

            // 绘制带有阴影效果的文本
            BufferedImage textImage = createTextImageWithShadow(text, getWidth(), getHeight());
            g.drawImage(textImage, 0, 0, null);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        Image tempImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tempImage, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }

    private BufferedImage createTextImageWithShadow(String text, int width, int height) {
        BufferedImage textImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = textImage.createGraphics();

        // 透明背景
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(AlphaComposite.SrcOver);

        // 设置文本属性
        g2d.setFont(new Font("Serif", Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();

        // 绘制阴影
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, x + shadowOffsetX, y + shadowOffsetY); // 使用传入的偏移值
        g2d.dispose();

        // 对阴影部分进行模糊处理
        BufferedImage shadowImage = blurImage(textImage);

        // 在阴影之上绘制文本
        g2d = shadowImage.createGraphics();
        g2d.setFont(new Font("Serif", Font.BOLD, 48));
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, x, y);
        g2d.dispose();

        return shadowImage;
    }

    private BufferedImage blurImage(BufferedImage image) {
        float[] matrix = {
                1/16f, 2/16f, 1/16f,
                2/16f, 4/16f, 2/16f,
                1/16f, 2/16f, 1/16f
        };
        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, matrix), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Shadow Text Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            String imagePath = "C:\\Users\\G3G4X5X6\\OneDrive\\图片\\Windows-high.png";  // 替换为你的图片路径
            int shadowOffsetX = -3;  // 设置阴影的X偏移量
            int shadowOffsetY = 3;  // 设置阴影的Y偏移量
            frame.add(new ShadowTextPanel(imagePath, shadowOffsetX, shadowOffsetY));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}


// C:\Users\G3G4X5X6\OneDrive\图片\Windows-high.png
