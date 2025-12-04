package pt.feup.tvvs.soulknight.view.menu;

import pt.feup.tvvs.soulknight.gui.GUI;

import java.io.IOException;

public interface ElementViewer<T> {
    void draw(T model, GUI gui, long time) throws IOException;
}