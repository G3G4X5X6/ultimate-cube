import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TestUnZip {

//    @SneakyThrows
//    @Test
//    public void testUnZip(){
//        // 目标目录
//        String dstPath = "C:\\Users\\18312\\IdeaProjects\\ultimate-cube\\ultimate-app";
//
//        // 打开压缩文件
//        InputStream inputStream = new FileInputStream("C:\\Users\\18312\\IdeaProjects\\ultimate-cube\\ultimate-app\\src\\test\\java\\test.zip"); ;
//        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
//
//        // 读取一个进入点
//        ZipEntry zipEntry = zipInputStream.getNextEntry();
//
//        // 解压缩
//        File file = createFile(dstPath, zipEntry.getName());
//        OutputStream outputStream = new FileOutputStream(file);
//        int readLength = 0;
//        int BUFFER = 1024;
//        byte[] buffer = new byte[BUFFER];
//        while ((readLength = zipInputStream.read(buffer, 0, BUFFER)) != -1){
//            outputStream.write(buffer, 0, readLength);
//        }
//        outputStream.close();
//
//    }
//
//    @SneakyThrows
//    private static File createFile(String dstPath, String fileName){
//        String[] dirs = fileName.split("/");
//        File file = new File(dstPath);
//
//        if (dirs.length > 1){ // 有上级目录
//            for (int i = 0; i < dirs.length - 1; i++){
//                file = new File(file, dirs[i]);
//            }
//            if (!file.exists()){
//                file.mkdirs(); // 文件对应目录不存在，则创建
//            }
//            file = new File(file, dirs[dirs.length - 1]); // 创建文件
//        } else {
//            if (!file.exists()){
//                file.mkdirs(); // 若目标目录不存在，则创建
//                System.out.println("mkdirs: " + file.getCanonicalPath());
//            }
//            file = new File(file, dirs[0]); // 创建文件
//        }
//        System.out.println("ZipFileName: " + file.getCanonicalPath());
//        return file;
//    }
}
