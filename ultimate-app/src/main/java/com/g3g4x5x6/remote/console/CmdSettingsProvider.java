package com.g3g4x5x6.remote.console;

import com.g3g4x5x6.terminal.settings.ColorScheme;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.ui.UIUtil;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;

import java.awt.*;

public class CmdSettingsProvider extends DefaultSettingsProvider {
    private final ColorScheme colorScheme = new ColorScheme("Tomorrow");

    public ColorPalette getTerminalColorPalette() {
        return new CmdColorPaletteImpl(colorScheme);
    }

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
        return new Font(fontName, Font.BOLD, (int) getTerminalFontSize());
    }

    @Override
    public float getTerminalFontSize() {
        return 14;
    }
}
