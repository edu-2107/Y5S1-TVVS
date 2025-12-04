package pt.feup.tvvs.soulknight.controller.menu;

import pt.feup.tvvs.soulknight.Game;
import pt.feup.tvvs.soulknight.model.menu.MainMenu;
import pt.feup.tvvs.soulknight.model.menu.SettingsMenu;
import pt.feup.tvvs.soulknight.state.MainMenuState;

import java.io.IOException;

public class SettingsMenuController extends MenuController<SettingsMenu> {
    public SettingsMenuController(SettingsMenu menu,ParticleMenuController particleMenuController,
                                  OptionController optionController)
    {
        super(menu, particleMenuController, optionController);
    }

    @Override
    protected void onQuit(Game game) throws IOException {
        game.setState(new MainMenuState(new MainMenu(), game.getSpriteLoader()));
    }
}
