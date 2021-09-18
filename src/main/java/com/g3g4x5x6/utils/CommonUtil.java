package com.g3g4x5x6.utils;

import com.alibaba.fastjson.JSONObject;
import com.fazecast.jSerialComm.SerialPort;
import com.github.jarod.qqwry.IPZone;
import com.github.jarod.qqwry.QQWry;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Properties;

public class CommonUtil {
    private static HashSet<SerialPort> ports = new HashSet<>();

    private CommonUtil() {
    }

    static {
        for (SerialPort port : SerialPort.getCommPorts()){
            ports.add(port);
        }
    }

    public static HashSet<SerialPort> getCommPorts(){
        return ports;
    }

    /**
     * 未测试
     */
    public static void updateCommPorts(){
        for (SerialPort port : SerialPort.getCommPorts()){
            ports.add(port);
        }
    }

    public static CharsetMatch checkCharset(InputStream input) {
        //		BufferedInputStream bis = new BufferedInputStream(input);
        CharsetDetector cd = new CharsetDetector();
        try {
            cd.setText(input);
        } catch (IOException e) {
            try {
                input.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        CharsetMatch cm = cd.detect();
        return cm;
    }


    /**
     * example: 这将获取所有的版本
     * https://api.github.com/repos/januwA/flutter_anime_app/releases
     * <p>
     * 最新版本:
     * https://api.github.com/repos/januwA/flutter_anime_app/releases/latest
     * <p>
     * 下载最新的包
     * https://api.github.com/repos/januwA/flutter_anime_app/releases/latest  // 获取下载地址: r.assets[0].browser_download_url
     */
    public static String getLastestVersion() {
        StringBuffer result = new StringBuffer();
        try {
            URL url = new URL("https://api.github.com/repos/G3G4X5X6/ultimateshell/releases/latest");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.connect();

            System.out.println("HTTP状态码=" + con.getResponseCode());

            BufferedReader inn = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String value = inn.readLine().trim();
            while (value != null) {
                if (!"".equals(value)) {
                    result.append(value.trim() + "\n");
                }
                value = inn.readLine();
            }

            inn.close();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject object = JSONObject.parseObject(result.toString());
        String tagName = object.getString("tag_name");
        System.out.println(tagName);
        return tagName;
    }

    public static String getCurrentVersion() {
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = CommonUtil.class.getClassLoader().getResourceAsStream("info.properties");
        // 使用properties对象加载输入流
        try {
            properties.load(in);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        //获取key对应的value值
        String currentVersion = properties.getProperty("version");
        return currentVersion;
    }

    public static void generateSystemInfo() {
        File temp = new File(ConfigUtil.getWorkPath() + "/temp");
        if (!temp.exists()) {
            temp.mkdir();
        }
        String fileName = temp.getAbsolutePath() + "/systeminfo.txt";
        String output = exec("systeminfo");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(output);
            writer.flush();
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static String getSystemInfo() {
        File temp = new File(ConfigUtil.getWorkPath() + "/temp");
        if (!temp.exists()) {
            temp.mkdir();
        }
        String fileName = temp.getAbsolutePath() + "/systeminfo.txt";
        StringBuffer systemInfo = new StringBuffer();
        String tempStr = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            while ((tempStr = reader.readLine()) != null) {
                systemInfo.append(tempStr + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return systemInfo.toString();
    }

    public static String exec(String cmd) {
        StringBuffer b = new StringBuffer();
        Runtime runtime = Runtime.getRuntime();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(runtime.exec(cmd).getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                b.append(line + "\n");
            }
            System.out.println(b.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b.toString();
    }

    public static String queryIp(String ip){
        QQWry qqwry = null; // load qqwry.dat from classpath
        try {
            qqwry = new QQWry();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        IPZone ipzone = qqwry.findIP(ip);
        System.out.printf("%s, %s", ipzone.getMainInfo(), ipzone.getSubInfo());
        return String.format("%s, %s", ipzone.getMainInfo(), ipzone.getSubInfo());
    }

    public static Boolean isWin(){
        return true;
    }

    public static Boolean isLinux(){
        return true;
    }

    public static Boolean isMac(){
        return true;
    }

    public static void main(String[] args) {
//        System.out.println(getLastestVersion());
//        System.out.println(getCurrentVersion());
//        generateSystemInfo();
        System.out.println(getSystemInfo());
    }
}
