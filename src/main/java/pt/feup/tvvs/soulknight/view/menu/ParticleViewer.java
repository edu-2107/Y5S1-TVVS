package pt.feup.tvvs.soulknight.view.menu;

import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.menu.Particle;
import pt.feup.tvvs.soulknight.view.elements.ElementViewer;

import java.io.IOException;

public class ParticleViewer implements ElementViewer<Particle> {
    @Override
    public void draw(Particle model, GUI gui, long time, int offsetX, int offsetY) throws IOException {
        gui.drawPixel((int)model.getPosition().x(), (int)model.getPosition().y(), model.getColor());
    }
}
