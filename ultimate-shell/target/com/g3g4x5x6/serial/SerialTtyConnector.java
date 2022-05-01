package com.g3g4x5x6.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.jediterm.terminal.Questioner;
import com.jediterm.terminal.TtyConnector;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


@Slf4j
public class SerialTtyConnector implements TtyConnector {

    private InputStream myInputStream;
    private OutputStream myOutputStream;
    private InputStreamReader myInputStreamReader;

    private SerialPort comPort;


    public SerialTtyConnector() {
        this.myInputStream = null;
        this.myOutputStream = null;
    }

    public SerialTtyConnector(SerialPort comPort) {
        this.comPort = comPort;
        this.myInputStream = this.comPort.getInputStream();
        this.myInputStreamReader = new InputStreamReader(this.myInputStream);
        this.myOutputStream = this.comPort.getOutputStream();
    }


    @Override
    public boolean init(Questioner questioner) {
        return true;
    }

    @Override
    public void close() {
        log.debug("Closed comPort");
        comPort.closePort();
    }

    @Override
    public void resize(@NotNull Dimension termWinSize) {
        TtyConnector.super.resize(termWinSize);
    }

    @Override
    public String getName() {
        return comPort.getDescriptivePortName();
    }

    @Override
    public int read(char[] chars, int i, int i1) throws IOException {
        return this.myInputStreamReader.read(chars, i, i1);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        this.myOutputStream.write(bytes);
        this.myOutputStream.flush();
    }

    @Override
    public boolean isConnected() {
        return comPort.isOpen();
    }

    @Override
    public void write(String s) throws IOException {
        this.myOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int waitFor() {
        return 0;
    }

    @Override
    public boolean ready() {
        return false;
    }
}
