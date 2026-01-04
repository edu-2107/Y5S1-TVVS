package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.knight.*;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MaxVelocityStateWhiteBoxTests {

    private Knight knight;
    private Scene scene;

    static class TestableMaxVelocityState extends MaxVelocityState {
        private int particlesTimer = 1;
        private final KnightState onAirReturn;

        TestableMaxVelocityState(Knight knight, KnightState onAirReturn) {
            super(knight);
            this.onAirReturn = onAirReturn;
        }

        void setParticlesTimerValue(int v) { this.particlesTimer = v; }

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
        protected KnightState getNextOnAirState() {
            return onAirReturn;
        }

        @Override
        public long getParticlesTimer() {
            return particlesTimer;
        }

        @Override
        public void resetParticlesTimer() {
            particlesTimer = 1;
        }
    }

    @BeforeEach
    void setUp() {
        knight = mock(Knight.class);
        scene = mock(Scene.class);
        when(knight.getScene()).thenReturn(scene);

        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);

        when(knight.getVelocity()).thenReturn(new Vector(RunningState.MAX_VELOCITY, 0.0));
        when(knight.getAcceleration()).thenReturn(0.9);

        when(knight.getJumpBoost()).thenReturn(2.0);
        when(knight.getDashBoost()).thenReturn(3.0);
        when(knight.isFacingRight()).thenReturn(true);
        when(knight.getJumpCounter()).thenReturn(0);

        when(knight.createParticlesJump(anyInt())).thenReturn(Collections.emptyList());
        when(knight.createDashParticles(anyInt())).thenReturn(Collections.emptyList());
        when(knight.createRespawnParticles(anyInt())).thenReturn(Collections.emptyList());
    }

    // -------- jump / dash / updateVelocity --------

    @Test
    public void jump_incrementsCounter_setsJumpParticles_andReturnsUpdatedVelocity() {
        TestableMaxVelocityState state = new TestableMaxVelocityState(knight, mock(KnightState.class));

        when(knight.getJumpCounter()).thenReturn(1);
        when(knight.getVelocity()).thenReturn(new Vector(2.0, 1.0));
        when(knight.getJumpBoost()).thenReturn(2.0);
        when(knight.getAcceleration()).thenReturn(0.5);

        Vector out = state.jump();

        verify(knight).setJumpCounter(2);
        verify(knight).createParticlesJump(10);
        verify(scene).setJumpParticles(anyList());

        // newVelocity=(2, -1), updateVelocity => x=2*0.5=1, y=-1
        assertEquals(1.0, out.x(), 1e-9);
        assertEquals(-1.0, out.y(), 1e-9);
    }

    @Test
    public void dash_facingRight_true_and_false() {
        TestableMaxVelocityState state = new TestableMaxVelocityState(knight, mock(KnightState.class));

        when(knight.getVelocity()).thenReturn(new Vector(1.0, 5.0));
        when(knight.getDashBoost()).thenReturn(3.0);

        when(knight.isFacingRight()).thenReturn(true);
        Vector out1 = state.dash();
        assertEquals(4.0, out1.x(), 1e-9); // 1 + 3
        assertEquals(5.0, out1.y(), 1e-9);

        when(knight.isFacingRight()).thenReturn(false);
        Vector out2 = state.dash();
        assertEquals(-2.0, out2.x(), 1e-9); // 1 - 3
        assertEquals(5.0, out2.y(), 1e-9);

        verify(knight, atLeastOnce()).createDashParticles(10);
        verify(scene, atLeastOnce()).setDashParticles(anyList());
    }

    @Test
    public void updateVelocity_appliesAcceleration_onlyOnX() {
        TestableMaxVelocityState state = new TestableMaxVelocityState(knight, mock(KnightState.class));
        when(knight.getAcceleration()).thenReturn(0.5);

        Vector out = state.updateVelocity(new Vector(2.0, 7.0));
        assertEquals(1.0, out.x(), 1e-9);
        assertEquals(7.0, out.y(), 1e-9);
    }

    // -------- getNextState branches --------

    @Test
    public void getNextState_whenCollideSpike_returnsRespawnState() {
        when(scene.collideSpike()).thenReturn(true);

        TestableMaxVelocityState state = new TestableMaxVelocityState(knight, mock(KnightState.class));
        KnightState next = state.getNextState();

        assertInstanceOf(RespawnState.class, next);
    }

    @Test
    public void getNextState_whenParticlesTimerZero_setsRespawnParticles_andResetsTimer_thenReturnsThis() {
        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(RunningState.MAX_VELOCITY, 0.0));

        TestableMaxVelocityState state = new TestableMaxVelocityState(knight, mock(KnightState.class));
        state.setParticlesTimerValue(0);

        KnightState next = state.getNextState();

        verify(scene).setRespawnParticles(anyList());
        assertEquals(1, state.getParticlesTimer());
        verify(knight).setJumpCounter(0);
        assertSame(state, next);
    }

    @Test
    public void getNextState_whenNotOnGround_returnsNextOnAirState_andDoesNotResetJumpCounter() {
        KnightState onAir = mock(KnightState.class);
        when(knight.isOnGround()).thenReturn(false);

        TestableMaxVelocityState state = new TestableMaxVelocityState(knight, onAir);

        KnightState next = state.getNextState();
        assertSame(onAir, next);

        verify(knight, never()).setJumpCounter(0);
    }

    @Test
    public void getNextState_onGround_overMaxXVelocity_returnsDashState() {
        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(true);

        TestableMaxVelocityState state = new TestableMaxVelocityState(knight, mock(KnightState.class));

        KnightState next = state.getNextState();
        assertInstanceOf(DashState.class, next);

        verify(knight).setJumpCounter(0);
    }

    @Test
    public void getNextState_onGround_belowRunningMax_returnsRunningState() {
        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(RunningState.MAX_VELOCITY - 0.01, 0.0)); // < MAX

        TestableMaxVelocityState state = new TestableMaxVelocityState(knight, mock(KnightState.class));

        KnightState next = state.getNextState();
        assertInstanceOf(RunningState.class, next);

        verify(knight).setJumpCounter(0);
    }

    @Test
    public void getNextState_onGround_atOrAboveRunningMax_returnsThis() {
        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(RunningState.MAX_VELOCITY, 0.0)); // NOT < MAX => this

        TestableMaxVelocityState state = new TestableMaxVelocityState(knight, mock(KnightState.class));

        KnightState next = state.getNextState();
        assertSame(state, next);

        verify(knight).setJumpCounter(0);
    }
}
