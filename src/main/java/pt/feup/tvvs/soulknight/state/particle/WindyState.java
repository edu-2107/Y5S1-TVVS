package pt.feup.tvvs.soulknight.state.particle;

import pt.feup.tvvs.soulknight.controller.menu.ParticleMenuController;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.menu.Particle;

public class WindyState implements ParticleState {
    @Override
    public Position move(Particle particle, long tick, ParticleMenuController controller) {
        double windAngle = controller.getWindAngle(); // Obtain wind properties from the controller
        double windSpeed = controller.getWindSpeed();

        int newX = (int) (particle.getPosition().x() + (int) (windSpeed * Math.cos(windAngle)));
        int newY = (int) (particle.getPosition().y() + (int) (windSpeed * Math.sin(windAngle)));

        return controller.wrapPosition(newX, newY);
    }
}
