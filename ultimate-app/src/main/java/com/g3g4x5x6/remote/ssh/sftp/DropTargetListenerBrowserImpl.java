package com.g3g4x5x6.remote.ssh.sftp;

import com.g3g4x5x6.exception.UserStopException;
import com.g3g4x5x6.remote.ssh.panel.SshTabbedPane;
import com.g3g4x5x6.remote.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;


@Slf4j
public class DropTargetListenerBrowserImpl implements DropTargetListener {
    private SftpFileSystem fs;
    private String currentPath;

    public DropTargetListenerBrowserImpl(SftpFileSystem fs, String currentPath) {
        this.fs = fs;
        this.currentPath = currentPath;
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
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

        //检测拖放进来的数据类型
        Transferable transfer = dtde.getTransferable();
        DataFlavor flav = check(transfer);
        if (flav != null) {
            //必须先调用acceptDrop
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

            try {
                // 调用 getTransferData() 来取得数据
                List<File> files = (List) transfer.getTransferData(flav);

                // TODO
                LinkedList<File> chooserFile = new LinkedList<>();
                LinkedList<File> fileList = new LinkedList<>();
                for (File file : files) {
                    chooserFile.add(file);  // 为了对比生成远程路径
                    FileUtil.traverseFolder(file, fileList);
                }
                TaskProgressPanel taskPanel = new TaskProgressPanel("上传", 0, 100, "");
                SshTabbedPane.taskPopupMenu.add(taskPanel);
                new Thread(() -> {
                    int fileCount = fileList.size();
                    for (File file : fileList) {
                        log.debug("file: " + file.getAbsolutePath());
                        taskPanel.setFileCount(--fileCount);
                        taskPanel.setTaskLabel(file.getAbsolutePath());
                        taskPanel.setMin(0);
                        taskPanel.setMax((int) file.length());
                        try {
                            String finalPath = "";
                            // 对比生成远程路径
                            for (File f : chooserFile) {
                                if (file.getAbsolutePath().contains(f.getAbsolutePath())) {
                                    finalPath = Path.of(currentPath, file.getAbsolutePath().substring(f.getParent().length())).toString();
                                }
                            }
                            if (Files.isDirectory(fs.getPath(finalPath))) {
                                Files.createDirectories(fs.getPath(finalPath));
                            } else {
                                Files.createDirectories(fs.getPath(finalPath).getParent());
                            }
                            // 开始上传文件
                            FileInputStream fis = new FileInputStream(file);
                            OutputStream outputStream = Files.newOutputStream(fs.getPath(finalPath));
                            byte[] data = new byte[1024 * 8];   // 缓冲区
                            int len;        // 创建长度
                            int sendLen = 0;    // 已发送长度
                            while ((len = fis.read(data)) != -1) {
                                outputStream.write(data, 0, len);
                                outputStream.flush();
                                sendLen += len;
                                taskPanel.setProgressBarValue(sendLen);
                                if (taskPanel.isTerminate())
                                    throw new UserStopException("用户终止任务");
                            }
                        } catch (IOException | UserStopException e) {
                            log.debug(e.getMessage());
                        }
                    }
                }).start();
            } catch (Exception e) {
                log.debug(e.getMessage());
            }

        }
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

    public void updateFs(SftpFileSystem fileSystem) {
        this.fs = fileSystem;
    }

}
