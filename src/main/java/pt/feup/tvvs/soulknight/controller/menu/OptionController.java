package pt.feup.tvvs.soulknight.controller.menu;

import pt.feup.tvvs.soulknight.Game;
import pt.feup.tvvs.soulknight.controller.Controller;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.gui.RescalableGUI;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.game.scene.SceneLoader;
import pt.feup.tvvs.soulknight.model.menu.MainMenu;
import pt.feup.tvvs.soulknight.model.menu.Menu;
import pt.feup.tvvs.soulknight.model.menu.SettingsMenu;
import pt.feup.tvvs.soulknight.state.GameState;
import pt.feup.tvvs.soulknight.state.MainMenuState;
import pt.feup.tvvs.soulknight.state.SettingsMenuState;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class OptionController extends Controller<Menu> {
    private static final RescalableGUI.ResolutionScale[] resolutions = RescalableGUI.ResolutionScale.values();

    public OptionController(Menu menu) {
        super(menu);
    }

    @Override
    public void move(Game game, GUI.ACTION action, long frameCount) throws IOException, URISyntaxException, FontFormatException {
        switch (getModel().getCurrentOption().getType()) {
            case START_GAME:
                if (action == GUI.ACTION.SELECT) {
                    game.setState(new GameState(
                            new SceneLoader(0)
                                    .createScene(new Knight(0, 0, 50, 10, 1)),
                            game.getSpriteLoader()
                    ));
                }
                break;
            case SETTINGS:
                if (action == GUI.ACTION.SELECT)
                    game.setState(new SettingsMenuState(new SettingsMenu(), game.getSpriteLoader()));
                break;
            case EXIT:
                if (action == GUI.ACTION.SELECT)
                    game.setState(null);
                break;
            case RESOLUTION:
                if (action == GUI.ACTION.RIGHT) {
                    int index = getResolutionIndex(game.getResolution());
                    if (index < resolutions.length - 1) {
                        RescalableGUI.ResolutionScale newResolution = resolutions[index + 1];
                        game.setResolution(newResolution);
                    }
                } else if (action == GUI.ACTION.LEFT) {
                    int index = getResolutionIndex(game.getResolution());
                    if (index > -1) {
                        RescalableGUI.ResolutionScale newResolution = index != 0 ? resolutions[index - 1] : null;
                        game.setResolution(newResolution);
                    }
                }
                break;
            case TO_MAIN_MENU:
                if (action == GUI.ACTION.SELECT)
                    game.setState(new MainMenuState(new MainMenu(), game.getSpriteLoader()));
        }
    }

    private Integer getResolutionIndex(RescalableGUI.ResolutionScale resolution) {
        for (int i = 0; i < resolutions.length; i++) {
            if (resolutions[i] == resolution)
                return i;
        }
        return -1;
    }
}
