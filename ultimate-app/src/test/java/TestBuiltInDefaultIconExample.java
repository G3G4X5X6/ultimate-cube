import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;

public class TestBuiltInDefaultIconExample {
    public static void main(String[] args) {
        // 创建JFrame窗口
        JFrame frame = new JFrame("Built-in Default Icons");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // 获取文件系统视图
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();

        // 使用已知存在的系统文件夹来获取默认文件夹图标
        File existingFolder = fileSystemView.getHomeDirectory(); // 用户的主目录, 通常都存在
        Icon defaultFolderIcon = fileSystemView.getSystemIcon(existingFolder);

        // 使用临时文件来获取默认文件图标
        File tempFile = null;
        Icon defaultFileIcon = null;
        try {
            tempFile = File.createTempFile("defaultIcon", ".txt"); // 创建临时文件
            defaultFileIcon = fileSystemView.getSystemIcon(tempFile); // 获取临时文件的图标作为默认文件图标
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tempFile != null) tempFile.delete(); // 删除临时文件
        }

        // 创建标签以显示默认图标
        JLabel folderLabel = new JLabel("Default Folder Icon", defaultFolderIcon, JLabel.CENTER);
        JLabel fileLabel = new JLabel("Default File Icon", defaultFileIcon, JLabel.CENTER);

        // 将标签添加到窗口
        frame.add(folderLabel);
        frame.add(fileLabel);

        // 调整窗口大小并显示
        frame.pack();
        frame.setVisible(true);
    }
}
