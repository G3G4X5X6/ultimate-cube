import com.google.common.base.Ascii;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCharReplace {
    @Test
    public void testRegx() {
        char ch = Ascii.ESC;
        String content = "[?[34mWRN?[0m]";
        String pattern = "(\\?)(\\[\\d+m)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        if (m.find()) {
            System.out.print("m.group(1): ");
            System.out.println(m.group(1));
            System.out.println(m);
            System.out.println(m.replaceAll("666$2"));
        }
        System.out.println(content.replaceAll(pattern, "7777"));
    }

}
