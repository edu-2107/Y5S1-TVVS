package pt.feup.tvvs.soulknight.view.elements.tree;

import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.game.elements.Tree;
import pt.feup.tvvs.soulknight.view.elements.ElementViewer;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TreeViewer implements ElementViewer<Tree> {
    private final Map<Character, Sprite> treeMap;

    public TreeViewer(SpriteLoader spriteLoader) throws IOException {
        treeMap = new HashMap<>();
        treeMap.put('t', spriteLoader.get("sprites/Ambient/SmallTree.png"));
        treeMap.put('T', spriteLoader.get("sprites/Ambient/MediumTree.png"));
    }

    @Override
    public void draw(Tree model, GUI gui, long time, int offsetX, int offsetY) throws IOException {
        Sprite sprite = treeMap.get(model.getChar());
        sprite.draw(gui, (int) model.getPosition().x(), (int) model.getPosition().y());
    }
}