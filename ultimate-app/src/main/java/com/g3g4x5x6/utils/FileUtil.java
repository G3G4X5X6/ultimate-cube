package com.g3g4x5x6.utils;

import com.g3g4x5x6.AppConfig;

import java.io.File;
import java.util.ArrayList;

public class FileUtil {

    /**
     * 递归列出目录下的所有文件
     *
     * @param directoryPath 目录路径字符串
     * @return 目录下的所有文件列表
     */
    public static ArrayList<File> listAllFiles(String directoryPath) {
        File rootDirectory = new File(directoryPath);
        ArrayList<File> fileList = new ArrayList<>();

        if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
            System.out.println("The specified directory does not exist or is not a directory.");
            return fileList;
        }

        traverseDirectory(rootDirectory, fileList);
        return fileList;
    }

    /**
     * 获取所有会话文件，不包括最近会话文件
     *
     * @return 目录下的所有文件列表
     */
    public static ArrayList<File> listAllSessionFiles() {
        ArrayList<File> filesList = new ArrayList<>();
        for (File file : listAllFiles(AppConfig.getSessionPath())) {
            if (file.getName().contains("recent")) {
                continue;
            }
            filesList.add(file);
        }
        return filesList;
    }

    /**
     * 递归遍历目录，将所有文件添加到文件列表中
     *
     * @param directory 要遍历的目录
     * @param fileList  文件列表
     */
    private static void traverseDirectory(File directory, ArrayList<File> fileList) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        traverseDirectory(file, fileList);
                    } else {
                        fileList.add(file);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        for (File file : listAllFiles("C:\\Users\\G3G4X5X6\\.ultimate-cube\\sessions")) {
            System.out.println(file.getAbsolutePath());

        }
    }
}
