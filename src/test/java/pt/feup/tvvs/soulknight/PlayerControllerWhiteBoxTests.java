package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import pt.feup.tvvs.soulknight.controller.game.PlayerController;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.knight.IdleState;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.game.elements.knight.KnightState;
import pt.feup.tvvs.soulknight.model.game.elements.knight.RespawnState;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerControllerWhiteBoxTests {

    private Scene scene;
    private Game game;
    private Knight knight;
    private PlayerController controller;

    @BeforeEach
    public void setUp() {
        scene = mock(Scene.class);
        game = mock(Game.class);
        knight = mock(Knight.class);

        when(scene.getPlayer()).thenReturn(knight);
        controller = new PlayerController(scene);

        when(knight.updatePosition()).thenReturn(new Position(10, 20));
    }

    @Test
    public void move_left_branch_setsVelocity_andFacingLeft_andNoIdleWhenStateNonNull() throws IOException {
        Vector leftVel = new Vector(-1, 0);
        KnightState next = mock(KnightState.class);

        when(knight.moveLeft()).thenReturn(leftVel);
        when(knight.getNextState()).thenReturn(next);
        when(knight.getState()).thenReturn(next);

        controller.move(game, GUI.ACTION.LEFT, 0);

        verify(knight).setVelocity(leftVel);
        verify(knight).setFacingRight(false);
        verify(knight, never()).updateVelocity();

        verify(knight).setPosition(new Position(10, 20));
        verify(knight).setScene(scene);
        verify(knight).setState(next);

        verify(knight, never()).setState(isA(IdleState.class));
    }

    @Test
    public void move_right_branch_setsVelocity_andFacingRight() throws IOException {
        Vector rightVel = new Vector(1, 0);
        KnightState next = mock(KnightState.class);

        when(knight.moveRight()).thenReturn(rightVel);
        when(knight.getNextState()).thenReturn(next);
        when(knight.getState()).thenReturn(next);

        controller.move(game, GUI.ACTION.RIGHT, 0);

        verify(knight).setVelocity(rightVel);
        verify(knight).setFacingRight(true);
    }

    @Test
    public void move_jump_branch_setsVelocityFromJump() throws IOException {
        Vector jumpVel = new Vector(0, -2);
        KnightState next = mock(KnightState.class);

        when(knight.jump()).thenReturn(jumpVel);
        when(knight.getNextState()).thenReturn(next);
        when(knight.getState()).thenReturn(next);

        controller.move(game, GUI.ACTION.JUMP, 0);

        verify(knight).setVelocity(jumpVel);
    }

    @Test
    public void move_dash_branch_setsVelocityFromDash() throws IOException {
        Vector dashVel = new Vector(3, 0);
        KnightState next = mock(KnightState.class);

        when(knight.dash()).thenReturn(dashVel);
        when(knight.getNextState()).thenReturn(next);
        when(knight.getState()).thenReturn(next);

        controller.move(game, GUI.ACTION.DASH, 0);

        verify(knight).setVelocity(dashVel);
    }

    @Test
    public void move_kill_branch_setsRespawnState_thenSetsNextState() throws IOException {
        KnightState next = mock(KnightState.class);

        when(knight.getNextState()).thenReturn(next);
        when(knight.getState()).thenReturn(next);

        controller.move(game, GUI.ACTION.KILL, 0);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        InOrder inOrder = inOrder(knight);
        inOrder.verify(knight).setState((KnightState) captor.capture()); // RespawnState
        assertInstanceOf(RespawnState.class, captor.getValue());

        inOrder.verify(knight).setPosition(new Position(10, 20));
        inOrder.verify(knight).setScene(scene);
        inOrder.verify(knight).setState(next);
    }

    @Test
    public void move_default_branch_callsUpdateVelocity_andIfStateNull_createsIdleState() throws IOException {
        Vector updated = new Vector(0.5, 1.0);

        when(knight.updateVelocity()).thenReturn(updated);
        when(knight.getNextState()).thenReturn(null);
        when(knight.getState()).thenReturn(null);

        controller.move(game, GUI.ACTION.NULL, 0);

        verify(knight).setVelocity(updated);

        ArgumentCaptor<Object> stateCaptor = ArgumentCaptor.forClass(Object.class);
        verify(knight, times(2)).setState((KnightState) stateCaptor.capture());

        assertNull(stateCaptor.getAllValues().get(0));
        assertInstanceOf(IdleState.class, stateCaptor.getAllValues().get(1));
    }
}
