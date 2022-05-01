package com.g3g4x5x6;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Slf4j
public class DefaultTrayIcon extends TrayIcon {

    private final JDialog dialog;

    /**
     * 构造方法，创建带指定图像、工具提示和弹出菜单的 DefaultTrayIcon
     *
     * @param image     显示在系统托盘的图标
     * @param tips      鼠标移动到系统托盘图标上的提示信息
     * @param popupMenu 弹出菜单
     */
    public DefaultTrayIcon(Image image, String tips, JPopupMenu popupMenu) {
        super(image, tips);

        //初始化JDialog
        dialog = new JDialog();
        dialog.setPreferredSize(new Dimension(1,1));
        dialog.setSize(new Dimension(1,1));
        dialog.setUndecorated(true);//取消窗体装饰
        dialog.setAlwaysOnTop(true);//设置窗体始终位于上方

        //设置系统图标大小为自动调整
        this.setImageAutoSize(true);

        //为TrayIcon设置鼠标监听器
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {

                //鼠标右键在组件上释放时调用，显示弹出菜单
                if (e.getButton() == MouseEvent.BUTTON3 && popupMenu != null) {

                    Dimension size = popupMenu.getPreferredSize();
                    log.debug(size.toString());

                    //设置dialog的显示位置
                    dialog.setLocation(e.getX() - size.width - 300, e.getY() - size.height - 300);
                    dialog.setVisible(true);

                    //显示弹出菜单 popupMenu
                    popupMenu.show(dialog.getContentPane(), 0, 0);
                }
            }
        });

        //为弹出菜单添加监听器
        popupMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                dialog.setVisible(false);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                dialog.setVisible(false);
            }
        });

    }
}
