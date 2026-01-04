package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.knight.*;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class FallingStateWhiteBoxTests {

    private Knight knight;
    private Scene scene;

    static class TestableFallingState extends FallingState {
        private long particlesTimer = 1;
        private final KnightState groundReturn;
        private final KnightState onAirReturn;

        TestableFallingState(Knight knight, KnightState groundReturn, KnightState onAirReturn) {
            super(knight);
            this.groundReturn = groundReturn;
            this.onAirReturn = onAirReturn;
        }

        void setParticlesTimerValue(long v) { this.particlesTimer = v; }

        @Override
        public void tickParticles() {
        }

        @Override
        protected Vector applyCollisions(Vector v) {
            return v;
        }

        @Override
        protected Vector limitVelocity(Vector v) {
            return v;
        }

        @Override
        protected KnightState getNextGroundState() {
            return groundReturn;
        }

        @Override
        protected KnightState getNextOnAirState() {
            return onAirReturn;
        }

        @Override
        public long getParticlesTimer() {
            return particlesTimer;
        }

        @Override
        public void resetParticlesTimer() {
            particlesTimer = 100;
        }
    }

    @BeforeEach
    void setUp() {
        knight = mock(Knight.class);
        scene = mock(Scene.class);

        when(knight.getScene()).thenReturn(scene);
        when(knight.getPosition()).thenReturn(new Position(0, 0));
        when(knight.getMaxVelocity()).thenReturn(new Vector(10, 10));

        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.isOnGround()).thenReturn(false);

        when(knight.getVelocity()).thenReturn(new Vector(0, 0));
        when(knight.getAcceleration()).thenReturn(1.0);
        when(scene.getGravity()).thenReturn(0.0);

        when(knight.getJumpCounter()).thenReturn(0);
        when(knight.getJumpBoost()).thenReturn(1.5);
        when(knight.getDashBoost()).thenReturn(2.0);
        when(knight.isFacingRight()).thenReturn(true);

        // factories usadas nos setters (evita NPE)
        when(knight.createParticlesDoubleJump(anyInt(), any())).thenReturn(java.util.Collections.emptyList());
        when(knight.createDashParticles(anyInt())).thenReturn(java.util.Collections.emptyList());
        when(knight.createRespawnParticles(anyInt())).thenReturn(java.util.Collections.emptyList());
    }

    // ---------------- jump() ----------------

    @Test
    public void jump_whenOutsideRange_returnsUpdateVelocityOfCurrentVelocity() {
        FallingState fallingState = new FallingState(knight);

        when(knight.getVelocity()).thenReturn(new Vector(0, 2)); // y fora do range [0..1]
        when(knight.getJumpCounter()).thenReturn(1);

        Vector result = fallingState.jump();

        // gravity default 0, accel default 1 => updateVelocity devolve (0,2)
        assertEquals(new Vector(0, 2), result);
        verify(scene, never()).setDoubleJumpParticles(anyList());
        verify(knight, never()).setJumpCounter(anyInt());
    }

    @Test
    public void jump_whenInRangeAndCanDoubleJump_setsCounterAndParticles() {
        FallingState fallingState = new FallingState(knight);

        when(knight.getVelocity()).thenReturn(new Vector(0, 0.5));
        when(knight.getJumpCounter()).thenReturn(1);
        when(knight.getJumpBoost()).thenReturn(1.5);

        Vector result = fallingState.jump();

        // newVelocity.y = 0.5 - 1.5 = -1.0 ; gravity 0 => mantém
        assertEquals(new Vector(0, -1.0), result);
        verify(knight).setJumpCounter(2);
        verify(scene).setDoubleJumpParticles(anyList());
    }

    @Test
    public void jump_whenInRangeButJumpCounterIs2_doesNotDoubleJumpBranch() {
        FallingState fallingState = new FallingState(knight);

        // y dentro do range, mas jumpCounter já é 2 => NÃO entra no inner if
        when(knight.getVelocity()).thenReturn(new Vector(0, 0.5));
        when(knight.getJumpCounter()).thenReturn(2);

        Vector result = fallingState.jump();

        assertEquals(new Vector(0, 0.5), result);
        verify(scene, never()).setDoubleJumpParticles(anyList());
        verify(knight, never()).setJumpCounter(anyInt());
    }

    // ---------------- dash() ----------------

    @Test
    public void dash_facingRight_true_and_false() {
        FallingState fallingState = new FallingState(knight);

        when(knight.getVelocity()).thenReturn(new Vector(1, 0));
        when(knight.getDashBoost()).thenReturn(2.0);

        when(knight.isFacingRight()).thenReturn(true);
        Vector r1 = fallingState.dash();
        assertEquals(new Vector(3, 0), r1);

        when(knight.isFacingRight()).thenReturn(false);
        Vector r2 = fallingState.dash();
        assertEquals(new Vector(-1, 0), r2);

        verify(scene, atLeastOnce()).setDashParticles(anyList());
    }

    // ---------------- updateVelocity() ----------------

    @Test
    public void updateVelocity_coversBothBranches() {
        FallingState fallingState = new FallingState(knight);

        when(scene.getGravity()).thenReturn(1.0);
        when(knight.getAcceleration()).thenReturn(0.9);

        // branch: current vy in [0..0.5]
        when(knight.getVelocity()).thenReturn(new Vector(1, 0.3));
        Vector r1 = fallingState.updateVelocity(new Vector(1, 0.3));
        assertEquals(new Vector(0.9, 0.8), r1); // 0.3 + 1*0.5

        // branch else: current vy > 0.5
        when(knight.getVelocity()).thenReturn(new Vector(1, 1.0));
        Vector r2 = fallingState.updateVelocity(new Vector(1, 1.0));
        assertEquals(new Vector(0.9, 2.75), r2); // 1.0 + 1*1.75
    }

    // ---------------- getNextState() branches ----------------

    @Test
    public void getNextState_whenCollideSpike_returnsRespawnState() {
        when(scene.collideSpike()).thenReturn(true);

        TestableFallingState state = new TestableFallingState(
                knight, mock(KnightState.class), mock(KnightState.class)
        );

        KnightState next = state.getNextState();
        assertInstanceOf(RespawnState.class, next);
    }

    @Test
    public void getNextState_whenParticlesTimerZero_setsRespawnParticles_andResetsTimer_andContinues() {
        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.isOnGround()).thenReturn(false);
        when(knight.getJumpCounter()).thenReturn(0); // para cair no "return this"

        TestableFallingState state = new TestableFallingState(
                knight, mock(KnightState.class), mock(KnightState.class)
        );
        state.setParticlesTimerValue(0);

        KnightState next = state.getNextState();

        verify(scene).setRespawnParticles(anyList());
        assertEquals(100, state.getParticlesTimer());
        assertSame(state, next);
    }

    @Test
    public void getNextState_whenOverMaxXVelocity_returnsDashState() {
        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOverMaxXVelocity()).thenReturn(true);

        TestableFallingState state = new TestableFallingState(
                knight, mock(KnightState.class), mock(KnightState.class)
        );

        KnightState next = state.getNextState();
        assertInstanceOf(DashState.class, next);
    }

    @Test
    public void getNextState_whenOnGround_returnsNextGroundState() {
        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.isOnGround()).thenReturn(true);

        KnightState ground = mock(KnightState.class);
        TestableFallingState state = new TestableFallingState(
                knight, ground, mock(KnightState.class)
        );

        KnightState next = state.getNextState();
        assertSame(ground, next);
    }

    @Test
    public void getNextState_whenJumpCounterIs2_returnsNextOnAirState() {
        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.isOnGround()).thenReturn(false);
        when(knight.getJumpCounter()).thenReturn(2);

        KnightState onAir = mock(KnightState.class);
        TestableFallingState state = new TestableFallingState(
                knight, mock(KnightState.class), onAir
        );

        KnightState next = state.getNextState();
        assertSame(onAir, next);
    }

    @Test
    public void getNextState_default_returnsThis() {
        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.isOnGround()).thenReturn(false);
        when(knight.getJumpCounter()).thenReturn(1); // != 2

        TestableFallingState state = new TestableFallingState(
                knight, mock(KnightState.class), mock(KnightState.class)
        );

        KnightState next = state.getNextState();
        assertSame(state, next);
    }
}
