package com.g3g4x5x6.panel.console;

import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.remote.ssh.DefaultTerminalTypeAheadSettings;
import com.g3g4x5x6.ui.terminal.settings.ColorScheme;
import com.g3g4x5x6.ui.terminal.settings.DefaultColorPaletteImpl;
import com.jediterm.terminal.HyperlinkStyle;
import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.model.TerminalTypeAheadSettings;
import com.jediterm.terminal.ui.TerminalActionPresentation;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collections;
import java.util.Objects;

public class CmdSettingsProvider extends DefaultSettingsProvider {

    private final ColorScheme colorScheme = new ColorScheme(AppConfig.getProperty("terminal.color.scheme"));

    public ColorPalette getTerminalColorPalette() {
        return new DefaultColorPaletteImpl(colorScheme);
    }

    @Override
    public float getLineSpacing() {
        return Float.parseFloat(AppConfig.getProperty("terminal.line.space"));
    }

    @SneakyThrows
    public Font getTerminalFont() {
        // Font SarasaMono-TTF-1.0.13
        // https://github.com/be5invis/Sarasa-Gothic
        Font myFont = Font.createFont(Font.PLAIN, Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("fonts/SarasaMono-TTF-1.0.13/" + "SarasaMonoSC-Regular.ttf")));
        return myFont.deriveFont(Font.PLAIN, (int) this.getTerminalFontSize());
    }

    public float getTerminalFontSize() {
        return Float.parseFloat(AppConfig.getProperty("terminal.font.size"));
    }

    @Override
    public @NotNull TextStyle getDefaultStyle() {
        return new TextStyle(TerminalColor.rgb(colorScheme.getForegroundColor().getRed(), colorScheme.getForegroundColor().getGreen(), colorScheme.getForegroundColor().getBlue()), TerminalColor.rgb(colorScheme.getBackgroundColor().getRed(), colorScheme.getBackgroundColor().getGreen(), colorScheme.getBackgroundColor().getBlue()));
    }

    public @NotNull TextStyle getSelectionColor() {
        return new TextStyle(TerminalColor.rgb(colorScheme.getForegroundColor().getRed(), colorScheme.getForegroundColor().getGreen(), colorScheme.getForegroundColor().getBlue()), TerminalColor.rgb(colorScheme.getSelectedColor().getRed(), colorScheme.getSelectedColor().getGreen(), colorScheme.getSelectedColor().getBlue()));
    }

    public @NotNull TextStyle getFoundPatternColor() {
        return new TextStyle(TerminalColor.rgb(200, 200, 200), TerminalColor.rgb(255, 255, 0));
    }


    public HyperlinkStyle.HighlightMode getHyperlinkHighlightingMode() {
        return HyperlinkStyle.HighlightMode.HOVER;
    }

    public boolean useInverseSelectionColor() {
        return true;
    }

    public boolean copyOnSelect() {
        return this.emulateX11CopyPaste();
    }

    public boolean pasteOnMiddleMouseClick() {
        return this.emulateX11CopyPaste();
    }

    public boolean emulateX11CopyPaste() {
        return false;
    }

    public boolean useAntialiasing() {
        return true;
    }

    public int maxRefreshRate() {
        return 50;
    }

    public boolean audibleBell() {
        return true;
    }

    public boolean enableMouseReporting() {
        return true;
    }

    public int caretBlinkingMs() {
        return 505;
    }

    public boolean scrollToBottomOnTyping() {
        return true;
    }

    public boolean DECCompatibilityMode() {
        return true;
    }

    public boolean forceActionOnMouseReporting() {
        return false;
    }

    public int getBufferMaxLinesCount() {
        return 5000;
    }

    public boolean altSendsEscape() {
        return true;
    }

    public boolean ambiguousCharsAreDoubleWidth() {
        return false;
    }

    @NotNull
    public TerminalTypeAheadSettings getTypeAheadSettings() {
        return DefaultTerminalTypeAheadSettings.DEFAULT;
    }

    @Override
    public boolean sendArrowKeysInAlternativeMode() {
        return false;
    }

    @NotNull
    public TerminalActionPresentation getOpenUrlActionPresentation() {
        return new TerminalActionPresentation("Open as URL", Collections.emptyList());
    }

    @NotNull
    public TerminalActionPresentation getSelectAllActionPresentation() {
        return new TerminalActionPresentation("Select All", Collections.emptyList());
    }
}
