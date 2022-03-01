import com.jediterm.terminal.ui.UIUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CmdProcess {
    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            Map<String, String> envs = System.getenv();
            String[] command;
            if (UIUtil.isWindows) {
                command = new String[]{"cmd.exe"};
            } else {
                command = new String[]{"/bin/bash", "--login"};
                envs = new HashMap<>(System.getenv());
                envs.put("TERM", "xterm-256color");
            }

            Process process = new ProcessBuilder(command).start();

            //获取输出流并转换成缓冲区
            BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bout.write("whoami");//输出数据
            bout.close();//关闭流

            //SequenceInputStream是一个串联流，能够把两个流结合起来，通过该对象就可以将
            //getInputStream方法和getErrorStream方法获取到的流一起进行查看了，当然也可以单独操作
            SequenceInputStream sis = new SequenceInputStream(process.getInputStream(), process.getErrorStream());
            InputStreamReader inst = new InputStreamReader(sis, "GBK");//设置编码格式并转换为输入流
            BufferedReader br = new BufferedReader(inst);//输入流缓冲区

            String res = null;
            StringBuilder sb = new StringBuilder();
            while ((res = br.readLine()) != null) {//循环读取缓冲区中的数据
                sb.append(res + "\n");
            }
            br.close();
            process.waitFor();
            process.destroy();
            System.out.print(sb);//输出获取的数据

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }
}
