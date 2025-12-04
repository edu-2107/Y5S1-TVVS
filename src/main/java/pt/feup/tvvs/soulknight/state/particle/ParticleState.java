package pt.feup.tvvs.soulknight.state.particle;

import pt.feup.tvvs.soulknight.controller.menu.ParticleMenuController;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.menu.Particle;

public interface ParticleState {
    Position move(Particle particle, long tick, ParticleMenuController controller);
}
