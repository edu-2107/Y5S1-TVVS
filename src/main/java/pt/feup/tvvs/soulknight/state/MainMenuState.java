package pt.feup.tvvs.soulknight.state;

import pt.feup.tvvs.soulknight.controller.Controller;
import pt.feup.tvvs.soulknight.controller.menu.MainMenuController;
import pt.feup.tvvs.soulknight.controller.menu.OptionController;
import pt.feup.tvvs.soulknight.controller.menu.ParticleMenuController;
import pt.feup.tvvs.soulknight.model.menu.MainMenu;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;
import pt.feup.tvvs.soulknight.view.sprites.ViewerProvider;
import pt.feup.tvvs.soulknight.view.states.MenuViewer;
import pt.feup.tvvs.soulknight.view.states.ScreenViewer;

import java.io.IOException;


public class MainMenuState extends State<MainMenu> {

    public MainMenuState(MainMenu model, SpriteLoader spriteLoader) throws IOException {
        super(model, spriteLoader);
    }

    @Override
    protected ScreenViewer<MainMenu> createScreenViewer(ViewerProvider viewerProvider) throws IOException {
        return new MenuViewer<>(getModel(), viewerProvider);
    }

    @Override
    protected Controller<MainMenu> createController() {
        return new MainMenuController(
                getModel(),
                new ParticleMenuController(getModel()),
                new OptionController(getModel())
        );
    }

}
