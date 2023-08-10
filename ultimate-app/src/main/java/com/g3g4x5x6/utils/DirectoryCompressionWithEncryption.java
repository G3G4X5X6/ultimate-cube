package com.g3g4x5x6.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;

public class DirectoryCompressionWithEncryption {
    public static void main(String[] args) {
        String sourceFolder = "path/to/source/folder";
        String zipFile = "path/to/output/zip/file";
        String password = "your_password";

        // 压缩并加密目录中的文件
        compressAndEncryptDirectory(sourceFolder, zipFile, password);
    }

    private static void compressAndEncryptDirectory(String sourceFolder, String zipFile, String password) {
        try {
            // 创建一个ZipFile对象
            ZipFile zip = new ZipFile(zipFile);

            // 设置加密参数
            ZipParameters parameters = new ZipParameters();
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD); // 使用标准加密方法

            // 设置密码
            zip.setPassword(password.toCharArray());

            // 获取目录中的所有文件
            File folder = new File(sourceFolder);
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    // 处理文件
                    processFile(file);

                    // 将文件添加到压缩文件中
                    zip.addFile(file, parameters);
                }
            }
            System.out.println("目录压缩并加密完成：" + zipFile);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    private static void processFile(File file) {
        // 在这里进行对文件的处理逻辑，可以根据需求自行编写
        System.out.println("处理文件：" + file.getAbsolutePath());
    }
}