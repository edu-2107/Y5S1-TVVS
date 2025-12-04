package pt.feup.tvvs.soulknight.state;

import pt.feup.tvvs.soulknight.controller.Controller;
import pt.feup.tvvs.soulknight.controller.menu.OptionController;
import pt.feup.tvvs.soulknight.controller.menu.ParticleMenuController;
import pt.feup.tvvs.soulknight.controller.menu.SettingsMenuController;
import pt.feup.tvvs.soulknight.model.menu.SettingsMenu;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;
import pt.feup.tvvs.soulknight.view.sprites.ViewerProvider;
import pt.feup.tvvs.soulknight.view.states.MenuViewer;
import pt.feup.tvvs.soulknight.view.states.ScreenViewer;

import java.io.IOException;

public class SettingsMenuState extends State<SettingsMenu> {
    public SettingsMenuState(SettingsMenu menu, SpriteLoader spriteLoader) throws IOException {
        super(menu, spriteLoader);
    }

    @Override
    protected Controller<SettingsMenu> createController() {
        return new SettingsMenuController(
                getModel(),
                new ParticleMenuController(getModel()),
                new OptionController(getModel()));
    }

    @Override
    protected ScreenViewer<SettingsMenu> createScreenViewer(ViewerProvider viewerProvider) throws IOException {
        return new MenuViewer<>(getModel(), viewerProvider);
    }

}

