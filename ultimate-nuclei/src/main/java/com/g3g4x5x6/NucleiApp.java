package com.g3g4x5x6;

import com.formdev.flatlaf.FlatLightLaf;
import com.g3g4x5x6.nuclei.NucleiFrame;
import com.g3g4x5x6.nuclei.ultils.NucleiConfig;

import javax.swing.*;

public class NucleiApp {


    public static void main(String[] args) {
        initFlatLaf();
        NucleiFrame nuclei = new NucleiFrame();
        nuclei.setTitle(NucleiConfig.getProperty("nuclei.title"));
        nuclei.setDefaultCloseOperation(NucleiFrame.EXIT_ON_CLOSE);
        nuclei.setVisible(true);
    }

    private static void initFlatLaf() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        UIManager.put("TextComponent.arc", 5);
    }
}
