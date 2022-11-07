package com.g3g4x5x6.remote.console;

import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.ui.settings.ColorScheme;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class CmdColorPaletteImpl extends ColorPalette {
    private final Color[] myColors;

    public CmdColorPaletteImpl(@NotNull ColorScheme colorScheme) {
        myColors = colorScheme.getColors();
    }

    @NotNull
    @Override
    public Color getForegroundByColorIndex(int colorIndex) {
        return myColors[colorIndex];
    }

    @NotNull
    @Override
    protected Color getBackgroundByColorIndex(int colorIndex) {
        return myColors[colorIndex];
    }
}
