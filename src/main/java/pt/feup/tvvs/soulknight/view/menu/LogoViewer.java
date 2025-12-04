package pt.feup.tvvs.soulknight.view.menu;

import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;

import java.io.IOException;

public class LogoViewer {
    private final Sprite logoSprite;

    public LogoViewer(SpriteLoader spriteLoader) throws IOException {
        this.logoSprite = spriteLoader.get("icon/gameIcon.png");
    }

    public void draw(GUI gui, int x, int y) throws IOException {
        logoSprite.draw(gui, x, y);
    }
}
