package pt.feup.tvvs.soulknight.view.elements.spike;

import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.game.elements.Spike;
import pt.feup.tvvs.soulknight.view.elements.ElementViewer;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpikeViewer implements ElementViewer<Spike> {
    private final Map<Character, Sprite> spikeMap;

    public SpikeViewer(SpriteLoader spriteLoader) throws IOException {
        spikeMap = new HashMap<>();
        spikeMap.put('^', spriteLoader.get("sprites/spikes/spike_ground.png"));
        spikeMap.put('+', spriteLoader.get("sprites/spikes/spike_brown.png"));
        spikeMap.put('-', spriteLoader.get("sprites/spikes/spike_brown_2.png"));
    }
    @Override
    public void draw(Spike model, GUI gui, long time, int offsetX, int offsetY) throws IOException {
        Sprite sprite = spikeMap.get(model.getCharacter());
        sprite.draw(gui, (int) model.getPosition().x(), (int) model.getPosition().y());
    }
}