import com.g3g4x5x6.utils.ConfigUtil;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class TestString {
    @Test
    public void testReplace() {
        String cmd = "java -jar %BasePath%/Behinder_v3.0_Beta_9_fixed/Behinder.jar";
        String path = Path.of(ConfigUtil.getWorkPath() + "/tools/external_tools").toString().replaceAll("\\\\", "/");
        System.out.println(path);
        System.out.println(cmd.replaceAll("%BasePath%", path));
    }
}
