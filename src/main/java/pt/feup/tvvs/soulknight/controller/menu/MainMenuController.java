package pt.feup.tvvs.soulknight.controller.menu;

import pt.feup.tvvs.soulknight.Game;
import pt.feup.tvvs.soulknight.model.menu.MainMenu;

public class MainMenuController extends MenuController<MainMenu> {
    public MainMenuController(MainMenu menu, ParticleMenuController particleMenuController,
                              OptionController optionController)
    {
        super(menu, particleMenuController, optionController);
    }

    @Override
    protected void onQuit(Game game) {
        game.setState(null);
    }
}

