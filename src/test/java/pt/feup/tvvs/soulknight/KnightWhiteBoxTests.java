package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;

import java.util.List;

import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.knight.DamagedState;
import pt.feup.tvvs.soulknight.model.game.elements.knight.FallingState;
import pt.feup.tvvs.soulknight.model.game.elements.knight.IdleState;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.game.elements.knight.KnightState;
import pt.feup.tvvs.soulknight.model.game.elements.particle.Particle;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class KnightWhiteBoxTests {

    private Knight createDefaultKnight() {
        // x=10, y=20, HP=50, Damage=2.0, Energy=100
        return new Knight(10, 20, 50, 2.0f, 100);
    }

    // ---------- CONSTRUCTOR / GETTERS ----------

    @Test
    void constructorSetsInitialHp() {
        Knight knight = createDefaultKnight();
        assertEquals(50, knight.getHP());
    }

    @Test
    void constructorSetsInitialDamageMultiplier() {
        Knight knight = createDefaultKnight();
        assertEquals(2.0f, knight.getDamage());
    }

    @Test
    void constructorSetsInitialEnergy() {
        Knight knight = createDefaultKnight();
        assertEquals(100, knight.getEnergy());
    }

    @Test
    void constructorOffsetsInitialPosition() {
        Knight knight = createDefaultKnight();

        // offSetX = 4, offSetY = 1
        Position position = knight.getPosition();
        assertEquals(new Position(10 + 4, 20 + 1), position);
    }

    @Test
    void constructorInitialStateIsIdle() {
        Knight knight = createDefaultKnight();
        assertInstanceOf(IdleState.class, knight.getState());
    }

    @Test
    void constructorInitialFacingDirectionIsRight() {
        Knight knight = createDefaultKnight();
        assertTrue(knight.isFacingRight());
    }

    @Test
    void constructorInitialGotHitIsFalse() {
        Knight knight = createDefaultKnight();
        assertFalse(knight.isGotHit());
    }

    // ---------- DEATHS / BIRTH TIME ----------

    @Test
    void increaseDeathsIncrementsDeathCounter() {
        Knight knight = createDefaultKnight();

        knight.increaseDeaths();

        assertEquals(1, knight.getNumberOfDeaths());
    }

    @Test
    void setBirthTimeOverridesInitialBirthTime() {
        Knight knight = createDefaultKnight();

        long customTime = 123456789L;
        knight.setBirthTime(customTime);

        assertEquals(customTime, knight.getBirthTime());
    }

    // ---------- ORBS ----------

    @Test
    void addOrbsIncrementsOrbsByOne() {
        Knight knight = createDefaultKnight();

        knight.addOrbs();

        assertEquals(1, knight.getOrbs());
    }

    @Test
    void setOrbsOverridesCurrentValue() {
        Knight knight = createDefaultKnight();

        knight.setOrbs(5);

        assertEquals(5, knight.getOrbs());
    }

    // ---------- VELOCITY / LIMITS ----------

    @Test
    void isOverMaxXVelocityReturnsFalseWhenBelowLimit() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(1.0, 0.0));   // maxVelocity.x() = 2.0

        assertFalse(knight.isOverMaxXVelocity());
    }

    @Test
    void isOverMaxXVelocityReturnsTrueWhenAboveLimit() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(3.0, 0.0));   // > maxVelocity.x()

        assertTrue(knight.isOverMaxXVelocity());
    }

    // ---------- RESET VALUES ----------

    @Test
    void resetValuesSetsFacingRightToTrue() {
        Knight knight = createDefaultKnight();
        knight.setFacingRight(false);

        knight.resetValues();

        assertTrue(knight.isFacingRight());
    }

    @Test
    void resetValuesChangesStateToFallingState() {
        Knight knight = createDefaultKnight();

        knight.resetValues();

        assertInstanceOf(FallingState.class, knight.getState());
    }

    // ---------- PLAYER HIT (BRANCHES) ----------

    @Test
    void playerHitDoesNothingWhenAlreadyGotHit() {
        Knight knight = createDefaultKnight();
        knight.setGotHit(true);
        knight.setHP(50);

        Scene scene = mock(Scene.class);
        knight.setScene(scene);

        knight.PlayerHit(10);

        assertEquals(50, knight.getHP());
    }

    @Test
    void playerHitWithPositiveHpReducesHpByDamage() {
        Knight knight = createDefaultKnight();
        knight.setGotHit(false);
        knight.setHP(50);

        Scene scene = mock(Scene.class);
        knight.setScene(scene);

        knight.PlayerHit(10);

        assertEquals(40, knight.getHP());
    }

    @Test
    void playerHitWithZeroHpSetsHpToOneBeforeApplyingDamage() {
        Knight knight = createDefaultKnight();
        knight.setGotHit(false);
        knight.setHP(0);

        Scene scene = mock(Scene.class);
        knight.setScene(scene);

        knight.PlayerHit(5);

        // HP is set to 1, then damage 5 is applied → 1 - 5 = -4
        assertEquals(-4, knight.getHP());
    }

    @Test
    void playerHitSetsGotHitToTrue() {
        Knight knight = createDefaultKnight();
        knight.setGotHit(false);

        Scene scene = mock(Scene.class);
        knight.setScene(scene);

        knight.PlayerHit(5);

        assertTrue(knight.isGotHit());
    }

    @Test
    void playerHitChangesStateToDamagedState() {
        Knight knight = createDefaultKnight();

        Scene scene = mock(Scene.class);
        knight.setScene(scene);

        knight.PlayerHit(5);

        assertInstanceOf(DamagedState.class, knight.getState());
    }

    // ---------- PARTICLE GENERATION ----------

    @Test
    void createParticlesDoubleJumpReturnsListOfGivenSize() {
        Knight knight = createDefaultKnight();

        List<Particle> particles = knight.createParticlesDoubleJump(15, null);

        assertEquals(15, particles.size());
    }

    @Test
    void createParticlesJumpReturnsListOfGivenSize() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(0.0, 0.0));

        List<Particle> particles = knight.createParticlesJump(15);

        assertEquals(15, particles.size());
    }

    @Test
    void createRespawnParticlesReturnsListOfGivenSize() {
        Knight knight = createDefaultKnight();

        List<Particle> particles = knight.createRespawnParticles(15);

        assertEquals(15, particles.size());
    }

    @Test
    void createDashParticlesReturnsListOfGivenSize() {
        Knight knight = createDefaultKnight();
        knight.setVelocity(new Vector(0.0, 0.0));

        List<Particle> particles = knight.createDashParticles(15);

        assertEquals(15, particles.size());
    }

    // ---------- DELEGAÇÃO NO STATE (updateVelocity, updatePosition, moves) ----------

    @Test
    void updateVelocityDelegatesToState() {
        Knight knight = createDefaultKnight();
        Vector currentVelocity = new Vector(1.0, 2.0);
        knight.setVelocity(currentVelocity);

        KnightState state = mock(KnightState.class);
        Vector expected = new Vector(3.0, 4.0);
        when(state.updateVelocity(currentVelocity)).thenReturn(expected);
        knight.setState(state);

        Vector result = knight.updateVelocity();

        assertEquals(expected, result);
    }

    @Test
    void updatePositionUsesStateApplyCollisionsAndUpdatesPosition() {
        Knight knight = createDefaultKnight();

        Vector resolvedVelocity = new Vector(2.0, -1.0);

        KnightState fakeState = new TestKnightState(knight, resolvedVelocity);
        knight.setState(fakeState);

        knight.setVelocity(new Vector(999.0, 999.0));

        Position oldPos = knight.getPosition();
        Position newPos = knight.updatePosition();

        assertEquals(
                new Position(oldPos.x() + resolvedVelocity.x(),
                        oldPos.y() + resolvedVelocity.y()),
                newPos
        );
    }


    @Test
    void moveLeftDelegatesToState() {
        Knight knight = createDefaultKnight();
        KnightState state = mock(KnightState.class);
        Vector expected = new Vector(-1.0, 0.0);
        when(state.moveKnightLeft()).thenReturn(expected);
        knight.setState(state);

        Vector result = knight.moveLeft();

        assertEquals(expected, result);
    }

    @Test
    void moveRightDelegatesToState() {
        Knight knight = createDefaultKnight();
        KnightState state = mock(KnightState.class);
        Vector expected = new Vector(1.0, 0.0);
        when(state.moveKnightRight()).thenReturn(expected);
        knight.setState(state);

        Vector result = knight.moveRight();

        assertEquals(expected, result);
    }

    @Test
    void jumpDelegatesToState() {
        Knight knight = createDefaultKnight();
        KnightState state = mock(KnightState.class);
        Vector expected = new Vector(0.0, -5.0);
        when(state.jump()).thenReturn(expected);
        knight.setState(state);

        Vector result = knight.jump();

        assertEquals(expected, result);
    }

    @Test
    void dashDelegatesToState() {
        Knight knight = createDefaultKnight();
        KnightState state = mock(KnightState.class);
        Vector expected = new Vector(5.0, 0.0);
        when(state.dash()).thenReturn(expected);
        knight.setState(state);

        Vector result = knight.dash();

        assertEquals(expected, result);
    }

    @Test
    void getNextStateDelegatesToCurrentState() throws Exception {
        Knight knight = createDefaultKnight();
        KnightState currentState = mock(KnightState.class);
        KnightState nextState = mock(KnightState.class);
        when(currentState.getNextState()).thenReturn(nextState);
        knight.setState(currentState);

        KnightState result = knight.getNextState();

        assertEquals(nextState, result);
    }

    // ---------- isOnGround (colisão com Scene) ----------

    @Test
    void isOnGroundReturnsTrueWhenSceneCollidesDownIsTrue() {
        Knight knight = createDefaultKnight();
        Scene scene = mock(Scene.class);
        when(scene.collidesDown(any(Position.class), any(Position.class)))
                .thenReturn(true);
        knight.setScene(scene);

        boolean result = knight.isOnGround();

        assertTrue(result);
    }

    @Test
    void isOnGroundReturnsFalseWhenSceneCollidesDownIsFalse() {
        Knight knight = createDefaultKnight();
        Scene scene = mock(Scene.class);
        when(scene.collidesDown(any(Position.class), any(Position.class)))
                .thenReturn(false);
        knight.setScene(scene);

        boolean result = knight.isOnGround();

        assertFalse(result);
    }

    // ---------- GETTERS / SETTERS ----------

    @Test
    void getJumpBoostReturnsInitialConfiguredValue() {
        Knight knight = createDefaultKnight();

        assertEquals(Math.PI, knight.getJumpBoost());
    }

    @Test
    void getAccelerationReturnsInitialConfiguredValue() {
        Knight knight = createDefaultKnight();

        assertEquals(0.75, knight.getAcceleration());
    }

    @Test
    void getDashBoostReturnsInitialConfiguredValue() {
        Knight knight = createDefaultKnight();

        assertEquals(6.0, knight.getDashBoost());
    }


    @Test
    void isFacingRightGetterReflectsSetter() {
        Knight knight = createDefaultKnight();
        knight.setFacingRight(false);

        assertFalse(knight.isFacingRight());
    }

    @Test
    void setDamageUpdatesDamageMultiplier() {
        Knight knight = createDefaultKnight();

        knight.setDamage(3.5f);

        assertEquals(3.5f, knight.getDamage());
    }

    @Test
    void setEnergyUpdatesEnergy() {
        Knight knight = createDefaultKnight();

        knight.setEnergy(42);

        assertEquals(42, knight.getEnergy());
    }

    @Test
    void setJumpCounterUpdatesJumpCounter() {
        Knight knight = createDefaultKnight();

        knight.setJumpCounter(3);

        assertEquals(3, knight.getJumpCounter());
    }

    @Test
    void setMaxVelocityUpdatesMaxVelocity() {
        Knight knight = createDefaultKnight();
        Vector newMax = new Vector(10.0, 20.0);

        knight.setMaxVelocity(newMax);

        assertEquals(newMax, knight.getMaxVelocity());
    }


    private static class TestKnightState extends KnightState {

        private final Vector collisionResult;

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
            return new Vector(0, 0);
        }

        @Override
        public Vector dash() {
            return new Vector(0, 0);
        }

        @Override
        public Vector updateVelocity(Vector newVelocity) {
            return newVelocity;
        }

        @Override
        public KnightState getNextState() {
            return this;
        }
    }
}
