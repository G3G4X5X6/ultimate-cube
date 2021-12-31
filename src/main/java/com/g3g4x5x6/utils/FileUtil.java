package com.g3g4x5x6.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.LinkedList;


@Slf4j
public class FileUtil {
    private FileUtil() {

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

    public static void traverseFolder(File file, LinkedList<File> filesList) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length != 0) {
                    for (File f : files) {
                        if (f.isDirectory()) {
                            traverseFolder(f, filesList);
                        } else {
                            filesList.add(f);
                        }
                    }
                }
            } else {
                filesList.add(file);
            }
        }
    }

}
