package com.g3g4x5x6.ui.panels.console;

import com.jediterm.terminal.Questioner;
import com.jediterm.terminal.TtyConnector;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;

public class CmdConnector implements TtyConnector {

    protected final InputStream myInputStream;
    protected final OutputStream myOutputStream;
    protected final InputStreamReader myReader;
    protected final String myCharset;
    private final Process myProcess;

    public CmdConnector(@NotNull Process process, @NotNull String charset) throws UnsupportedEncodingException {
        this.myOutputStream = process.getOutputStream();
        this.myCharset = charset;
        this.myInputStream = process.getInputStream();
        this.myReader = new InputStreamReader(this.myInputStream, Charset.defaultCharset());
        this.myProcess = process;
    }

    @Override
    public boolean init(Questioner questioner) {
        return false;
    }

    @Override
    public void close() {
        this.myProcess.destroy();

        try {
            this.myOutputStream.close();
        } catch (IOException var3) {
        }

        try {
            this.myInputStream.close();
        } catch (IOException var2) {
        }
    }

    @Override
    public String getName() {
        return "本地终端";
    }

    @Override
    public int read(char[] buf, int offset, int length) throws IOException {

        return this.myReader.read(buf, offset, length);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        this.myOutputStream.write(bytes);
        this.myOutputStream.flush();
    }

    @Override
    public boolean isConnected() {
        return myProcess.isAlive();
    }

    @Override
    public void write(String string) throws IOException {
        this.write(string.getBytes(this.myCharset));
    }

    @Override
    public int waitFor() throws InterruptedException {
        return this.myProcess.waitFor();
    }
}
