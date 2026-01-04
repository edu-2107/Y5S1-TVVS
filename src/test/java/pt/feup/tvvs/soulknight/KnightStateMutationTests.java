package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.knight.*;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KnightStateMutationTests {

    private Knight knight;
    private Scene scene;

    // Subclasse para expor métodos protected do KnightState (sem mexer no código production)
    static class TestableKnightState extends KnightState {
        public TestableKnightState(Knight knight) { super(knight); }

        // wrappers públicos para métodos protected
        public Vector exposeLimitVelocity(Vector v) { return super.limitVelocity(v); }
        public Vector exposeApplyCollisions(Vector v) { return super.applyCollisions(v); }
        public KnightState exposeNextGround() { return super.getNextGroundState(); }
        public KnightState exposeNextOnAir() { return super.getNextOnAirState(); }

        @Override public Vector jump() { return getKnight().getVelocity(); }
        @Override public Vector dash() { return getKnight().getVelocity(); }
        @Override public Vector updateVelocity(Vector newVelocity) { return newVelocity; }
        @Override public KnightState getNextState() throws IOException { return this; }
    }

    @BeforeEach
    public void setUp() {
        knight = mock(Knight.class);
        scene = mock(Scene.class);

        when(knight.getScene()).thenReturn(scene);
        when(knight.getPosition()).thenReturn(new Position(0, 0));
        when(knight.getWidth()).thenReturn((int) 10.0);
        when(knight.getHeight()).thenReturn((int) 9.0);

        // defaults: sem colisões
        when(scene.collidesDown(any(), any())).thenReturn(false);
        when(scene.collidesUp(any(), any())).thenReturn(false);
        when(scene.collidesLeft(any(), any())).thenReturn(false);
        when(scene.collidesRight(any(), any())).thenReturn(false);
    }

    @Test
    public void limitVelocity_clampsToMax_andZeroesSmallVx() {
        when(knight.getMaxVelocity()).thenReturn(new Vector(2.0, 3.0));

        TestableKnightState st = new TestableKnightState(knight);

        // clamp forte (mata mutantes min/max e sinais)
        Vector out1 = st.exposeLimitVelocity(new Vector(5.0, 4.0));
        assertEquals(2.0, out1.x(), 1e-9);
        assertEquals(3.0, out1.y(), 1e-9);

        Vector out2 = st.exposeLimitVelocity(new Vector(-5.0, 1.0));
        assertEquals(-2.0, out2.x(), 1e-9);
        assertEquals(1.0, out2.y(), 1e-9);

        // branch chave: |vx| < 0.2 => vx = 0
        Vector out3 = st.exposeLimitVelocity(new Vector(0.19, 0.0));
        assertEquals(0.0, out3.x(), 1e-9);

        // e no limiar não deve ir a zero (mata mutantes < vs <=)
        Vector out4 = st.exposeLimitVelocity(new Vector(0.2, 0.0));
        assertEquals(0.2, out4.x(), 1e-9);
    }

    @Test
    public void applyCollisions_resolvesDownwardWhileLoop_precisely() {
        when(knight.getMaxVelocity()).thenReturn(new Vector(100, 100));
        TestableKnightState st = new TestableKnightState(knight);

        // vy > 0 e colide 2 vezes, depois deixa de colidir
        when(scene.collidesDown(any(), any())).thenReturn(true, true, false);

        Vector out = st.exposeApplyCollisions(new Vector(0.0, 3.2));
        // 3.2 -> 2.2 -> 1.2 -> stop => 1.2
        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(1.2, out.y(), 1e-9);
    }

    @Test
    public void applyCollisions_resolvesUpwardWhileLoop_precisely() {
        when(knight.getMaxVelocity()).thenReturn(new Vector(100, 100));
        TestableKnightState st = new TestableKnightState(knight);

        // vy < 0 e colide 2 vezes, depois deixa de colidir
        when(scene.collidesUp(any(), any())).thenReturn(true, true, false);

        Vector out = st.exposeApplyCollisions(new Vector(0.0, -2.5));
        // -2.5 -> -1.5 -> -0.5 -> stop => -0.5
        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(-0.5, out.y(), 1e-9);
    }

    @Test
    public void applyCollisions_resolvesLeftWhileLoop_untilStopsAtZero() {
        when(knight.getMaxVelocity()).thenReturn(new Vector(100, 100));
        TestableKnightState st = new TestableKnightState(knight);

        // força o while do vx < 0 até vx chegar a 0
        when(scene.collidesLeft(any(), any())).thenReturn(true);

        Vector out = st.exposeApplyCollisions(new Vector(-2.3, 0.0));
        // -2.3 -> -1.3 -> -0.3 -> 0.0
        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);
    }

    @Test
    public void applyCollisions_resolvesRightWhileLoop_untilStopsAtZero() {
        when(knight.getMaxVelocity()).thenReturn(new Vector(100, 100));
        TestableKnightState st = new TestableKnightState(knight);

        // força o while do vx > 0 até vx chegar a 0
        when(scene.collidesRight(any(), any())).thenReturn(true);

        Vector out = st.exposeApplyCollisions(new Vector(2.7, 0.0));
        // 2.7 -> 1.7 -> 0.7 -> 0.0
        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);
    }

    @Test
    public void getNextGroundState_respectsThresholds_running_walking_idle() {
        TestableKnightState st = new TestableKnightState(knight);

        // >= RunningState.MIN_VELOCITY => RunningState
        when(knight.getVelocity()).thenReturn(new Vector(RunningState.MIN_VELOCITY, 0.0));
        assertInstanceOf(RunningState.class, st.exposeNextGround());

        // >= WalkingState.MIN_VELOCITY e < RunningState.MIN_VELOCITY => WalkingState
        when(knight.getVelocity()).thenReturn(new Vector(WalkingState.MIN_VELOCITY, 0.0));
        assertInstanceOf(WalkingState.class, st.exposeNextGround());

        // < WalkingState.MIN_VELOCITY => IdleState
        when(knight.getVelocity()).thenReturn(new Vector(WalkingState.MIN_VELOCITY - 0.01, 0.0));
        assertInstanceOf(IdleState.class, st.exposeNextGround());
    }

    @Test
    public void getNextOnAirState_jumpWhenVyNegative_elseFalling() {
        TestableKnightState st = new TestableKnightState(knight);

        when(knight.getVelocity()).thenReturn(new Vector(0.0, -0.1));
        assertInstanceOf(JumpState.class, st.exposeNextOnAir());

        when(knight.getVelocity()).thenReturn(new Vector(0.0, 0.0));
        assertInstanceOf(FallingState.class, st.exposeNextOnAir());
    }

    @Test
    public void particlesTimer_tickAndReset_areConsistent() {
        TestableKnightState st = new TestableKnightState(knight);

        assertEquals(100, st.getParticlesTimer());
        st.tickParticles();
        st.tickParticles();
        assertEquals(98, st.getParticlesTimer());

        st.resetParticlesTimer();
        assertEquals(100, st.getParticlesTimer());
    }
}
