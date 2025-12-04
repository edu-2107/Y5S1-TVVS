package pt.feup.tvvs.soulknight.gui;

import com.googlecode.lanterna.screen.Screen;

import java.awt.*;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URISyntaxException;

public interface ScreenGenerator {
    Screen createScreen(RescalableGUI.ResolutionScale resolutionScale, String title, KeyListener keyListener)
            throws IOException, URISyntaxException, FontFormatException;

    int getWidth();
    int getHeight();
}
