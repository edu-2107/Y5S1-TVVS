package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import pt.feup.tvvs.soulknight.controller.game.EnemieController;
import pt.feup.tvvs.soulknight.controller.game.ParticleController;
import pt.feup.tvvs.soulknight.controller.game.PlayerController;
import pt.feup.tvvs.soulknight.controller.game.SceneController;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;
import pt.feup.tvvs.soulknight.model.game.scene.SceneLoader;
import pt.feup.tvvs.soulknight.state.CreditsState;
import pt.feup.tvvs.soulknight.state.GameState;
import pt.feup.tvvs.soulknight.state.MainMenuState;
import pt.feup.tvvs.soulknight.state.State;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SceneControllerWhiteBoxTests {

    @Test
    void move_whenQuit_setsMainMenuState_andDoesNotCallControllers() throws Exception {
        Scene scene = mock(Scene.class);
        Knight knight = mock(Knight.class);
        when(scene.getPlayer()).thenReturn(knight);

        PlayerController playerController = mock(PlayerController.class);
        ParticleController particleController = mock(ParticleController.class);
        EnemieController enemieController = mock(EnemieController.class);

        Game game = mock(Game.class);
        SpriteLoader sprites = mock(SpriteLoader.class);
        when(game.getSpriteLoader()).thenReturn(sprites);

        SceneController controller = new SceneController(scene, playerController, particleController, enemieController);

        controller.move(game, GUI.ACTION.QUIT, 123L);

        ArgumentCaptor<State<?>> cap = ArgumentCaptor.forClass(State.class);
        verify(game).setState(cap.capture());
        assertInstanceOf(MainMenuState.class, cap.getValue());

        verifyNoInteractions(playerController);
        verifyNoInteractions(particleController);
        verifyNoInteractions(enemieController);
        verify(scene, never()).collectOrbs(any());
        verify(scene, never()).collideMonsters(any());
    }

    @Test
    void move_whenNotQuit_andNotEndCondition_executesGameplayElseBranch() throws Exception {
        Scene scene = mock(Scene.class);
        Knight knight = mock(Knight.class);
        when(scene.getPlayer()).thenReturn(knight);

        when(scene.isAtEndPosition()).thenReturn(false);

        when(scene.getOrbs()).thenReturn(null);
        when(scene.getMonsters()).thenReturn(null);

        PlayerController playerController = mock(PlayerController.class);
        ParticleController particleController = mock(ParticleController.class);
        EnemieController enemieController = mock(EnemieController.class);

        Game game = mock(Game.class);

        SceneController controller = new SceneController(scene, playerController, particleController, enemieController);

        controller.move(game, GUI.ACTION.LEFT, 50L);

        verify(playerController, times(1)).move(game, GUI.ACTION.LEFT, 50L);

        verify(scene, times(1)).collectOrbs(scene.getOrbs());
        verify(scene, times(1)).collideMonsters(scene.getMonsters());

        verify(particleController, times(1)).move(game, GUI.ACTION.LEFT, 50L);
        verify(enemieController, times(1)).move(game, GUI.ACTION.LEFT, 50L);

        verify(game, never()).setState(any());
    }

    @Test
    void move_whenEndAndOrbsMatch_andLastLevel_setsCreditsState() throws Exception {
        Scene scene = mock(Scene.class);
        Knight knight = mock(Knight.class);
        when(scene.getPlayer()).thenReturn(knight);

        when(scene.isAtEndPosition()).thenReturn(true);

        when(scene.getSceneID()).thenReturn(1); // sceneID+1 = 2 => 3*2 = 6
        when(knight.getOrbs()).thenReturn(6);

        Game game = mock(Game.class);
        SpriteLoader sprites = mock(SpriteLoader.class);
        when(game.getSpriteLoader()).thenReturn(sprites);

        when(game.getNumberOfLevels()).thenReturn(2); // 2 >= 2 => true

        PlayerController playerController = mock(PlayerController.class);
        ParticleController particleController = mock(ParticleController.class);
        EnemieController enemieController = mock(EnemieController.class);

        SceneController controller = new SceneController(scene, playerController, particleController, enemieController);

        controller.move(game, GUI.ACTION.RIGHT, 77L);

        verify(playerController, times(1)).move(game, GUI.ACTION.RIGHT, 77L);

        ArgumentCaptor<State<?>> cap = ArgumentCaptor.forClass(State.class);
        verify(game).setState(cap.capture());
        assertInstanceOf(CreditsState.class, cap.getValue());

        verify(scene, never()).collectOrbs(any());
        verify(scene, never()).collideMonsters(any());
        verify(particleController, never()).move(any(), any(), anyLong());
        verify(enemieController, never()).move(any(), any(), anyLong());
    }

    @Test
    void move_whenEndAndOrbsMatch_andNotLastLevel_setsGameState_usingMockedSceneLoader() throws Exception {
        Scene scene = mock(Scene.class);
        Knight knight = mock(Knight.class);
        when(scene.getPlayer()).thenReturn(knight);

        when(scene.isAtEndPosition()).thenReturn(true);

        when(scene.getSceneID()).thenReturn(1);  // sceneID+1 = 2
        when(knight.getOrbs()).thenReturn(6);    // 3*(2)=6

        Game game = mock(Game.class);
        SpriteLoader sprites = mock(SpriteLoader.class);
        when(game.getSpriteLoader()).thenReturn(sprites);

        // not last level: (sceneID+1) < numberOfLevels
        when(game.getNumberOfLevels()).thenReturn(5); // 2 < 5 => false no if, entra no else (GameState)

        PlayerController playerController = mock(PlayerController.class);
        ParticleController particleController = mock(ParticleController.class);
        EnemieController enemieController = mock(EnemieController.class);

        SceneController controller = new SceneController(scene, playerController, particleController, enemieController);

        Scene fakeNextScene = mock(Scene.class);

        try (MockedConstruction<SceneLoader> mocked = mockConstruction(
                SceneLoader.class,
                (mock, ctx) -> when(mock.createScene(any(Knight.class))).thenReturn(fakeNextScene)
        )) {
            controller.move(game, GUI.ACTION.JUMP, 10L);
        }

        verify(playerController, times(1)).move(game, GUI.ACTION.JUMP, 10L);

        ArgumentCaptor<State<?>> cap = ArgumentCaptor.forClass(State.class);
        verify(game).setState(cap.capture());
        assertInstanceOf(GameState.class, cap.getValue());

        verify(scene, never()).collectOrbs(any());
        verify(scene, never()).collideMonsters(any());
        verify(particleController, never()).move(any(), any(), anyLong());
        verify(enemieController, never()).move(any(), any(), anyLong());
    }
}
