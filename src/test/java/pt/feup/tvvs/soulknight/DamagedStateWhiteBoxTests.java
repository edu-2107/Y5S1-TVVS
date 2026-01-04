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

class DamagedStateWhiteBoxTests {

    private Knight knight;
    private Scene scene;

    static class TestableDamagedState extends DamagedState {
        private final KnightState onAirReturn;

        TestableDamagedState(Knight knight, int particles, KnightState onAirReturn) {
            super(knight, particles);
            this.onAirReturn = onAirReturn;
        }

        @Override
        public void tickParticles() {
            // no-op
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
    }

    @BeforeEach
    void setUp() {
        knight = mock(Knight.class);
        scene = mock(Scene.class);
        when(knight.getScene()).thenReturn(scene);

        // defaults "safe"
        when(scene.collideSpike()).thenReturn(false);
        when(knight.getHP()).thenReturn(10);

        when(knight.getAcceleration()).thenReturn(0.9);
        when(scene.getGravity()).thenReturn(0.25);

        when(knight.getJumpBoost()).thenReturn(2.0);
        when(knight.getDashBoost()).thenReturn(3.0);
        when(knight.isFacingRight()).thenReturn(true);

        when(knight.getVelocity()).thenReturn(new Vector(0.0, 0.0));
        when(knight.getJumpCounter()).thenReturn(0);

        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);

        when(knight.createRespawnParticles(anyInt())).thenReturn(Collections.emptyList());
        when(knight.createParticlesJump(anyInt())).thenReturn(Collections.emptyList());
        when(knight.createDashParticles(anyInt())).thenReturn(Collections.emptyList());
    }

    private void advanceTicksToEnd(DamagedState state) {
        // ticks começa em 0 e enquanto ticks < 15 retorna this e incrementa ticks
        for (int i = 0; i < 15; i++) {
            assertSame(state, state.getNextState());
        }
    }

    @Test
    public void constructor_setsRespawnParticles() {
        new TestableDamagedState(knight, 7, mock(KnightState.class));
        verify(knight).createRespawnParticles(7);
        verify(scene).setRespawnParticles(anyList());
    }

    @Test
    public void jump_incrementsJumpCounter_setsJumpParticles_andReturnsUpdatedVelocity() {
        TestableDamagedState state = new TestableDamagedState(knight, 1, mock(KnightState.class));

        when(knight.getJumpCounter()).thenReturn(1);
        when(knight.getVelocity()).thenReturn(new Vector(1.0, 1.0));
        when(knight.getJumpBoost()).thenReturn(2.0);

        Vector out = state.jump();

        verify(knight).setJumpCounter(2);
        verify(knight).createParticlesJump(10);
        verify(scene).setJumpParticles(anyList());

        // newVelocity = (1, 1-2) = (1, -1)
        // updateVelocity => x=1*0.9=0.9 ; y=-1+0.25=-0.75
        assertEquals(0.9, out.x(), 1e-9);
        assertEquals(-0.75, out.y(), 1e-9);
    }

    @Test
    public void dash_facingRight_true_and_false() {
        TestableDamagedState state = new TestableDamagedState(knight, 1, mock(KnightState.class));
        when(knight.getVelocity()).thenReturn(new Vector(0.0, 5.0));
        when(knight.getDashBoost()).thenReturn(3.0);

        when(knight.isFacingRight()).thenReturn(true);
        Vector out1 = state.dash();
        assertEquals(3.0, out1.x(), 1e-9);
        assertEquals(5.0, out1.y(), 1e-9);

        when(knight.isFacingRight()).thenReturn(false);
        Vector out2 = state.dash();
        assertEquals(-3.0, out2.x(), 1e-9);
        assertEquals(5.0, out2.y(), 1e-9);

        verify(knight, atLeastOnce()).createDashParticles(10);
        verify(scene, atLeastOnce()).setDashParticles(anyList());
    }

    @Test
    public void updateVelocity_appliesAccelerationAndGravity() {
        TestableDamagedState state = new TestableDamagedState(knight, 1, mock(KnightState.class));
        when(knight.getAcceleration()).thenReturn(0.5);
        when(scene.getGravity()).thenReturn(1.0);

        Vector out = state.updateVelocity(new Vector(2.0, 3.0));
        assertEquals(1.0, out.x(), 1e-9);
        assertEquals(4.0, out.y(), 1e-9);
    }

    // -------- getNextState branches --------

    @Test
    public void getNextState_whenCollideSpike_returnsRespawnState10() {
        when(scene.collideSpike()).thenReturn(true);
        DamagedState state = new TestableDamagedState(knight, 1, mock(KnightState.class));

        KnightState next = state.getNextState();
        assertInstanceOf(RespawnState.class, next);
    }

    @Test
    public void getNextState_whenHpZero_returnsRespawnState5() {
        when(scene.collideSpike()).thenReturn(false);
        when(knight.getHP()).thenReturn(0);

        DamagedState state = new TestableDamagedState(knight, 1, mock(KnightState.class));

        KnightState next = state.getNextState();
        assertInstanceOf(RespawnState.class, next);
    }

    @Test
    public void getNextState_afterTicks_notOnGround_returnsNextOnAirState_andSetsGotHitFalse() {
        KnightState onAir = mock(KnightState.class);
        TestableDamagedState state = new TestableDamagedState(knight, 1, onAir);

        advanceTicksToEnd(state);

        when(knight.isOnGround()).thenReturn(false);

        KnightState next = state.getNextState();
        assertSame(onAir, next);

        verify(knight).setGotHit(false);
        verify(knight, never()).setJumpCounter(0);
    }

    @Test
    public void getNextState_afterTicks_onGround_overMaxXVelocity_returnsDashState_andResetsJumpCounter() {
        TestableDamagedState state = new TestableDamagedState(knight, 1, mock(KnightState.class));

        advanceTicksToEnd(state);

        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(true);

        KnightState next = state.getNextState();
        assertInstanceOf(DashState.class, next);

        verify(knight).setGotHit(false);
        verify(knight).setJumpCounter(0);
    }

    @Test
    public void getNextState_afterTicks_onGround_walkingVelocity_returnsWalkingState() {
        TestableDamagedState state = new TestableDamagedState(knight, 1, mock(KnightState.class));

        advanceTicksToEnd(state);

        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(WalkingState.MIN_VELOCITY, 0.0));

        KnightState next = state.getNextState();
        assertInstanceOf(WalkingState.class, next);

        verify(knight).setGotHit(false);
        verify(knight).setJumpCounter(0);
    }

    @Test
    public void getNextState_afterTicks_onGround_belowMinVelocity_returnsIdleState() {
        TestableDamagedState state = new TestableDamagedState(knight, 1, mock(KnightState.class));

        advanceTicksToEnd(state);

        when(knight.isOnGround()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(0.0, 0.0));

        KnightState next = state.getNextState();
        assertInstanceOf(IdleState.class, next);

        verify(knight).setGotHit(false);
        verify(knight).setJumpCounter(0);
    }
}
