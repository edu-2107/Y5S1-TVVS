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

class WalkingStateWhiteBoxTests {

    private Knight knight;
    private Scene scene;

    static class TestableWalkingState extends WalkingState {
        private int particlesTimer;
        private final KnightState onAirReturn;

        TestableWalkingState(Knight knight, KnightState onAirReturn) {
            super(knight);
            this.onAirReturn = onAirReturn;
            this.particlesTimer = 1;
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

        // Defaults
        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(0.0, 0.0));
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
    public void jump_incrementsCounter_setsJumpParticles_andReturnsNewVelocity() {
        TestableWalkingState state = new TestableWalkingState(knight, mock(KnightState.class));

        when(knight.getJumpCounter()).thenReturn(1);
        when(knight.getVelocity()).thenReturn(new Vector(1.0, 1.0));
        when(knight.getJumpBoost()).thenReturn(2.0);

        Vector out = state.jump();

        verify(knight).setJumpCounter(2);
        verify(knight).createParticlesJump(10);
        verify(scene).setJumpParticles(anyList());

        assertEquals(1.0, out.x(), 1e-9);
        assertEquals(-1.0, out.y(), 1e-9);
    }

    @Test
    public void dash_facingRight_true_and_false() {
        TestableWalkingState state = new TestableWalkingState(knight, mock(KnightState.class));
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
        TestableWalkingState state = new TestableWalkingState(knight, mock(KnightState.class));
        when(knight.getAcceleration()).thenReturn(0.5);

        Vector out = state.updateVelocity(new Vector(2.0, 7.0));
        assertEquals(1.0, out.x(), 1e-9); // 2 * 0.5
        assertEquals(7.0, out.y(), 1e-9); // y unchanged
    }

    // -------- getNextState branches --------

    @Test
    public void getNextState_whenCollideSpike_returnsRespawnState() {
        when(scene.collideSpike()).thenReturn(true);

        TestableWalkingState state = new TestableWalkingState(knight, mock(KnightState.class));
        KnightState next = state.getNextState();

        assertInstanceOf(RespawnState.class, next);
    }

    @Test
    public void getNextState_whenParticlesTimerZero_setsRespawnParticles_andResetsTimer_thenContinues() {
        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(0.8, 0.0));

        TestableWalkingState state = new TestableWalkingState(knight, mock(KnightState.class));
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

        TestableWalkingState state = new TestableWalkingState(knight, onAir);

        KnightState next = state.getNextState();

        assertSame(onAir, next);
        verify(knight, never()).setJumpCounter(0);
    }

    @Test
    public void getNextState_onGround_overMaxXVelocity_returnsDashState() {
        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(true);

        TestableWalkingState state = new TestableWalkingState(knight, mock(KnightState.class));

        KnightState next = state.getNextState();

        assertInstanceOf(DashState.class, next);
        verify(knight).setJumpCounter(0);
    }

    @Test
    public void getNextState_onGround_runningVelocity_returnsRunningState() {
        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(RunningState.MIN_VELOCITY, 0.0));

        TestableWalkingState state = new TestableWalkingState(knight, mock(KnightState.class));

        KnightState next = state.getNextState();

        assertInstanceOf(RunningState.class, next);
        verify(knight).setJumpCounter(0);
    }

    @Test
    public void getNextState_onGround_belowWalkingMin_returnsIdleState() {
        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(WalkingState.MIN_VELOCITY - 0.01, 0.0));

        TestableWalkingState state = new TestableWalkingState(knight, mock(KnightState.class));

        KnightState next = state.getNextState();

        assertInstanceOf(IdleState.class, next);
        verify(knight).setJumpCounter(0);
    }

    @Test
    public void getNextState_onGround_betweenWalkingMinAndRunningMin_returnsThis() {
        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);

        // vx >= Walking.MIN (0.75) e < Running.MIN
        double vx = (WalkingState.MIN_VELOCITY + RunningState.MIN_VELOCITY) / 2.0;
        when(knight.getVelocity()).thenReturn(new Vector(vx, 0.0));

        TestableWalkingState state = new TestableWalkingState(knight, mock(KnightState.class));

        KnightState next = state.getNextState();

        assertSame(state, next);
        verify(knight).setJumpCounter(0);
    }
}
