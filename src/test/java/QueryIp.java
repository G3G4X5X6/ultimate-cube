import com.github.jarod.qqwry.IPZone;
import com.github.jarod.qqwry.QQWry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class QueryIp {
    public static void main(String[] args) throws IOException {
        QQWry qqwry = new QQWry(); // load qqwry.dat from classpath

//        QQWry qqwry = new QQWry(Paths.get("path/to/qqwry.dat")); // load qqwry.dat from java.nio.file.Path

//        byte[] data = Files.readAllBytes(Paths.get("path/to/qqwry.dat"));
//        QQWry qqwry = new QQWry(data); // create QQWry with provided data

        String dbVer = qqwry.getDatabaseVersion();
        System.out.printf("qqwry.dat version=> %s", dbVer);
        System.out.println();
// qqwry.dat version=2020.9.10

        String myIP = "192.168.100.100";
        IPZone ipzone = qqwry.findIP(myIP);
        System.out.printf("%s, %s", ipzone.getMainInfo(), ipzone.getSubInfo());
// IANA, 保留地址用于本地回送
    }
}
