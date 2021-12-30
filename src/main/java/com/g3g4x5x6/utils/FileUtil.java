package com.g3g4x5x6.utils;

import java.io.File;

public class FileUtil {
    private FileUtil(){

    }

    public static void delDir(File file) {
        if (file.isDirectory()) {
            File zFiles[] = file.listFiles();
            for (File file2 : zFiles) {
                delDir(file2);
            }
        }
        file.delete();
    }
}
