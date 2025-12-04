package pt.feup.tvvs.soulknight.controller;

import pt.feup.tvvs.soulknight.Game;
import pt.feup.tvvs.soulknight.gui.GUI;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public abstract class Controller<T> {
    private final T model;

    protected Controller(T model) {
        this.model = model;
    }

    public T getModel() {
        return model;
    }

    public abstract void move(Game game, GUI.ACTION action, long time) throws IOException, URISyntaxException, FontFormatException;
}
