package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.knight.*;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class KnightStateWhiteBoxTests {

    private Knight createDefaultKnight() {
        // x=0, y=0, HP=50, Damage=2.0, Energy=100
        return new Knight(0, 0, 50, 2.0f, 100);
    }

    private static class CollisionKnightState extends KnightState {
        CollisionKnightState(Knight knight) {
            super(knight);
        }

        Vector callApplyCollisions(Vector v) {
            return super.applyCollisions(v);
        }

        @Override public Vector jump() { return new Vector(0,0); }
        @Override public Vector dash() { return new Vector(0,0); }
        @Override public Vector updateVelocity(Vector newVelocity) { return newVelocity; }
        @Override public KnightState getNextState() { return this; }
    }


    private static class TestKnightState extends KnightState {

        private final Vector collisionResult;

        TestKnightState(Knight knight) {
            this(knight, new Vector(0.0, 0.0));
        }

        TestKnightState(Knight knight, Vector collisionResult) {
            super(knight);
            this.collisionResult = collisionResult;
        }

        @Override
        protected Vector applyCollisions(Vector velocity) {
            return collisionResult;
        }

        @Override
        public Vector jump() {
            return new Vector(0.0, 0.0);
        }

        @Override
        public Vector dash() {
            return new Vector(0.0, 0.0);
        }

        @Override
        public Vector updateVelocity(Vector newVelocity) {
            return limitVelocity(newVelocity);
        }

        @Override
        public KnightState getNextState() {
            return this;
        }
        public Vector callLimitVelocity(Vector velocity) {
            return limitVelocity(velocity);
        }

        public KnightState callGetNextGroundState() {
            return getNextGroundState();
        }

        public KnightState callGetNextOnAirState() {
            return getNextOnAirState();
        }
    }

    // ---------- moveKnightLeft / moveKnightRight ----------

    @Test
    public void moveKnightLeftDecreasesVelocityXByAcceleration() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(0.0, 0.0)); // aceleração = 0.75

        TestKnightState state = new TestKnightState(knight);

        Vector result = state.moveKnightLeft();

        assertEquals(new Vector(-0.75, 0.0), result);
    }

    @Test
    public void moveKnightRightIncreasesVelocityXByAcceleration() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(0.0, 0.0));

        TestKnightState state = new TestKnightState(knight);

        Vector result = state.moveKnightRight();

        assertEquals(new Vector(0.75, 0.0), result);
    }

    // ---------- limitVelocity ----------

    @Test
    public void limitVelocityClampsXAboveMaxVelocity() {
        Knight knight = createDefaultKnight();
        // maxVelocity.x() = 2.0
        TestKnightState state = new TestKnightState(knight);

        Vector limited = state.callLimitVelocity(new Vector(10.0, 0.0));

        assertEquals(new Vector(2.0, 0.0), limited);
    }

    @Test
    public void limitVelocityClampsXBelowMinusMaxVelocity() {
        Knight knight = createDefaultKnight();
        TestKnightState state = new TestKnightState(knight);

        Vector limited = state.callLimitVelocity(new Vector(-10.0, 0.0));

        assertEquals(new Vector(-2.0, 0.0), limited);
    }

    @Test
    public void limitVelocityClampsYAboveMaxVelocityY() {
        Knight knight = createDefaultKnight();
        // maxVelocity.y() = 4.0
        TestKnightState state = new TestKnightState(knight);

        Vector limited = state.callLimitVelocity(new Vector(0.0, 10.0));

        assertEquals(new Vector(0.0, 4.0), limited);
    }

    @Test
    public void limitVelocitySetsVerySmallXComponentToZero() {
        Knight knight = createDefaultKnight();
        TestKnightState state = new TestKnightState(knight);

        Vector limited = state.callLimitVelocity(new Vector(0.1, 1.0));

        assertEquals(new Vector(0.0, 1.0), limited);
    }

    // ---------- getNextGroundState ----------

    @Test
    public void getNextGroundStateReturnsIdleWhenVelocityIsVeryLow() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(0.0, 0.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState result = state.callGetNextGroundState();

        assertInstanceOf(IdleState.class, result);
    }

    @Test
    public void getNextGroundStateReturnsWalkingWhenVelocityAboveWalkingMinButBelowRunningMin() {
        Knight knight = createDefaultKnight();
        double mid =
                (WalkingState.MIN_VELOCITY + RunningState.MIN_VELOCITY) / 2.0;
        knight.setVelocity(new Vector(mid, 0.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState result = state.callGetNextGroundState();

        assertInstanceOf(WalkingState.class, result);
    }

    @Test
    public void getNextGroundStateReturnsRunningWhenVelocityAboveRunningMin() {
        Knight knight = createDefaultKnight();
        double aboveRunning = RunningState.MIN_VELOCITY + 0.5;
        knight.setVelocity(new Vector(aboveRunning, 0.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState result = state.callGetNextGroundState();

        assertInstanceOf(RunningState.class, result);
    }

    // ---------- getNextOnAirState ----------

    @Test
    public void getNextOnAirStateReturnsJumpStateWhenVerticalVelocityIsNegative() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(0.0, -1.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState result = state.callGetNextOnAirState();

        assertInstanceOf(JumpState.class, result);
    }

    @Test
    public void getNextOnAirStateReturnsFallingStateWhenVerticalVelocityIsNonNegative() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(0.0, 1.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState result = state.callGetNextOnAirState();

        assertInstanceOf(FallingState.class, result);
    }

    // ---------- particles timer ----------

    @Test
    public void tickParticlesDecrementsParticlesTimer() {
        Knight knight = createDefaultKnight();
        TestKnightState state = new TestKnightState(knight);

        state.tickParticles();
        long timer = state.getParticlesTimer();

        assertEquals(99, timer);
    }

    @Test
    public void resetParticlesTimerResetsValueTo100() {
        Knight knight = createDefaultKnight();
        TestKnightState state = new TestKnightState(knight);

        state.tickParticles();
        state.resetParticlesTimer();

        assertEquals(100, state.getParticlesTimer());
    }

    @Test
    public void applyCollisions_noCollisions_returnsSameVelocity() {
        Knight knight = mock(Knight.class);
        Scene scene = mock(Scene.class);

        when(knight.getScene()).thenReturn(scene);
        when(knight.getPosition()).thenReturn(new Position(0, 0));
        when(knight.getWidth()).thenReturn((int) 1.0);
        when(knight.getHeight()).thenReturn((int) 1.0);

        // nenhuma colisão em lado nenhum
        when(scene.collidesDown(any(), any())).thenReturn(false);
        when(scene.collidesUp(any(), any())).thenReturn(false);
        when(scene.collidesLeft(any(), any())).thenReturn(false);
        when(scene.collidesRight(any(), any())).thenReturn(false);

        CollisionKnightState state = new CollisionKnightState(knight);

        Vector out = state.callApplyCollisions(new Vector(0.0, 0.0));
        assertEquals(new Vector(0.0, 0.0), out);
    }

    @Test
    public void applyCollisions_downCollision_reducesPositiveVy_untilNotColliding() {
        Knight knight = mock(Knight.class);
        Scene scene = mock(Scene.class);

        when(knight.getScene()).thenReturn(scene);
        when(knight.getPosition()).thenReturn(new Position(0, 0));
        when(knight.getWidth()).thenReturn((int) 1.0);
        when(knight.getHeight()).thenReturn((int) 1.0);

        // vy=3: true (vy->2), true (->1), false (para)
        when(scene.collidesDown(any(), any())).thenReturn(true, true, false);
        when(scene.collidesUp(any(), any())).thenReturn(false);
        when(scene.collidesLeft(any(), any())).thenReturn(false);
        when(scene.collidesRight(any(), any())).thenReturn(false);

        CollisionKnightState state = new CollisionKnightState(knight);

        Vector out = state.callApplyCollisions(new Vector(0.0, 3.0));

        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(1.0, out.y(), 1e-9);
    }

    @Test
    public void applyCollisions_upCollision_increasesNegativeVy_untilNotColliding() {
        Knight knight = mock(Knight.class);
        Scene scene = mock(Scene.class);

        when(knight.getScene()).thenReturn(scene);
        when(knight.getPosition()).thenReturn(new Position(0, 0));
        when(knight.getWidth()).thenReturn((int) 1.0);
        when(knight.getHeight()).thenReturn((int) 1.0);

        // vy=-3: true (vy->-2), true (->-1), false (para)
        when(scene.collidesUp(any(), any())).thenReturn(true, true, false);
        when(scene.collidesDown(any(), any())).thenReturn(false);
        when(scene.collidesLeft(any(), any())).thenReturn(false);
        when(scene.collidesRight(any(), any())).thenReturn(false);

        CollisionKnightState state = new CollisionKnightState(knight);

        Vector out = state.callApplyCollisions(new Vector(0.0, -3.0));

        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(-1.0, out.y(), 1e-9);
    }

    @Test
    public void applyCollisions_leftCollision_increasesNegativeVx_untilNotColliding() {
        Knight knight = mock(Knight.class);
        Scene scene = mock(Scene.class);

        when(knight.getScene()).thenReturn(scene);
        when(knight.getPosition()).thenReturn(new Position(0, 0));
        when(knight.getWidth()).thenReturn((int) 1.0);
        when(knight.getHeight()).thenReturn((int) 1.0);

        // vx=-3: true (vx->-2), true (->-1), false (para)
        when(scene.collidesLeft(any(), any())).thenReturn(true, true, false);
        when(scene.collidesDown(any(), any())).thenReturn(false);
        when(scene.collidesUp(any(), any())).thenReturn(false);
        when(scene.collidesRight(any(), any())).thenReturn(false);

        CollisionKnightState state = new CollisionKnightState(knight);

        Vector out = state.callApplyCollisions(new Vector(-3.0, 0.0));

        assertEquals(-1.0, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);
    }

    @Test
    public void applyCollisions_rightCollision_reducesPositiveVx_untilNotColliding() {
        Knight knight = mock(Knight.class);
        Scene scene = mock(Scene.class);

        when(knight.getScene()).thenReturn(scene);
        when(knight.getPosition()).thenReturn(new Position(0, 0));
        when(knight.getWidth()).thenReturn((int) 1.0);
        when(knight.getHeight()).thenReturn((int) 1.0);

        // vx=3: true (vx->2), true (->1), false (para)
        when(scene.collidesRight(any(), any())).thenReturn(true, true, false);
        when(scene.collidesDown(any(), any())).thenReturn(false);
        when(scene.collidesUp(any(), any())).thenReturn(false);
        when(scene.collidesLeft(any(), any())).thenReturn(false);

        CollisionKnightState state = new CollisionKnightState(knight);

        Vector out = state.callApplyCollisions(new Vector(3.0, 0.0));

        assertEquals(1.0, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);
    }

}
