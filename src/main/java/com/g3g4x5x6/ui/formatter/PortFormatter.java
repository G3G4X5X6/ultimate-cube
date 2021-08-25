package com.g3g4x5x6.ui.formatter;

import javax.swing.text.DefaultFormatter;
import java.text.ParseException;

public class PortFormatter extends DefaultFormatter {
    @Override
    public Object stringToValue(String string) throws ParseException {
        int port = 22;
        try {
            port = Integer.parseInt(string);
            if (port > 65535 || port < 1) {
                throw new ParseException("Out of range", 0);
            }
        } catch (NumberFormatException e) {
            throw new ParseException("Not an integer", 0);
        }

        return port;
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        return super.valueToString(value);
    }
}
