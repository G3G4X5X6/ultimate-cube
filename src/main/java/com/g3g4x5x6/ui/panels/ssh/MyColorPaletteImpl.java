package com.g3g4x5x6.ui.panels.ssh;

import com.jediterm.terminal.emulator.ColorPalette;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MyColorPaletteImpl extends ColorPalette {

    private final Color[] myColors;

    public MyColorPaletteImpl(@NotNull ColorScheme colorScheme) {
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
