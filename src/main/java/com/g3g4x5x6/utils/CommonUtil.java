package com.g3g4x5x6.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Properties;

public class CommonUtil {
    private CommonUtil(){}

    /**
     * example: 这将获取所有的版本
     * https://api.github.com/repos/januwA/flutter_anime_app/releases
     *
     * 最新版本:
     * https://api.github.com/repos/januwA/flutter_anime_app/releases/latest
     *
     * 下载最新的包
     * https://api.github.com/repos/januwA/flutter_anime_app/releases/latest  // 获取下载地址: r.assets[0].browser_download_url
     *
     */
    public static String getLastestVersion(){
        StringBuffer result = new StringBuffer();
        try {
            URL url = new URL( "https://api.github.com/repos/G3G4X5X6/ultimateshell/releases/latest");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.connect();

            System.out.println("HTTP状态码="+con.getResponseCode());

            BufferedReader inn = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String value = inn.readLine().trim();
            while(value != null){
                if(!"".equals(value)){
                    result.append(value.trim()+"\n");
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

    public static String getCurrentVersion(){
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

    public static void main(String[] args) {
        System.out.println(getLastestVersion());
        System.out.println(getCurrentVersion());
    }
}
