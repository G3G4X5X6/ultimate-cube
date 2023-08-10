package com.g3g4x5x6.panel.console;

import com.g3g4x5x6.ui.terminal.settings.ColorScheme;
import com.jediterm.core.Platform;
import com.jediterm.terminal.emulator.ColorPalette;
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
        if (Platform.current() == Platform.Windows) {
            fontName = "新宋体";
        } else if (Platform.current() == Platform.Mac) {
            fontName = "Menlo";
        } else {
            fontName = "Monospaced";
        }
        return new Font(fontName, Font.PLAIN, (int) getTerminalFontSize());
    }

    @Override
    public float getTerminalFontSize() {
        return 15;
    }
}
