import com.jediterm.pty.PtyProcessTtyConnector;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.UIUtil;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SerialTerminalExample {

    private static @NotNull JediTermWidget createTerminalWidget() {
        JediTermWidget widget = new JediTermWidget(80, 24, new DefaultSettingsProvider());
        widget.setTtyConnector(createTtyConnector());
        widget.start();
        return widget;
    }

    private static @NotNull TtyConnector createTtyConnector() {
        return new SerialTtyConnector();
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Basic Terminal Shell Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(createTerminalWidget());
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Create and show this application's GUI in the event-dispatching thread.
        SwingUtilities.invokeLater(SerialTerminalExample::createAndShowGUI);
    }
}