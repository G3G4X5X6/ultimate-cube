package com.g3g4x5x6.remote.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fazecast.jSerialComm.SerialPort;
import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.remote.utils.os.OsInfoUtil;
import com.github.jarod.qqwry.IPZone;
import com.github.jarod.qqwry.QQWry;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.BLUE_TEXT;

@Slf4j
public class CommonUtil {
    private static HashSet<SerialPort> ports = new HashSet<>();

    private CommonUtil() {
    }

    static {
        for (SerialPort port : SerialPort.getCommPorts()) {
            ports.add(port);
        }
    }

    public static HashSet<SerialPort> getCommPorts() {
        return ports;
    }

    /**
     * 未测试
     */
    public static void updateCommPorts() {
        for (SerialPort port : SerialPort.getCommPorts()) {
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

    public static void setClipboardText(String text) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(text);
        clip.setContents(tText, null);
    }


    /**
     * example: 这将获取所有的版本
     * https://api.github.com/repos/G3G4X5X6/ultimateshell/releases
     * <p>
     * 最新版本:
     * https://api.github.com/repos/G3G4X5X6/ultimateshell/releases/latest
     * <p>
     * 下载最新的包
     * https://api.github.com/repos/G3G4X5X6/ultimateshell/releases/latest  // 获取下载地址: r.assets[0].browser_download_url
     */
    public static String getLastestVersion() {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL("https://api.github.com/repos/G3G4X5X6/ultimateshell/releases/latest");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.connect();

            BufferedReader inn = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String value = inn.readLine().trim();
            while (value != null) {
                if (!"".equals(value)) {
                    result.append(value.trim()).append("\n");
                }
                value = inn.readLine();
            }

            inn.close();
        } catch (IOException e) {
            log.error("获取程序更新信息异常");
            e.printStackTrace();
        }

        JSONObject object = JSONObject.parseObject(result.toString());
        return object.getString("tag_name");
    }

    public static void getLatestJar() {
        StringBuilder result = new StringBuilder();
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
                    result.append(value.trim()).append("\n");
                }
                value = inn.readLine();
            }

            inn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject object = JSONObject.parseObject(result.toString());
        JSONArray upload = object.getJSONArray("assets");
        ListIterator<Object> iterator = upload.listIterator();
        String browser_download_url = "https://github.com/G3G4X5X6/ultimateshell/releases/";
        while (iterator.hasNext()) {
            JSONObject obj = (JSONObject) iterator.next();
            if (OsInfoUtil.isLinux() || OsInfoUtil.isMacOS()) {
                if (obj.getString("name").contains("jar-with-dependencies.jar")) {
                    browser_download_url = obj.getString("browser_download_url");
                }
            } else if (OsInfoUtil.isWindows()) {
                if (obj.getString("name").contains("setup.exe")) {
                    browser_download_url = obj.getString("browser_download_url");
                }
            }
        }
        System.out.println(browser_download_url);
        try {
            Desktop.getDesktop().browse(new URL(browser_download_url).toURI());
        } catch (IOException | URISyntaxException exception) {
            exception.printStackTrace();
        }
    }

    @Deprecated
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
        return properties.getProperty("version");
    }

    @Deprecated
    public static String getBuildOn() {
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
        String currentVersion = properties.getProperty("build");
        return currentVersion.strip();
    }

    public static void generateSystemInfo() {
        File temp = new File(AppConfig.getWorkPath() + "/temp");
        if (!temp.exists()) {
            boolean ignored = temp.mkdir();
        }
        String fileName = temp.getAbsolutePath() + "/systeminfo.txt";
        String output = exec("systeminfo");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8));
            writer.write(output);
            writer.flush();
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static String getSystemInfo() {
        File temp = new File(AppConfig.getWorkPath() + "/temp");
        if (!temp.exists()) {
            boolean ignored = temp.mkdir();
        }
        String fileName = temp.getAbsolutePath() + "/systeminfo.txt";
        StringBuilder systemInfo = new StringBuilder();
        String tempStr;
        try {
            CharsetMatch cm = CommonUtil.checkCharset(new BufferedInputStream(new FileInputStream(fileName)));
            BufferedReader reader = new BufferedReader(cm.getReader());
            while ((tempStr = reader.readLine()) != null) {
                systemInfo.append(tempStr).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return systemInfo.toString();
    }

    public static String exec(String cmd) {
        StringBuilder b = new StringBuilder();
        Runtime runtime = Runtime.getRuntime();
        try {
            CharsetMatch cm = CommonUtil.checkCharset(runtime.exec(cmd).getInputStream());
            BufferedReader br = new BufferedReader(cm.getReader());
            String line = null;
            while ((line = br.readLine()) != null) {
                b.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b.toString();
    }

    public static String queryIp(String ip) {
        QQWry qqwry = null; // load qqwry.dat from classpath
        try {
            qqwry = new QQWry();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        assert qqwry != null;
        IPZone ipzone = qqwry.findIP(ip);
//        System.out.printf("%s, %s", ipzone.getMainInfo(), ipzone.getSubInfo());
        return String.format("%s, %s", ipzone.getMainInfo(), ipzone.getSubInfo());
    }

    public static Boolean isWin() {
        return true;
    }

    public static Boolean isLinux() {
        return true;
    }

    public static Boolean isMac() {
        return true;
    }

    public static void terminalOutput(String msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String threadName = Thread.currentThread().getName();
        System.out.println(colorize(sdf.format(new Date()) + " INFO [" + threadName + "] - " + msg, BLUE_TEXT()));
    }

    public static void initGlobalFont(Font font) {
        FontUIResource fontResource = new FontUIResource(font);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                System.out.println(key);
                UIManager.put(key, fontResource);
            }
        }
    }

}
