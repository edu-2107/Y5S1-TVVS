package pt.feup.tvvs.soulknight.state.particle;

import pt.feup.tvvs.soulknight.controller.menu.ParticleMenuController;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.menu.Particle;

public class ZicoState implements ParticleState {
    @Override
    public Position move(Particle particle, long tick, ParticleMenuController controller) {

        return controller.wrapPosition((int) particle.getPosition().x(), (int) (particle.getPosition().y() + 10));
    }
}
