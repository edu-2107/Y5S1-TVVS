package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;
import pt.feup.tvvs.soulknight.model.game.elements.knight.DashState;
import pt.feup.tvvs.soulknight.model.game.elements.knight.FallingState;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.game.elements.knight.KnightState;
import pt.feup.tvvs.soulknight.model.game.elements.knight.RespawnState;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FallingStateWhiteBoxTests {

    private Knight knight;
    private Scene scene;
    private FallingState fallingState;

    @BeforeEach
    void setUp() {
        knight = mock(Knight.class);
        scene = mock(Scene.class);
        when(knight.getScene()).thenReturn(scene);
        when(knight.getPosition()).thenReturn(new Position(0, 0));
        when(knight.getMaxVelocity()).thenReturn(new Vector(10, 10));
        fallingState = new FallingState(knight);
    }

    @Test
    void testJump() {
        when(knight.getVelocity()).thenReturn(new Vector(0, 2));
        when(knight.getJumpCounter()).thenReturn(2);
        Vector result = fallingState.jump();
        assertEquals(new Vector(0, 2), result);

        when(knight.getVelocity()).thenReturn(new Vector(0, 0.5));
        when(knight.getJumpCounter()).thenReturn(1);
        when(knight.getJumpBoost()).thenReturn(1.5);
        result = fallingState.jump();
        assertEquals(new Vector(0, -1.0), result);
        verify(knight, times(1)).setJumpCounter(2);
        verify(scene, times(1)).setDoubleJumpParticles(any());
    }

    @Test
    void testDash() {
        when(knight.getVelocity()).thenReturn(new Vector(1, 0));
        when(knight.isFacingRight()).thenReturn(true);
        when(knight.getDashBoost()).thenReturn(2.0);
        Vector result = fallingState.dash();
        assertEquals(new Vector(3, 0), result);
        verify(scene, times(1)).setDashParticles(any());

        when(knight.isFacingRight()).thenReturn(false);
        result = fallingState.dash();
        assertEquals(new Vector(-1, 0), result);
    }

    @Test
    void testUpdateVelocity() {
        when(knight.getVelocity()).thenReturn(new Vector(1, 0.3));
        when(scene.getGravity()).thenReturn(1.0);
        when(knight.getAcceleration()).thenReturn(0.9);
        Vector result = fallingState.updateVelocity(new Vector(1, 0.3));
        assertEquals(new Vector(0.9, 0.8), result);

        when(knight.getVelocity()).thenReturn(new Vector(1, 1.0));
        result = fallingState.updateVelocity(new Vector(1, 1.0));
        assertEquals(new Vector(0.9, 2.75), result);
    }

}