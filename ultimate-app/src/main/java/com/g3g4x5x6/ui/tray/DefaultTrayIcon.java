package com.g3g4x5x6.ui.tray;

import com.g3g4x5x6.App;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Slf4j
public class DefaultTrayIcon extends TrayIcon {
    private final JDialog dialog = new JDialog();

    /**
     * 构造方法，创建带指定图像、工具提示和弹出菜单的 DefaultTrayIcon
     *
     * @param image     显示在系统托盘的图标
     * @param tips      鼠标移动到系统托盘图标上的提示信息
     * @param popupMenu 弹出菜单
     */
    public DefaultTrayIcon(Image image, String tips, JPopupMenu popupMenu) {
        super(image, tips);

        // 设置系统图标大小为自动调整
        this.setImageAutoSize(true);

        dialog.setUndecorated(true);
        dialog.setPreferredSize(new Dimension(1, 1));
        dialog.setSize(new Dimension(1, 1));

        // 为TrayIcon设置鼠标监听器
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1: {
                        log.debug("托盘图标被鼠标左键被点击");
                        App.openApp();
                        break;
                    }
                    case MouseEvent.BUTTON2: {
                        log.debug("托盘图标被鼠标中键被点击");
                        break;
                    }
                    case MouseEvent.BUTTON3: {
                        log.debug("托盘图标被鼠标右键被点击，X:" + e.getX() + " <=> Y:" + e.getY());

                        // 鼠标右键在组件上释放时调用，显示弹出菜单
                        if (popupMenu != null) {
                            // 获取 右键菜单大小
                            Dimension size = popupMenu.getPreferredSize();
                            log.debug(size.toString());

                            // 设置dialog的显示位置
                            dialog.setLocation(e.getX(), (int) (e.getY() - size.getHeight()));
                            dialog.setVisible(true);
                            log.debug("SS: " + dialog.getX() + " : " + dialog.getY());

                            // 显示弹出菜单 popupMenu
                            popupMenu.show(dialog, 0, 0);
                        }
                        break;
                    }
                    default: {
                        break;
                    }
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
