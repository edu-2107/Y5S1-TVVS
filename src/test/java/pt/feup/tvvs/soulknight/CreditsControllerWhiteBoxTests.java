package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.controller.credits.CreditsController;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.credits.Credits;
import pt.feup.tvvs.soulknight.state.MainMenuState;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreditsControllerWhiteBoxTests {

    @Test
    void move_whenQuit_setsMainMenuState() throws Exception {
        Credits credits = mock(Credits.class);
        CreditsController controller = new CreditsController(credits);

        Game game = mock(Game.class);

        SpriteLoader spriteLoader = mock(SpriteLoader.class);
        when(game.getSpriteLoader()).thenReturn(spriteLoader);

        controller.move(game, GUI.ACTION.QUIT, 0);

        verify(game, times(1)).setState(any(MainMenuState.class));
        verify(game, times(1)).getSpriteLoader();
        verifyNoMoreInteractions(game);
    }

    @Test
    void move_whenNotQuit_doesNothing() throws Exception {
        Credits credits = mock(Credits.class);
        CreditsController controller = new CreditsController(credits);

        Game game = mock(Game.class);

        controller.move(game, GUI.ACTION.UP, 0);

        verifyNoInteractions(game);
    }
}
