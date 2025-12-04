package pt.feup.tvvs.soulknight.controller.credits;

import pt.feup.tvvs.soulknight.Game;
import pt.feup.tvvs.soulknight.controller.Controller;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.credits.Credits;
import pt.feup.tvvs.soulknight.model.menu.MainMenu;
import pt.feup.tvvs.soulknight.state.MainMenuState;

import java.io.IOException;

import static pt.feup.tvvs.soulknight.gui.GUI.ACTION.QUIT;

public class CreditsController extends Controller<Credits> {
    public CreditsController(Credits model) {
        super(model);
    }

    @Override
    public void move(Game game, GUI.ACTION action, long frameCount) throws IOException {
        if (action == QUIT) {
            game.setState(new MainMenuState(new MainMenu(), game.getSpriteLoader()));
        }
    }
}
