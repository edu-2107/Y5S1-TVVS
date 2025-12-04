package pt.feup.tvvs.soulknight.view.elements.tile;

import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.game.elements.tile.Tile;
import pt.feup.tvvs.soulknight.view.elements.ElementViewer;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TileViewer implements ElementViewer<Tile> {
    private final Map<Character, Sprite> tileMap;

    public TileViewer(SpriteLoader spriteLoader) throws IOException {
        this.tileMap = new HashMap<>();
        tileMap.put('M', spriteLoader.get("sprites/Tiles/ground_metal.png"));
        tileMap.put('G', spriteLoader.get("sprites/Tiles/ground_grass.png"));
        tileMap.put('L', spriteLoader.get("sprites/Tiles/ground_rocky_lava.png"));

    }

    @Override
    public void draw(Tile model, GUI gui, long time, int offsetX, int offsetY) {
        Sprite sprite = tileMap.get(model.getCharacter());
        sprite.draw(gui, (int) model.getPosition().x(), (int) model.getPosition().y());

    }
}
