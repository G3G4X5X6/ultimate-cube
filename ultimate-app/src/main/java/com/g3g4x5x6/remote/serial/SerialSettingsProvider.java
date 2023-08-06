package com.g3g4x5x6.remote.serial;

import com.jediterm.terminal.HyperlinkStyle;
import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.emulator.ColorPaletteImpl;
import com.jediterm.terminal.model.TerminalTypeAheadSettings;
import com.jediterm.terminal.ui.TerminalActionPresentation;
import com.jediterm.terminal.ui.UIUtil;
import com.jediterm.terminal.ui.settings.SettingsProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

public class SerialSettingsProvider implements SettingsProvider {

    @NotNull
    public TerminalActionPresentation getNewSessionActionPresentation() {
        return new TerminalActionPresentation("New Session", UIUtil.isMac ? KeyStroke.getKeyStroke(84, 256) : KeyStroke.getKeyStroke(84, 192));
    }

    @NotNull
    public TerminalActionPresentation getOpenUrlActionPresentation() {
        return new TerminalActionPresentation("Open as URL", Collections.emptyList());
    }

    @NotNull
    public TerminalActionPresentation getCopyActionPresentation() {
        KeyStroke keyStroke = UIUtil.isMac ? KeyStroke.getKeyStroke(67, 256) : KeyStroke.getKeyStroke(67, 192);
        return new TerminalActionPresentation("Copy", keyStroke);
    }

    @NotNull
    public TerminalActionPresentation getPasteActionPresentation() {
        KeyStroke keyStroke = UIUtil.isMac ? KeyStroke.getKeyStroke(86, 256) : KeyStroke.getKeyStroke(86, 192);
        return new TerminalActionPresentation("Paste", keyStroke);
    }

    @NotNull
    public TerminalActionPresentation getClearBufferActionPresentation() {
        return new TerminalActionPresentation("Clear Buffer", UIUtil.isMac ? KeyStroke.getKeyStroke(75, 256) : KeyStroke.getKeyStroke(76, 128));
    }

    @NotNull
    public TerminalActionPresentation getPageUpActionPresentation() {
        return new TerminalActionPresentation("Page Up", KeyStroke.getKeyStroke(33, 64));
    }

    @NotNull
    public TerminalActionPresentation getPageDownActionPresentation() {
        return new TerminalActionPresentation("Page Down", KeyStroke.getKeyStroke(34, 64));
    }

    @NotNull
    public TerminalActionPresentation getLineUpActionPresentation() {
        return new TerminalActionPresentation("Line Up", UIUtil.isMac ? KeyStroke.getKeyStroke(38, 256) : KeyStroke.getKeyStroke(38, 128));
    }

    @NotNull
    public TerminalActionPresentation getLineDownActionPresentation() {
        return new TerminalActionPresentation("Line Down", UIUtil.isMac ? KeyStroke.getKeyStroke(40, 256) : KeyStroke.getKeyStroke(40, 128));
    }

    @NotNull
    public TerminalActionPresentation getCloseSessionActionPresentation() {
        return new TerminalActionPresentation("Close Session", UIUtil.isMac ? KeyStroke.getKeyStroke(87, 256) : KeyStroke.getKeyStroke(87, 192));
    }

    @NotNull
    public TerminalActionPresentation getFindActionPresentation() {
        return new TerminalActionPresentation("Find", UIUtil.isMac ? KeyStroke.getKeyStroke(70, 256) : KeyStroke.getKeyStroke(70, 128));
    }

    @NotNull
    public TerminalActionPresentation getSelectAllActionPresentation() {
        return new TerminalActionPresentation("Select All", Collections.emptyList());
    }

    public ColorPalette getTerminalColorPalette() {
        return UIUtil.isWindows ? ColorPaletteImpl.WINDOWS_PALETTE : ColorPaletteImpl.XTERM_PALETTE;
    }

    public Font getTerminalFont() {
        String fontName;
        if (UIUtil.isWindows) {
            fontName = "新宋体";
        } else if (UIUtil.isMac) {
            fontName = "Menlo";
        } else {
            fontName = "Monospaced";
        }

        return new Font(fontName, 0, (int) this.getTerminalFontSize());
    }


    public float getTerminalFontSize() {
        return 14.0F;
    }

    @Override
    public float getLineSpacing() {
        return SettingsProvider.super.getLineSpacing();
    }

    @Override
    public boolean shouldDisableLineSpacingForAlternateScreenBuffer() {
        return SettingsProvider.super.shouldDisableLineSpacingForAlternateScreenBuffer();
    }

    @Override
    public boolean shouldFillCharacterBackgroundIncludingLineSpacing() {
        return SettingsProvider.super.shouldFillCharacterBackgroundIncludingLineSpacing();
    }

    @Override
    public TextStyle getDefaultStyle() {
        return new TextStyle(
                TerminalColor.rgb(
                        UIManager.getColor("Panel.foreground").getRed(),
                        UIManager.getColor("Panel.foreground").getGreen(),
                        UIManager.getColor("Panel.foreground").getBlue()),
                TerminalColor.rgb(
                        UIManager.getColor("Table.background").getRed(),
                        UIManager.getColor("Table.background").getGreen(),
                        UIManager.getColor("Table.background").getBlue()));
    }

//    public void setDefaultStyle(TextStyle textStyle){
//        this.textStyle = textStyle;
//    }

    public TextStyle getSelectionColor() {
        return new TextStyle(TerminalColor.WHITE, TerminalColor.rgb(82, 109, 165));
    }

    public TextStyle getFoundPatternColor() {
        return new TextStyle(TerminalColor.BLACK, TerminalColor.rgb(255, 255, 0));
    }

    public TextStyle getHyperlinkColor() {
        return new TextStyle(TerminalColor.BLACK, TerminalColor.WHITE);
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
        return TerminalTypeAheadSettings.DEFAULT;
    }

    @Override
    public boolean sendArrowKeysInAlternativeMode() {
        return false;
    }
}
