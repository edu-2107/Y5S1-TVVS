package pt.feup.tvvs.soulknight.controller.game;

import pt.feup.tvvs.soulknight.Game;
import pt.feup.tvvs.soulknight.controller.Controller;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.game.elements.enemies.Enemies;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnemieController extends Controller<Scene> {
    private long lastMovement;
    public EnemieController(Scene scene) {
        super(scene);
        this.lastMovement = 0;
    }

    @Override
    public void move(Game game, GUI.ACTION action, long time) throws IOException {
        if (time - lastMovement > 2) {
            // Consolidate all enemy lists into a single list
            List<Enemies> allEnemies = new ArrayList<>(getModel().getMonsters());
            // Move each enemy in the list
            for (Enemies enemy : allEnemies) {
                enemy.setPosition(enemy.moveMonster());
            }

            this.lastMovement = time;
        }
    }
}













