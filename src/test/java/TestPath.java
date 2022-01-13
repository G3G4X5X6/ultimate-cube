import com.g3g4x5x6.utils.CommonUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class TestPath {
    @Test
    public void testPath() throws IOException {
        File file = new File("C:\\Users\\G3G4X5X6\\IdeaProjects\\ultimateshell\\src\\test\\java\\TestPath.java;ls");
        File file1 = new File(".\\TestPath666.java;ls");
        File file2 = new File(".\\TestPath666.java;ls or whoami");

        System.out.println(file.getCanonicalPath());
        System.out.println(file1.getCanonicalPath());
        System.out.println(file2.getCanonicalPath());
    }

    @Test
    public void testExec() throws IOException {
        File file2 = new File(".\\TestPath666.java;ls; or whoami");
        String cmd = "python " + file2.getCanonicalPath();
        String cmd1 = "python -m pip -V && calc";
        System.out.println(cmd);
//        Runtime.getRuntime().exec(cmd);
//        Runtime.getRuntime().exec(cmd1);
//        System.out.println(CommonUtil.exec(cmd));
        System.out.println(CommonUtil.exec(cmd1));
        System.out.println(CommonUtil.exec("whoami && calc"));
    }
}
