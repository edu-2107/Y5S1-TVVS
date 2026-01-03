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

class JumpStateWhiteBoxTests {

    private Knight knight;
    private Scene scene;

    static class TestableJumpState extends JumpState {
        private int particlesTimer = 1; // default != 0

        public TestableJumpState(Knight knight) {
            super(knight);
        }

        public void setParticlesTimer(int value) {
            this.particlesTimer = value;
        }

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
        public long getParticlesTimer() {
            return particlesTimer;
        }

        @Override
        public void resetParticlesTimer() {
            this.particlesTimer = 1;
        }
    }

    @BeforeEach
    void setUp() {
        knight = mock(Knight.class);
        scene = mock(Scene.class);

        when(knight.getScene()).thenReturn(scene);

        when(scene.getGravity()).thenReturn(0.25);
        when(knight.getAcceleration()).thenReturn(0.9);
        when(knight.getDashBoost()).thenReturn(2.0);
        when(knight.getJumpBoost()).thenReturn(3.0);
        when(knight.isFacingRight()).thenReturn(true);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(scene.collideSpike()).thenReturn(false);

        // listas de partículas para não rebentar em setXParticles(...)
        when(knight.createParticlesDoubleJump(anyInt(), any(Scene.class))).thenReturn(Collections.emptyList());
        when(knight.createDashParticles(anyInt())).thenReturn(Collections.emptyList());
        when(knight.createRespawnParticles(anyInt())).thenReturn(Collections.emptyList());
    }

    // ---------------- updateVelocity(Vector) ----------------

    @Test
    void updateVelocity_whenCurrentVyBetweenMinus05And0_usesHalfGravityBranch() {
        // condição do if: getKnight().getVelocity().y() < 0 && >= -0.5
        when(knight.getVelocity()).thenReturn(new Vector(0.0, -0.3));

        TestableJumpState state = new TestableJumpState(knight);

        Vector out = state.updateVelocity(new Vector(2.0, -0.3));

        // x = newvelocity.x * accel = 2 * 0.9 = 1.8
        // y = newvelocity.y + gravity*0.5 = -0.3 + 0.25*0.5 = -0.175
        assertEquals(1.8, out.x(), 1e-9);
        assertEquals(-0.175, out.y(), 1e-9);
    }

    @Test
    void updateVelocity_whenCurrentVyOutsideRange_usesFullGravityElseBranch() {
        // falha o if (ex: -0.6 < -0.5)
        when(knight.getVelocity()).thenReturn(new Vector(0.0, -0.6));

        TestableJumpState state = new TestableJumpState(knight);

        Vector out = state.updateVelocity(new Vector(2.0, -0.6));

        // x = 2*0.9=1.8
        // y = -0.6 + 0.25 = -0.35
        assertEquals(1.8, out.x(), 1e-9);
        assertEquals(-0.35, out.y(), 1e-9);
    }

    // ---------------- dash() ----------------

    @Test
    void dash_facingRight_true_setsDashParticles_andUsesPositiveBoost() {
        when(knight.isFacingRight()).thenReturn(true);
        when(knight.getVelocity()).thenReturn(new Vector(0.0, 1.0));
        when(knight.getDashBoost()).thenReturn(2.0);

        TestableJumpState state = new TestableJumpState(knight);

        Vector out = state.dash();

        assertEquals(2.0, out.x(), 1e-9);
        assertEquals(1.0, out.y(), 1e-9);
        verify(scene, times(1)).setDashParticles(anyList());
    }

    @Test
    void dash_facingRight_false_setsDashParticles_andUsesNegativeBoost() {
        when(knight.isFacingRight()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(0.0, 1.0));
        when(knight.getDashBoost()).thenReturn(2.0);

        TestableJumpState state = new TestableJumpState(knight);

        Vector out = state.dash();

        assertEquals(-2.0, out.x(), 1e-9);
        assertEquals(1.0, out.y(), 1e-9);
        verify(scene, times(1)).setDashParticles(anyList());
    }

