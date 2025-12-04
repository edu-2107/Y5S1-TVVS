package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.knight.*;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;

import static org.junit.jupiter.api.Assertions.*;

class KnightStateWhiteBoxTests {

    private Knight createDefaultKnight() {
        // x=0, y=0, HP=50, Damage=2.0, Energy=100
        return new Knight(0, 0, 50, 2.0f, 100);
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
    void moveKnightLeftDecreasesVelocityXByAcceleration() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(0.0, 0.0)); // aceleração = 0.75

        TestKnightState state = new TestKnightState(knight);

        Vector result = state.moveKnightLeft();

        assertEquals(new Vector(-0.75, 0.0), result);
    }

    @Test
    void moveKnightRightIncreasesVelocityXByAcceleration() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(0.0, 0.0));

        TestKnightState state = new TestKnightState(knight);

        Vector result = state.moveKnightRight();

        assertEquals(new Vector(0.75, 0.0), result);
    }

    // ---------- limitVelocity ----------

    @Test
    void limitVelocityClampsXAboveMaxVelocity() {
        Knight knight = createDefaultKnight();
        // maxVelocity.x() = 2.0
        TestKnightState state = new TestKnightState(knight);

        Vector limited = state.callLimitVelocity(new Vector(10.0, 0.0));

        assertEquals(new Vector(2.0, 0.0), limited);
    }

    @Test
    void limitVelocityClampsXBelowMinusMaxVelocity() {
        Knight knight = createDefaultKnight();
        TestKnightState state = new TestKnightState(knight);

        Vector limited = state.callLimitVelocity(new Vector(-10.0, 0.0));

        assertEquals(new Vector(-2.0, 0.0), limited);
    }

    @Test
    void limitVelocityClampsYAboveMaxVelocityY() {
        Knight knight = createDefaultKnight();
        // maxVelocity.y() = 4.0
        TestKnightState state = new TestKnightState(knight);

        Vector limited = state.callLimitVelocity(new Vector(0.0, 10.0));

        assertEquals(new Vector(0.0, 4.0), limited);
    }

    @Test
    void limitVelocitySetsVerySmallXComponentToZero() {
        Knight knight = createDefaultKnight();
        TestKnightState state = new TestKnightState(knight);

        Vector limited = state.callLimitVelocity(new Vector(0.1, 1.0));

        assertEquals(new Vector(0.0, 1.0), limited);
    }

    // ---------- getNextGroundState ----------

    @Test
    void getNextGroundStateReturnsIdleWhenVelocityIsVeryLow() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(0.0, 0.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState result = state.callGetNextGroundState();

        assertTrue(result instanceof IdleState);
    }

    @Test
    void getNextGroundStateReturnsWalkingWhenVelocityAboveWalkingMinButBelowRunningMin() {
        Knight knight = createDefaultKnight();
        double mid =
                (WalkingState.MIN_VELOCITY + RunningState.MIN_VELOCITY) / 2.0;
        knight.setVelocity(new Vector(mid, 0.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState result = state.callGetNextGroundState();

        assertTrue(result instanceof WalkingState);
    }

    @Test
    void getNextGroundStateReturnsRunningWhenVelocityAboveRunningMin() {
        Knight knight = createDefaultKnight();
        double aboveRunning = RunningState.MIN_VELOCITY + 0.5;
        knight.setVelocity(new Vector(aboveRunning, 0.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState result = state.callGetNextGroundState();

        assertTrue(result instanceof RunningState);
    }

    // ---------- getNextOnAirState ----------

    @Test
    void getNextOnAirStateReturnsJumpStateWhenVerticalVelocityIsNegative() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(0.0, -1.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState result = state.callGetNextOnAirState();

        assertTrue(result instanceof JumpState);
    }

    @Test
    void getNextOnAirStateReturnsFallingStateWhenVerticalVelocityIsNonNegative() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(0.0, 1.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState result = state.callGetNextOnAirState();

        assertTrue(result instanceof FallingState);
    }

    // ---------- particles timer ----------

    @Test
    void tickParticlesDecrementsParticlesTimer() {
        Knight knight = createDefaultKnight();
        TestKnightState state = new TestKnightState(knight);

        state.tickParticles();
        long timer = state.getParticlesTimer();

        assertEquals(99, timer);
    }

    @Test
    void resetParticlesTimerResetsValueTo100() {
        Knight knight = createDefaultKnight();
        TestKnightState state = new TestKnightState(knight);

        state.tickParticles();
        state.resetParticlesTimer();

        assertEquals(100, state.getParticlesTimer());
    }
}
