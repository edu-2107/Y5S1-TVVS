package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.knight.*;
import pt.feup.tvvs.soulknight.model.game.elements.particle.Particle;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class KnightStatesWhiteBoxTests {

    // ---------- Helpers ----------

    private Knight createKnightWithScene() {
        Knight knight = new Knight(0, 0, 50, 2.0f, 100);
        Scene scene = mock(Scene.class);

        when(scene.collideSpike()).thenReturn(false);
        when(scene.collidesDown(any(Position.class), any(Position.class))).thenReturn(false);
        when(scene.collidesUp(any(Position.class), any(Position.class))).thenReturn(false);
        when(scene.collidesLeft(any(Position.class), any(Position.class))).thenReturn(false);
        when(scene.collidesRight(any(Position.class), any(Position.class))).thenReturn(false);

        knight.setScene(scene);
        return knight;
    }

    private Knight createKnightOnGround() {
        Knight knight = createKnightWithScene();
        when(knight.getScene().collidesDown(any(Position.class), any(Position.class))).thenReturn(true);
        return knight;
    }

    private static class TestKnightState extends KnightState {

        TestKnightState(Knight knight) {
            super(knight);
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

        // protected methods

        public Vector callLimitVelocity(Vector v) {
            return limitVelocity(v);
        }

        public KnightState callGetNextGroundState() {
            return getNextGroundState();
        }

        public KnightState callGetNextOnAirState() {
            return getNextOnAirState();
        }
    }

    // ------------------------------------------------------------------
    // KnightState base: limitVelocity / ground/air transitions / timer
    // ------------------------------------------------------------------

    @Test
    void limitVelocityClampsXAboveMax() {
        Knight knight = createKnightWithScene();
        TestKnightState state = new TestKnightState(knight);

        Vector limited = state.callLimitVelocity(new Vector(10.0, 0.0));

        assertEquals(new Vector(2.0, 0.0), limited);
    }

    @Test
    void limitVelocityClampsXBelowMinusMax() {
        Knight knight = createKnightWithScene();
        TestKnightState state = new TestKnightState(knight);

        Vector limited = state.callLimitVelocity(new Vector(-10.0, 0.0));

        assertEquals(new Vector(-2.0, 0.0), limited);
    }

    @Test
    void limitVelocityClampsYToMaxY() {
        Knight knight = createKnightWithScene();
        TestKnightState state = new TestKnightState(knight);

        Vector limited = state.callLimitVelocity(new Vector(0.0, 10.0));

        assertEquals(new Vector(0.0, 4.0), limited);
    }

    @Test
    void limitVelocityZeroesVerySmallX() {
        Knight knight = createKnightWithScene();
        TestKnightState state = new TestKnightState(knight);

        Vector limited = state.callLimitVelocity(new Vector(0.1, 1.0));

        assertEquals(new Vector(0.0, 1.0), limited);
    }

    @Test
    void getNextGroundStateReturnsIdleWhenVelocityIsLow() {
        Knight knight = createKnightWithScene();
        knight.setVelocity(new Vector(0.0, 0.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState next = state.callGetNextGroundState();

        assertInstanceOf(IdleState.class, next);
    }

    @Test
    void getNextGroundStateReturnsWalkingWhenVelocityBetweenWalkingAndRunning() {
        Knight knight = createKnightWithScene();
        double mid = (WalkingState.MIN_VELOCITY + RunningState.MIN_VELOCITY) / 2.0;
        knight.setVelocity(new Vector(mid, 0.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState next = state.callGetNextGroundState();

        assertInstanceOf(WalkingState.class, next);
    }

    @Test
    void getNextGroundStateReturnsRunningWhenVelocityAboveRunningMin() {
        Knight knight = createKnightWithScene();
        double aboveRunning = RunningState.MIN_VELOCITY + 0.5;
        knight.setVelocity(new Vector(aboveRunning, 0.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState next = state.callGetNextGroundState();

        assertInstanceOf(RunningState.class, next);
    }

    @Test
    void getNextOnAirStateReturnsJumpWhenVerticalVelocityNegative() {
        Knight knight = createKnightWithScene();
        knight.setVelocity(new Vector(0.0, -1.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState next = state.callGetNextOnAirState();

        assertInstanceOf(JumpState.class, next);
    }

    @Test
    void getNextOnAirStateReturnsFallingWhenVerticalVelocityNonNegative() {
        Knight knight = createKnightWithScene();
        knight.setVelocity(new Vector(0.0, 1.0));

        TestKnightState state = new TestKnightState(knight);

        KnightState next = state.callGetNextOnAirState();

        assertInstanceOf(FallingState.class, next);
    }

    @Test
    void tickParticlesDecrementsParticlesTimer() {
        Knight knight = createKnightWithScene();
        TestKnightState state = new TestKnightState(knight);

        state.tickParticles();

        assertEquals(99, state.getParticlesTimer());
    }

    @Test
    void resetParticlesTimerResetsToHundred() {
        Knight knight = createKnightWithScene();
        TestKnightState state = new TestKnightState(knight);

        state.tickParticles();
        state.resetParticlesTimer();

        assertEquals(100, state.getParticlesTimer());
    }

    // ------------------------------------------------------------------
    // IdleState
    // ------------------------------------------------------------------

    @Test
    void idleStateJumpIncrementsJumpCounter() {
        Knight knight = createKnightWithScene();
        IdleState state = new IdleState(knight);

        state.jump();

        assertEquals(1, knight.getJumpCounter());
    }

    @Test
    void idleStateGetNextStateStaysIdleWhenOnGroundAndNoHorizontalVelocity() {
        Knight knight = createKnightOnGround();
        knight.setVelocity(new Vector(0.0, 0.0));
        IdleState state = new IdleState(knight);

        KnightState next = state.getNextState();

        assertInstanceOf(IdleState.class, next);
    }

    // ------------------------------------------------------------------
    // WalkingState
    // ------------------------------------------------------------------

    @Test
    void walkingStateGetNextStateGoesToIdleWhenVelocityBelowMin() {
        Knight knight = createKnightOnGround();
        knight.setVelocity(new Vector(WalkingState.MIN_VELOCITY / 2.0, 0.0));
        WalkingState state = new WalkingState(knight);

        KnightState next = state.getNextState();

        assertInstanceOf(IdleState.class, next);
    }

    @Test
    void walkingStateGetNextStateGoesToRunningWhenVelocityAboveRunningMin() {
        Knight knight = createKnightOnGround();
        knight.setVelocity(new Vector(RunningState.MIN_VELOCITY + 0.1, 0.0));
        WalkingState state = new WalkingState(knight);

        KnightState next = state.getNextState();

        assertInstanceOf(RunningState.class, next);
    }

    // ------------------------------------------------------------------
    // RunningState / MaxVelocityState
    // ------------------------------------------------------------------

    @Test
    void runningStateGetNextStateGoesToMaxVelocityWhenAtMax() {
        Knight knight = createKnightOnGround();
        knight.setVelocity(new Vector(RunningState.MAX_VELOCITY, 0.0)); // 2.0
        RunningState state = new RunningState(knight);

        KnightState next = state.getNextState();

        assertInstanceOf(MaxVelocityState.class, next);
    }

    @Test
    void runningStateGetNextStateGoesToWalkingWhenBelowMin() {
        Knight knight = createKnightOnGround();
        knight.setVelocity(new Vector(RunningState.MIN_VELOCITY / 2.0, 0.0));
        RunningState state = new RunningState(knight);

        KnightState next = state.getNextState();

        assertInstanceOf(WalkingState.class, next);
    }

    @Test
    void maxVelocityStateGetNextStateGoesToRunningWhenVelocityDropsBelowMax() {
        Knight knight = createKnightOnGround();
        knight.setVelocity(new Vector(RunningState.MAX_VELOCITY - 0.1, 0.0));
        MaxVelocityState state = new MaxVelocityState(knight);

        KnightState next = state.getNextState();

        assertInstanceOf(RunningState.class, next);
    }

    // ------------------------------------------------------------------
    // JumpState / FallingState
    // ------------------------------------------------------------------

    @Test
    void jumpStateJumpIncrementsJumpCounterIfBelowLimit() {
        Knight knight = createKnightWithScene();
        knight.setVelocity(new Vector(0.0, 0.0));
        JumpState state = new JumpState(knight);

        state.jump();

        assertEquals(1, knight.getJumpCounter());
    }

    @Test
    void fallingStateGetNextStateGoesToGroundStateWhenOnGround() {
        Knight knight = createKnightOnGround();
        knight.setVelocity(new Vector(0.0, 1.0)); // velocidade a cair
        FallingState state = new FallingState(knight);

        KnightState next = state.getNextState();

        assertInstanceOf(IdleState.class, next);
    }

    // ------------------------------------------------------------------
    // DashState / AfterDashState
    // ------------------------------------------------------------------

    @Test
    void dashStateGetNextStateGoesToAfterDashWhenVelocityBelowMin() {
        Knight knight = createKnightWithScene();
        knight.setVelocity(new Vector(RunningState.MIN_VELOCITY / 2.0, 0.0));
        DashState state = new DashState(knight);

        KnightState next = state.getNextState();

        assertInstanceOf(AfterDashState.class, next);
    }

    @Test
    void dashStateGetNextStateReturnsRespawnWhenSpikeCollision() {
        Knight knight = createKnightWithScene();
        when(knight.getScene().collideSpike()).thenReturn(true);
        DashState state = new DashState(knight);

        KnightState next = state.getNextState();

        assertInstanceOf(RespawnState.class, next);
    }

    @Test
    void afterDashStateGetNextStateReturnsRespawnWhenSpikeCollision() {
        Knight knight = createKnightWithScene();
        when(knight.getScene().collideSpike()).thenReturn(true);
        AfterDashState state = new AfterDashState(knight);

        KnightState next = state.getNextState();

        assertInstanceOf(RespawnState.class, next);
    }

    @Test
    void afterDashStateGetNextStateUsesGroundStateWhenOnGround() {
        Knight knight = createKnightOnGround();
        knight.setVelocity(new Vector(0.0, 0.0));
        AfterDashState state = new AfterDashState(knight);

        KnightState next = state.getNextState();

        assertInstanceOf(IdleState.class, next);
    }

    // ------------------------------------------------------------------
    // DamagedState
    // ------------------------------------------------------------------

    @Test
    void damagedStateStaysDamagedAtLeastForFirstStep() throws Exception {
        Knight knight = createKnightWithScene();
        KnightState state = new DamagedState(knight, 0);

        KnightState next = state.getNextState();

        assertInstanceOf(DamagedState.class, next);
    }

    @Test
    void damagedStateEventuallyTransitionsToNonDamagedState() throws Exception {
        Knight knight = createKnightWithScene();
        KnightState current = new DamagedState(knight, 0);

        for (int i = 0; i < 30; i++) {
            current = current.getNextState();
        }

        assertFalse(current instanceof DamagedState);
    }


    // ------------------------------------------------------------------
    // RespawnState
    // ------------------------------------------------------------------

    @Test
    void respawnStateGetNextStateStaysInRespawnBeforeTimerExpires() throws Exception {
        Knight knight = createKnightWithScene();
        RespawnState state = new RespawnState(knight, 100); // timer grande

        KnightState next = state.getNextState();

        assertInstanceOf(RespawnState.class, next);
    }

    @Test
    void respawnStateJumpDoesNotThrowAndReturnsVector() throws Exception {
        Knight knight = createKnightWithScene();
        RespawnState state = new RespawnState(knight, 100);

        Vector v = state.jump();

        assertNotNull(v);
    }

    @Test
    void respawnStateDashDoesNotThrowAndReturnsVector() throws Exception {
        Knight knight = createKnightWithScene();
        RespawnState state = new RespawnState(knight, 100);

        Vector v = state.dash();

        assertNotNull(v);
    }
}
