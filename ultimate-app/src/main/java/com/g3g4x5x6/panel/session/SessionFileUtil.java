package com.g3g4x5x6.panel.session;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.g3g4x5x6.utils.FileUtil;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class SessionFileUtil {
    @SneakyThrows
    public static HashMap<String, ArrayList<JSONObject>> getCategoriesMap() {
        ArrayList<File> sessionFiles = FileUtil.listAllSessionFiles();
        HashMap<String, ArrayList<JSONObject>> categoriesMaps = new HashMap<>();
        for (File sessionFile : sessionFiles) {
            String json = FileUtils.readFileToString(sessionFile, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSON.parseObject(json);
            jsonObject.put("sessionFilePath", sessionFile.getAbsolutePath());
            ArrayList<JSONObject> arrayList = categoriesMaps.get(jsonObject.getString("sessionCategory"));
            if (arrayList == null) {
                arrayList = new ArrayList<>();
            }
            arrayList.add(jsonObject);
            categoriesMaps.put(jsonObject.getString("sessionCategory"), arrayList);
        }
        return categoriesMaps;
    }

    @SneakyThrows
    public static HashMap<String, ArrayList<JSONObject>> getProtocolsMap() {
        ArrayList<File> sessionFiles = FileUtil.listAllSessionFiles();
        HashMap<String, ArrayList<JSONObject>> protocolsMaps = new HashMap<>();
        for (File sessionFile : sessionFiles) {
            String json = FileUtils.readFileToString(sessionFile, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSON.parseObject(json);
            jsonObject.put("sessionFilePath", sessionFile.getAbsolutePath());

            ArrayList<JSONObject> arrayList = protocolsMaps.get(jsonObject.getString("sessionProtocol"));
            if (arrayList == null) {
                arrayList = new ArrayList<>();
            }
            arrayList.add(jsonObject);
            protocolsMaps.put(jsonObject.getString("sessionProtocol"), arrayList);
        }
        return protocolsMaps;
    }

}
