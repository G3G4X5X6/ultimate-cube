import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class TestCommandInjection {

    @SneakyThrows
    @Test
    public void testCommandInjection() {
        String fileName = "-c print('a')";
//        String fileName = "-c print('a') \\| whoami";

        // 读取文件路径
        File file = new File("-c print('a') .txt");
        System.out.println("文件名：" + file.getName());
        System.out.println("文件绝对路径：" + file.getCanonicalPath());

        // 执行命令
//        String cmd = "python " + file.getCanonicalPath();
//        Process process = Runtime.getRuntime().exec(cmd);

        String cmd = "python " + file.getName();
        Process process = Runtime.getRuntime().exec(cmd);

        // 输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // 生成恶意文件名
        FileWriter writer = new FileWriter(fileName + " .txt");
        File file1 = new File(fileName + " .txt");
        System.out.println(file1.getCanonicalPath());
        writer.append(fileName);
        writer.flush();

        File file2 = new File(new File("C:\\Users\\18312\\IdeaProjects\\ultimate-cube\\ultimate-app\\"), "..");
        file2 = new File(file2, "..");
        System.out.println(file2.getCanonicalPath());
    }
}
