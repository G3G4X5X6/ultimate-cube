package com.g3g4x5x6.remote.ssh;

import com.g3g4x5x6.remote.utils.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.List;


@Slf4j
public class DropTargetListenerImpl implements DropTargetListener {
    private final DefaultTtyConnector ttyConnector;
    private final SftpFileSystem fs;

    public DropTargetListenerImpl(DefaultTtyConnector ttyConnector) throws IOException {
        this.ttyConnector = ttyConnector;
        this.fs = SessionUtil.getSftpFileSystem(ttyConnector.getSession());
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        log.debug("dragEnter: 拖拽目标进入组件区域");
        //检测拖放进来的数据类型
        Transferable transfer = dtde.getTransferable();
        DataFlavor flav = check(transfer);
        if (flav == null) {
            //没有需要的类型，拒绝进入
            dtde.rejectDrag();
        }
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        log.debug("dragOver: 拖拽目标在组件区域内移动");
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        log.debug("dropActionChanged: 当前 drop 操作被修改");
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        log.debug("dragExit: 拖拽目标离开组件区域");
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        log.debug("drop: 拖拽目标在组件区域内释放");
        //必须先调用acceptDrop
        dtde.acceptDrop(DnDConstants.ACTION_COPY);

        //检测拖放进来的数据类型
        Transferable transfer = dtde.getTransferable();
        DataFlavor flav = check(transfer);
        if (flav != null) {
            //必须先调用acceptDrop
            dtde.acceptDrop(DnDConstants.ACTION_COPY);

            try {
                // 调用 getTransferData() 来取得数据
                List<File> files = (List) transfer.getTransferData(flav);

                // TODO ttyConnector
                String path = SessionUtil.getSftpFileSystem(ttyConnector.getSession()).getDefaultDir().toString();
                log.debug("Upload Path: " + fs.getDefaultDir().toString());

                upload(files, path);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void upload(List<File> files, String savePath) {
        log.debug("保存路径：" + savePath);
//        TaskProgressPanel taskPanel = new TaskProgressPanel("上传", 0, 100, "");
//        sftpBrowser.getSftpTabbedPanel().addTask(taskPanel);
//        new Thread(() -> {
//            int fileCount = files.size();
//            for (File file : files) {
//                log.debug(file.getAbsolutePath());
//                taskPanel.setFileCount(fileCount);
//                taskPanel.setTaskLabel(file.getAbsolutePath());
//                taskPanel.setMin(0);
//                taskPanel.setMax((int) file.length());
//
//                String subPath = file.getAbsolutePath().substring(file.getParent().length());
//                log.debug("SubPath: " + subPath);
//                try {
//                    String finalPath = Path.of(savePath, subPath).toString();
//
//                    if (Files.isDirectory(fs.getPath(finalPath))) {
//                        Files.createDirectories(fs.getPath(finalPath));
//                    } else {
//                        Files.createDirectories(fs.getPath(finalPath).getParent());
//                    }
//                    FileInputStream fis = new FileInputStream(file);
//                    OutputStream outputStream = Files.newOutputStream(fs.getPath(finalPath));
//                    byte[] data = new byte[1024 * 8];   // 缓冲区
//                    int len = 0;        // 创建长度
//                    int sendLen = 0;    // 已发送长度
//                    while ((len = fis.read(data)) != -1) {
//                        outputStream.write(data, 0, len);
//                        outputStream.flush();
//                        sendLen += len;
//                        taskPanel.setProgressBarValue(sendLen);
//                    }
//                    fileCount -= 1;
//                } catch (IOException fileNotFoundException) {
//                    fileNotFoundException.printStackTrace();
//                }
//            }
//        }).start();
    }

    // 拖放文件进来时，MIME类型: application/x-java-file-list; class=java.util.List
    private DataFlavor check(Transferable transfer) {
        // 或者写成  flv = new DataFlavor("application/x-java-file-list; class=java.util.List")
        DataFlavor flv = DataFlavor.javaFileListFlavor;
        if (transfer.isDataFlavorSupported(flv)) {
            return flv;
        }
        return null;
    }
}
