package pt.feup.tvvs.soulknight.state;

import pt.feup.tvvs.soulknight.controller.Controller;
import pt.feup.tvvs.soulknight.controller.game.EnemieController;
import pt.feup.tvvs.soulknight.controller.game.ParticleController;
import pt.feup.tvvs.soulknight.controller.game.PlayerController;
import pt.feup.tvvs.soulknight.controller.game.SceneController;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;
import pt.feup.tvvs.soulknight.view.sprites.ViewerProvider;
import pt.feup.tvvs.soulknight.view.states.GameViewer;
import pt.feup.tvvs.soulknight.view.states.ScreenViewer;

import java.io.IOException;

public class GameState extends State<Scene> {
    public GameState(Scene model, SpriteLoader spriteLoader) throws IOException {
        super(model, spriteLoader);
    }

    @Override
    protected Controller<Scene> createController() {
        return new SceneController(getModel(), new PlayerController(getModel()),
                new ParticleController(getModel()), new EnemieController(getModel()));
    }

    @Override
    protected ScreenViewer<Scene> createScreenViewer(ViewerProvider viewerProvider) throws IOException {
        return new GameViewer(getModel(), viewerProvider);
    }
}