    // ---------------- jump() ----------------

    @Test
    void jump_whenJumpCounterLessThan2_entersIf_setsCounter_setsDoubleJumpParticles() {
        // entra no if
        when(knight.getJumpCounter()).thenReturn(1);
        // escolhe current vy -0.3 para também bater no branch de half-gravity do updateVelocity
        when(knight.getVelocity()).thenReturn(new Vector(1.0, -0.3));
        when(knight.getJumpBoost()).thenReturn(3.0);

        TestableJumpState state = new TestableJumpState(knight);

        Vector out = state.jump();

        // newVelocity y = -0.3 - (3/3) = -1.3
        // updateVelocity (half gravity): y = -1.3 + 0.125 = -1.175 ; x = 1*0.9 = 0.9
        assertEquals(0.9, out.x(), 1e-9);
        assertEquals(-1.175, out.y(), 1e-9);

        verify(knight, times(1)).setJumpCounter(2);
        verify(scene, times(1)).setDoubleJumpParticles(anyList());
    }

    @Test
    void jump_whenJumpCounterIs2_skipsIf_doesNotSetCounterOrParticles() {
        when(knight.getJumpCounter()).thenReturn(2);
        when(knight.getVelocity()).thenReturn(new Vector(1.0, -0.6));

        TestableJumpState state = new TestableJumpState(knight);

        Vector out = state.jump();

        // updateVelocity else-branch (full gravity): newvelocity = current velocity
        // x = 1*0.9 = 0.9; y = -0.6 + 0.25 = -0.35
        assertEquals(0.9, out.x(), 1e-9);
        assertEquals(-0.35, out.y(), 1e-9);

        verify(knight, never()).setJumpCounter(anyInt());
        verify(scene, never()).setDoubleJumpParticles(anyList());
    }

    // ---------------- getNextState() ----------------

    @Test
    void getNextState_whenCollideSpike_returnsRespawnState() {
        when(scene.collideSpike()).thenReturn(true);

        TestableJumpState state = new TestableJumpState(knight);

        KnightState next = state.getNextState();
        assertInstanceOf(RespawnState.class, next);
    }

    @Test
    void getNextState_whenParticlesTimerZero_setsRespawnParticles_andResetsTimer_thenReturnsThis() {
        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(0.0, -0.2)); // y < 0 para não cair em FallingState

        TestableJumpState state = new TestableJumpState(knight);
        state.setParticlesTimer(0);

        KnightState next = state.getNextState();

        assertSame(state, next);
        verify(scene, times(1)).setRespawnParticles(anyList());
        // implicitamente validado porque no fim ainda devolve this, mas:
        assertEquals(1, state.getParticlesTimer());
    }

    @Test
    void getNextState_whenOverMaxXVelocity_returnsDashState() {
        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOverMaxXVelocity()).thenReturn(true);

        TestableJumpState state = new TestableJumpState(knight);
        state.setParticlesTimer(1);

        KnightState next = state.getNextState();
        assertInstanceOf(DashState.class, next);
    }

    @Test
    void getNextState_whenVyNonNegative_returnsFallingState() {
        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(0.0, 0.0)); // y >= 0

        TestableJumpState state = new TestableJumpState(knight);
        state.setParticlesTimer(1);

        KnightState next = state.getNextState();
        assertInstanceOf(FallingState.class, next);
    }

    @Test
    void getNextState_whenNoConditionsMet_returnsThis() {
        when(scene.collideSpike()).thenReturn(false);
        when(knight.isOverMaxXVelocity()).thenReturn(false);
        when(knight.getVelocity()).thenReturn(new Vector(0.0, -0.2)); // y < 0

        TestableJumpState state = new TestableJumpState(knight);
        state.setParticlesTimer(1);

        KnightState next = state.getNextState();
        assertSame(state, next);
    }
}
