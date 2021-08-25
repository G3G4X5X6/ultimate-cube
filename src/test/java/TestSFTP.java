import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.hosts.HostConfigEntry;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.fs.SftpFileSystemProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;

public class TestSFTP {
    public static void main(String[] args) throws IOException {
        HostConfigEntry hostConfigEntry = new HostConfigEntry();
        hostConfigEntry.setHostName("192.168.83.137");
        hostConfigEntry.setHost("192.168.83.137");
        hostConfigEntry.setPort(22);

        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        ClientSession session = client.connect(hostConfigEntry).verify(5000).getClientSession();
        Path remotePath;
        Path remoteDir;
//        // Full programmatic control
//        SftpFileSystemProvider provider = new SftpFileSystemProvider(client);
//        URI uri = SftpFileSystemProvider.createFileSystemURI("192.168.83.137", 22, "root", "12345678");
//        try (FileSystem fs = provider.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
//            remotePath = fs.getPath("/root/test");


        // "Mounting" a file system
        URI uri = SftpFileSystemProvider.createFileSystemURI("192.168.83.137", 22, "root", "12345678");
        try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
            remotePath = fs.getPath("/root/test");
            remoteDir = fs.getPath("/root");

        }

        try (InputStream input = Files.newInputStream(remotePath)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf-8"));
            System.out.println(reader.readLine());
        }


        try (DirectoryStream<Path> ds = Files.newDirectoryStream(remoteDir)) {
            for (Path remoteFile : ds) {
                if (Files.isRegularFile(remoteFile)) {
                    System.out.println("Delete " + remoteFile + " size=" + Files.size(remoteFile));
                    Files.delete(remoteFile);
                } else if (Files.isDirectory(remoteFile)) {
                    System.out.println(remoteFile + " - directory");
                }
            }
        }

    }
}
