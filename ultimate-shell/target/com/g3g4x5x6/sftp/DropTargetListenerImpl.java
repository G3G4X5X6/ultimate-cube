package com.g3g4x5x6.sftp;

import lombok.extern.slf4j.Slf4j;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

@Slf4j
public class DropTargetListenerImpl implements DropTargetListener {
    private SftpBrowser sftpBrowser;

    public DropTargetListenerImpl(SftpBrowser sftpBrowser) {
        this.sftpBrowser = sftpBrowser;
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        log.debug("dragEnter: 拖拽目标进入组件区域");
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
    }
}
