import com.g3g4x5x6.ui.panels.ssh.SshSettingsProvider;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TelnetTerminalExample {

    private static @NotNull JediTermWidget createTerminalWidget() {
        JediTermWidget widget = new JediTermWidget(80, 24, new SshSettingsProvider());
        widget.setTtyConnector(createTtyConnector());
        widget.start();
        return widget;
    }

    private static @NotNull TtyConnector createTtyConnector() {
        return new TelnetTtyConnector("172.17.200.12", 23);
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
        SwingUtilities.invokeLater(TelnetTerminalExample::createAndShowGUI);
    }
}