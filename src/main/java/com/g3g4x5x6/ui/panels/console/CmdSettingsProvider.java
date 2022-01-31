package com.g3g4x5x6.ui.panels.console;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.ui.UIUtil;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;

import javax.swing.*;
import java.awt.*;

public class CmdSettingsProvider extends DefaultSettingsProvider {
    private TextStyle textStyle;

    @Override
    public Font getTerminalFont() {
        String fontName;
        if (UIUtil.isWindows) {
            fontName = "新宋体";
        } else if (UIUtil.isMac) {
            fontName = "Menlo";
        } else {
            fontName = "Monospaced";
        }
        return new Font(fontName, Font.PLAIN, (int)getTerminalFontSize());
    }

    @Override
    public float getTerminalFontSize() {
        return 14;
    }

    @Override
    public TextStyle getDefaultStyle() {
        return new TextStyle(
                TerminalColor.rgb(
                        UIManager.getColor("Panel.foreground").getRed(),
                        UIManager.getColor("Panel.foreground").getGreen(),
                        UIManager.getColor("Panel.foreground").getBlue()),
                TerminalColor.rgb(
                        UIManager.getColor("Panel.background").getRed(),
                        UIManager.getColor("Panel.background").getGreen(),
                        UIManager.getColor("Panel.background").getBlue()));
    }

    public void setDefaultStyle(TextStyle textStyle){
        this.textStyle = textStyle;
    }
}
