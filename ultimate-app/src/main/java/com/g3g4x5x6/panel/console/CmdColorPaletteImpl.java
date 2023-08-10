package com.g3g4x5x6.panel.console;

import com.g3g4x5x6.ui.terminal.settings.ColorScheme;
import com.jediterm.core.Color;
import com.jediterm.terminal.emulator.ColorPalette;
import org.jetbrains.annotations.NotNull;

public class CmdColorPaletteImpl extends ColorPalette {
    private final com.jediterm.core.Color[] myColors;

    public CmdColorPaletteImpl(@NotNull ColorScheme colorScheme) {
        myColors = colorScheme.getColors();
    }

    @Override
    public com.jediterm.core.@NotNull Color getForegroundByColorIndex(int colorIndex) {
        return myColors[colorIndex];
    }

    @Override
    protected @NotNull Color getBackgroundByColorIndex(int colorIndex) {
        return myColors[colorIndex];
    }
}
