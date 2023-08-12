package com.g3g4x5x6.remote.ssh;

import com.g3g4x5x6.AppConfig;
import com.g3g4x5x6.ui.terminal.settings.ColorScheme;
import com.g3g4x5x6.ui.terminal.settings.DefaultColorPaletteImpl;
import com.jediterm.core.Platform;
import com.jediterm.terminal.HyperlinkStyle;
import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.model.TerminalTypeAheadSettings;
import com.jediterm.terminal.ui.TerminalActionPresentation;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Collections;
import java.util.Objects;

public class SshSettingsProvider extends DefaultSettingsProvider {
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
//        String fontName;
//        if (UIUtil.isWindows) {
//            fontName = AppConfig.getProperty("terminal.font");
//        } else if (Platform.current() == Platform.Mac) {
//            fontName = "Menlo";
//        } else {
//            fontName = "Monospaced";
//        }
//        fontName = "楷体";
//        return new Font(fontName, Font.PLAIN, (int) this.getTerminalFontSize());
        Font myFont = Font.createFont(Font.PLAIN, Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("fonts/ttf/" + "JetBrainsMonoNL-Regular.ttf")));
        return myFont.deriveFont(Font.PLAIN, (int) this.getTerminalFontSize());
    }

    @SneakyThrows
    public Font getTerminalChineseFont() {
        return new Font("新宋体", Font.PLAIN, (int) this.getTerminalFontSize());
    }

    public float getTerminalFontSize() {
        return Float.parseFloat(AppConfig.getProperty("terminal.font.size"));
    }

    @Override
    public TextStyle getDefaultStyle() {
        return new TextStyle(
                TerminalColor.rgb(
                        colorScheme.getForegroundColor().getRed(),
                        colorScheme.getForegroundColor().getGreen(),
                        colorScheme.getForegroundColor().getBlue()),
                TerminalColor.rgb(
                        colorScheme.getBackgroundColor().getRed(),
                        colorScheme.getBackgroundColor().getGreen(),
                        colorScheme.getBackgroundColor().getBlue())
        );
    }

    public @NotNull TextStyle getSelectionColor() {
        return new TextStyle(
                TerminalColor.rgb(
                        colorScheme.getForegroundColor().getRed(),
                        colorScheme.getForegroundColor().getGreen(),
                        colorScheme.getForegroundColor().getBlue()),
                TerminalColor.rgb(
                        colorScheme.getSelectedColor().getRed(),
                        colorScheme.getSelectedColor().getGreen(),
                        colorScheme.getSelectedColor().getBlue())
        );
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
    public TerminalActionPresentation getNewSessionActionPresentation() {
        return new TerminalActionPresentation("New Session", Platform.current() == Platform.Mac ? KeyStroke.getKeyStroke(84, InputEvent.META_DOWN_MASK) : KeyStroke.getKeyStroke(84, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
    }

    @NotNull
    public TerminalActionPresentation getOpenUrlActionPresentation() {
        return new TerminalActionPresentation("Open as URL", Collections.emptyList());
    }

    @NotNull
    public TerminalActionPresentation getCopyActionPresentation() {
        KeyStroke keyStroke = Platform.current() == Platform.Mac ? KeyStroke.getKeyStroke(67, InputEvent.META_DOWN_MASK) : KeyStroke.getKeyStroke(67, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
        return new TerminalActionPresentation("Copy", keyStroke);
    }

    @NotNull
    public TerminalActionPresentation getPasteActionPresentation() {
        KeyStroke keyStroke = Platform.current() == Platform.Mac ? KeyStroke.getKeyStroke(86, InputEvent.META_DOWN_MASK) : KeyStroke.getKeyStroke(86, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
        return new TerminalActionPresentation("Paste", keyStroke);
    }

    @NotNull
    public TerminalActionPresentation getClearBufferActionPresentation() {
        return new TerminalActionPresentation("Clear Buffer", Platform.current() == Platform.Mac ? KeyStroke.getKeyStroke(75, InputEvent.META_DOWN_MASK) : KeyStroke.getKeyStroke(76, InputEvent.CTRL_DOWN_MASK));
    }

    @NotNull
    public TerminalActionPresentation getPageUpActionPresentation() {
        return new TerminalActionPresentation("Page Up", KeyStroke.getKeyStroke(33, InputEvent.SHIFT_DOWN_MASK));
    }

    @NotNull
    public TerminalActionPresentation getPageDownActionPresentation() {
        return new TerminalActionPresentation("Page Down", KeyStroke.getKeyStroke(34, InputEvent.SHIFT_DOWN_MASK));
    }

    @NotNull
    public TerminalActionPresentation getLineUpActionPresentation() {
        return new TerminalActionPresentation("Line Up", Platform.current() == Platform.Mac ? KeyStroke.getKeyStroke(38, InputEvent.META_DOWN_MASK) : KeyStroke.getKeyStroke(38, InputEvent.CTRL_DOWN_MASK));
    }

    @NotNull
    public TerminalActionPresentation getLineDownActionPresentation() {
        return new TerminalActionPresentation("Line Down", Platform.current() == Platform.Mac ? KeyStroke.getKeyStroke(40, InputEvent.META_DOWN_MASK) : KeyStroke.getKeyStroke(40, InputEvent.CTRL_DOWN_MASK));
    }

    @NotNull
    public TerminalActionPresentation getCloseSessionActionPresentation() {
        return new TerminalActionPresentation("Close Session", Platform.current() == Platform.Mac ? KeyStroke.getKeyStroke(87, InputEvent.META_DOWN_MASK) : KeyStroke.getKeyStroke(87, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
    }

    @NotNull
    public TerminalActionPresentation getFindActionPresentation() {
        return new TerminalActionPresentation("Find", Platform.current() == Platform.Mac ? KeyStroke.getKeyStroke(70, InputEvent.META_DOWN_MASK) : KeyStroke.getKeyStroke(70, InputEvent.CTRL_DOWN_MASK));
    }

    @NotNull
    public TerminalActionPresentation getSelectAllActionPresentation() {
        return new TerminalActionPresentation("Select All", Collections.emptyList());
    }
}
