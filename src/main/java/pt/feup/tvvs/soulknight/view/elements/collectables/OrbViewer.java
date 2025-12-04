package pt.feup.tvvs.soulknight.view.elements.collectables;

import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.game.elements.collectables.Collectables;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;
import pt.feup.tvvs.soulknight.view.elements.ElementViewer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrbViewer implements ElementViewer<Collectables> {
    private final Map<Character, Sprite> orbMap;

    public OrbViewer(SpriteLoader spriteLoader) throws IOException {
        orbMap = new HashMap<>();
        orbMap.put('h', spriteLoader.get("sprites/collectables/health.png"));
        orbMap.put('e', spriteLoader.get("sprites/collectables/energy.png"));
        orbMap.put('s', spriteLoader.get("sprites/collectables/speed.png"));
    }

    @Override
    public void draw(Collectables model, GUI gui, long time, int offsetX, int offsetY) throws IOException {
        Sprite sprite = orbMap.get(model.getChar());
        if (sprite == null) {
            throw new IllegalArgumentException("No sprite for character: " + model.getChar());
        }
        sprite.draw(gui, (int) model.getPosition().x(), (int) model.getPosition().y());
    }
}
