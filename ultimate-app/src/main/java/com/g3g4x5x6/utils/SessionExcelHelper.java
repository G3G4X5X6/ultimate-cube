package com.g3g4x5x6.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.g3g4x5x6.AppConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
public class SessionExcelHelper {
    @SneakyThrows
    public static void exportSessions() {
        // 导出文件
        String file = AppConfig.getWorkPath() + "/export/backup_" + new Date().getTime() + ".xls";
        // 1.创建workbook
        Workbook workbook = new HSSFWorkbook();

        // 2.根据workbook创建sheet
        Sheet sshSheet = workbook.createSheet("SSH");
        Sheet rdpSheet = workbook.createSheet("RDP");
        Sheet vncSheet = workbook.createSheet("VNC");
        Sheet telnetSheet = workbook.createSheet("Telnet");
        // Set Header
        String[] header = {"sessionName", "sessionProtocol", "sessionAddress", "sessionPort", "sessionUser", "sessionPass", "sessionKeyPath", "sessionLoginType", "sessionComment"};
        SessionExcelUtil.createHeaderRow(sshSheet, header);
        SessionExcelUtil.createHeaderRow(rdpSheet, header);
        SessionExcelUtil.createHeaderRow(vncSheet, header);
        SessionExcelUtil.createHeaderRow(telnetSheet, header);

        // 3.写入数据
        for (File sessionFile : FileUtil.listAllSessionFiles()) {
            String json = FileUtils.readFileToString(sessionFile, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSON.parseObject(json);

            String sessionProtocol = jsonObject.getString("sessionProtocol");
            switch (sessionProtocol) {
                case "SSH":
                    log.debug("SSH");
                    SessionExcelUtil.appendJsonToSheet(jsonObject, sshSheet, SessionExcelUtil.getHeaderList(sshSheet));
                    break;
                case "RDP":
                    log.debug("RDP");
                    SessionExcelUtil.appendJsonToSheet(jsonObject, rdpSheet, SessionExcelUtil.getHeaderList(sshSheet));
                case "VNC":
                    log.debug("VNC");
                    SessionExcelUtil.appendJsonToSheet(jsonObject, vncSheet, SessionExcelUtil.getHeaderList(sshSheet));
                case "Telnet":
                    log.debug("Telnet");
                    SessionExcelUtil.appendJsonToSheet(jsonObject, telnetSheet, SessionExcelUtil.getHeaderList(sshSheet));
                default:
                    log.debug("default: nothing to do");
            }
        }

        // 4.通过输出流写到文件里去
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }


    public static void importSessions() {

    }


    public static JSONArray convertExcelToJson(Sheet sheet) {
        JSONArray jsonArray = new JSONArray();

        Row headerRow = sheet.getRow(0);
        int numColumns = headerRow.getPhysicalNumberOfCells();
        int numRows = sheet.getPhysicalNumberOfRows();

        // 获取表头
        List<String> headers = new ArrayList<>();
        for (int j = 0; j < numColumns; j++) {
            Cell headerCell = headerRow.getCell(j);
            headers.add(headerCell.getStringCellValue());
        }

        // 读取数据行
        for (int i = 1; i < numRows; i++) { // 从1开始，跳过表头
            Row row = sheet.getRow(i);
            JSONObject jsonObject = new JSONObject();

            for (int j = 0; j < numColumns; j++) {
                Cell dataCell = row.getCell(j);
                String header = headers.get(j);
                String value = getCellValueAsString(dataCell);
                jsonObject.put(header, value);
            }

            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}

