package pt.feup.tvvs.soulknight.controller.game;

import pt.feup.tvvs.soulknight.Game;
import pt.feup.tvvs.soulknight.controller.Controller;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.game.elements.particle.Particle;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import java.io.IOException;

public class ParticleController extends Controller<Scene>{

    public ParticleController(Scene scene) {
        super(scene);
    }

    @Override
    public void move(Game game, GUI.ACTION action, long time) throws IOException {
        for(Particle particle: getModel().getParticles()){
            particle.setScene(getModel());
            particle.setPosition(particle.moveParticle(getModel(), time));

        }
        for(Particle particle: getModel().getDoubleJumpParticles()){
            particle.setScene(getModel());
            particle.setPosition(particle.moveParticle(getModel(), time));

        }
        for(Particle particle: getModel().getJumpParticles()){
            particle.setScene(getModel());
            particle.setPosition(particle.moveParticle(getModel(), time));
        }
        for(Particle particle: getModel().getRespawnParticles()){
            particle.setScene(getModel());
            particle.setPosition(particle.moveParticle(getModel(), time));
        }
        for(Particle particle: getModel().getDashParticles()){
            particle.setScene(getModel());
            particle.setPosition(particle.moveParticle(getModel(), time));
        }

    }
}
