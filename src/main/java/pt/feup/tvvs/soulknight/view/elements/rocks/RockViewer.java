package pt.feup.tvvs.soulknight.view.elements.rocks;

import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.game.elements.rocks.Rock;
import pt.feup.tvvs.soulknight.view.elements.ElementViewer;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RockViewer implements ElementViewer<Rock> {

    private final Map<Character, Sprite> rockMap;

    public RockViewer(SpriteLoader spriteLoader) throws IOException {
        rockMap = new HashMap<>();
        rockMap.put('r', spriteLoader.get("sprites/Ambient/Smallrock.png"));
        rockMap.put('R', spriteLoader.get("sprites/Ambient/Bigrock.png"));
    }
    @Override
    public void draw(Rock model, GUI gui, long time, int offsetX, int offsetY) throws IOException {
        Sprite sprite = rockMap.get(model.getChar());
        if (sprite == null) {
            throw new IllegalArgumentException("No sprite for character: " + model.getChar());
        }
        sprite.draw(gui, (int) model.getPosition().x(), (int) model.getPosition().y());
    }
}
