package com.g3g4x5x6.utils;

import com.alibaba.fastjson.JSONObject;
import com.g3g4x5x6.remote.utils.VaultUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;

public class SessionExcelUtil {
    // 创建表头的方法
    public static void createHeaderRow(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    // 获取表头顺序的方法
    public static List<String> getHeaderList(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        List<String> headers = new ArrayList<>();

        for (Cell cell : headerRow) {
            headers.add(cell.getStringCellValue());
        }

        return headers;
    }

    // 向 sheet 中追加 JSON 数据的方法
    public static void appendJsonToSheet(JSONObject jsonObject, Sheet sheet, List<String> headers) {
        int lastRowNum = sheet.getLastRowNum();
        Row newRow = sheet.createRow(lastRowNum + 1);

        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            Cell dataCell = newRow.createCell(i);
            if (jsonObject.containsKey(header)) {
                if (header.equals("sessionPass")) {
                    dataCell.setCellValue(VaultUtil.decryptPasswd(jsonObject.getString("sessionPass")));
                } else {
                    dataCell.setCellValue(jsonObject.getString(header).strip());
                }
            } else {
                dataCell.setCellValue(""); // 如果JSON对象中没有该表头对应的值，则填充空字符串
            }
        }
    }
}
