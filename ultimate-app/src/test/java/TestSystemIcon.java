

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;

public class TestSystemIcon {


    public static void main(String[] args) {
        // 创建JFrame窗口
        JFrame frame = new JFrame("File and Folder Icons");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // 获取文件系统视图
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();

        // 获取并显示文件图标
        Icon fileIcon = fileSystemView.getSystemIcon(new java.io.File("E:\\0x02.GitHub\\ultimate-cube\\ultimate-app\\src\\test\\java\\TestSystemIcon.java"));
        JLabel fileLabel = new JLabel("File Icon", fileIcon, JLabel.CENTER);
        frame.add(fileLabel);

        // 获取并显示文件夹图标
        Icon folderIcon = fileSystemView.getSystemIcon(fileSystemView.getHomeDirectory());
        JLabel folderLabel = new JLabel("Folder Icon", folderIcon, JLabel.CENTER);
        frame.add(folderLabel);

        // 调整窗口大小，使所有内容都可见
        frame.pack();

        // 显示窗口
        frame.setVisible(true);
    }
}
