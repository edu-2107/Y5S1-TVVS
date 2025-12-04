package pt.feup.tvvs.soulknight.controller.game;

import pt.feup.tvvs.soulknight.Game;
import pt.feup.tvvs.soulknight.controller.Controller;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.credits.Credits;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;
import pt.feup.tvvs.soulknight.model.game.scene.SceneLoader;
import pt.feup.tvvs.soulknight.model.menu.MainMenu;
import pt.feup.tvvs.soulknight.state.CreditsState;
import pt.feup.tvvs.soulknight.state.GameState;
import pt.feup.tvvs.soulknight.state.MainMenuState;

import java.io.IOException;

public class SceneController extends Controller<Scene> {
    private final PlayerController playerController;
    private final ParticleController particleController;
    private final EnemieController enemieController;

    public SceneController(Scene scene, PlayerController playerController,
                           ParticleController particleController, EnemieController enemieController) {
        super(scene);
        this.playerController = playerController;
        this.particleController = particleController;
        this.enemieController = enemieController;
    }

    @Override
    public void move(Game game, GUI.ACTION action, long time) throws IOException {
        Knight knight = getModel().getPlayer();
        if (action == GUI.ACTION.QUIT)
            game.setState(new MainMenuState(new MainMenu(), game.getSpriteLoader()));
        else {

            playerController.move(game, action, time);

            if (getModel().isAtEndPosition() && knight.getOrbs() == 3 * (getModel().getSceneID() + 1)) {
                if (getModel().getSceneID() + 1 >= game.getNumberOfLevels()) {
                    Credits credits = new Credits(getModel().getPlayer());
                    game.setState(new CreditsState(credits, game.getSpriteLoader()));
                } else {
                    SceneLoader sceneLoader = new SceneLoader((getModel().getSceneID() + 1));
                    Scene newScene = sceneLoader.createScene(knight);
                    game.setState(new GameState(newScene, game.getSpriteLoader()));
                }
            }
            else{
                getModel().collectOrbs(getModel().getOrbs());

                getModel().collideMonsters(getModel().getMonsters());

                particleController.move(game, action,time);
                enemieController.move(game,action,time);
            }
        }
    }


}
