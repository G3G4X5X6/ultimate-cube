package com.g3g4x5x6.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.common.SftpConstants;

import java.util.Arrays;


@Slf4j
public class TableUtil {
    private TableUtil(){}

    public static String[] convertFileLongNameToStringArray(SftpClient.DirEntry entry, boolean isAddTag) {
        // 文件名, 权限, 大小, 类型, 属组, 修改时间
        String[] temp = new String[6];
        temp[0] = entry.getFilename();
        temp[1] = entry.getLongFilename().split("\\s+")[0];
        log.debug(Arrays.toString(entry.getLongFilename().split("\\s+")));

        // 大小单位转换
        String humanSize = "";
        long size = entry.getAttributes().getSize();
        if (size >= 1024 && size < 1024 * 1024) { // KB
            double d = size / 1024.0;
            humanSize = String.format("%.2f", d) + " KB";
        } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {   // MB
            double d = size / 1024.0 / 1024.0;
            humanSize = String.format("%.2f", d) + " MB";
        } else if (size >= 1024 * 1024 * 1024) {  // GB
            double d = size / 1024.0 / 1024.0 / 1024.0;
            humanSize = String.format("%.2f", d) + " GB";
        }
        temp[2] = humanSize;

        /**
         *     public static final int SSH_FILEXFER_TYPE_REGULAR = 1;
         *     public static final int SSH_FILEXFER_TYPE_DIRECTORY = 2;
         *     public static final int SSH_FILEXFER_TYPE_SYMLINK = 3;
         *     public static final int SSH_FILEXFER_TYPE_SPECIAL = 4;
         *     public static final int SSH_FILEXFER_TYPE_UNKNOWN = 5;
         *     public static final int SSH_FILEXFER_TYPE_SOCKET = 6; // v5
         *     public static final int SSH_FILEXFER_TYPE_CHAR_DEVICE = 7; // v5
         *     public static final int SSH_FILEXFER_TYPE_BLOCK_DEVICE = 8; // v5
         *     public static final int SSH_FILEXFER_TYPE_FIFO = 9; // v5
         */
        switch (entry.getAttributes().getType()) {
            case SftpConstants.SSH_FILEXFER_TYPE_REGULAR:
                temp[3] = "Regular";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_DIRECTORY:
                temp[3] = "Directory";
                if (isAddTag)
                    temp[0] = "DIR:" + temp[0];
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_SYMLINK:
                temp[3] = "Symlink";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_SPECIAL:
                temp[3] = "Special";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_UNKNOWN:
                temp[3] = "Unknown";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_SOCKET:
                temp[3] = "Socket";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_CHAR_DEVICE:
                temp[3] = "Char_Device";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_BLOCK_DEVICE:
                temp[3] = "Block_Device";
                break;
            case SftpConstants.SSH_FILEXFER_TYPE_FIFO:
                temp[3] = "FIFO";
                break;
        }
        temp[4] = entry.getLongFilename().split("\\s+")[2] + "/" + entry.getLongFilename().split("\\s+")[3];
        temp[5] = entry.getAttributes().getModifyTime().toString();

        return temp;
    }

}
