package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.knight.*;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KnightMutationTests {

    private Knight knightWithScene() {
        Knight k = new Knight(10, 20, 50, 2.0f, 100);
        Scene scene = mock(Scene.class);

        // default: no collisions
        when(scene.collidesDown(any(Position.class), any(Position.class))).thenReturn(false);
        when(scene.collidesUp(any(Position.class), any(Position.class))).thenReturn(false);
        when(scene.collidesLeft(any(Position.class), any(Position.class))).thenReturn(false);
        when(scene.collidesRight(any(Position.class), any(Position.class))).thenReturn(false);

        k.setScene(scene);
        return k;
    }

    // ----------------- Constructor invariants (kill constant/offset mutants) -----------------

    @Test
    void constructorAppliesPositionOffsetExactly() {
        Knight k = new Knight(10, 20, 50, 2.0f, 100);
        assertEquals(new Position(14, 21), k.getPosition()); // offSetX=4 offSetY=1
    }

    @Test
    void constructorInitializesCoreFields() {
        Knight k = new Knight(0, 0, 12, 3.5f, 77);
        assertAll(
                () -> assertEquals(12, k.getHP()),
                () -> assertEquals(3.5f, k.getDamage()),
                () -> assertEquals(77, k.getEnergy()),
                () -> assertTrue(k.isFacingRight()),
                () -> assertFalse(k.isGotHit()),
                () -> assertEquals(0, k.getOrbs())
        );
    }

    // ----------------- isOverMaxXVelocity (kill comparator flip mutants) -----------------

    @Test
    void isOverMaxXVelocityFalseAtBoundaryAndTrueAbove() {
        Knight k = knightWithScene();
        k.setVelocity(new Vector(2.0, 0.0));
        assertFalse(k.isOverMaxXVelocity()); // boundary

        k.setVelocity(new Vector(2.0001, 0.0));
        assertTrue(k.isOverMaxXVelocity());
    }

    // ----------------- updatePosition (kill removed add / sign flip mutants) -----------------

    @Test
    void updatePositionAddsResolvedVelocityToPosition() {
        Knight k = knightWithScene();

        // Use a state that overrides applyCollisions deterministically
        KnightState s = new KnightState(k) {
            @Override public Vector jump() { return new Vector(0,0); }
            @Override public Vector dash() { return new Vector(0,0); }
            @Override public Vector updateVelocity(Vector newVelocity) { return newVelocity; }
            @Override public KnightState getNextState() { return this; }
            @Override protected Vector applyCollisions(Vector velocity) { return new Vector(3.0, -2.0); }
        };
        k.setState(s);
        k.setVelocity(new Vector(999,999)); // ignored

        Position before = k.getPosition();
        Position after = k.updatePosition();

        assertEquals(new Position(before.x() + 3.0, before.y() - 2.0), after);
    }

    // ----------------- getNextState delegates (kill removed call mutants) -----------------

    @Test
    void getNextStateDelegatesToCurrentState() throws Exception {
        Knight k = knightWithScene();
        KnightState current = mock(KnightState.class);
        KnightState next = mock(KnightState.class);

        when(current.getNextState()).thenReturn(next);
        k.setState(current);

        assertSame(next, k.getNextState());
        verify(current, times(1)).getNextState();
    }

    // ----------------- movement delegates (kill wrong method called mutants) -----------------

    @Test
    void moveLeftDelegatesToStateAndReturnsItsVector() {
        Knight k = knightWithScene();
        KnightState st = mock(KnightState.class);
        Vector v = new Vector(-1.0, 0.0);
        when(st.moveKnightLeft()).thenReturn(v);
        k.setState(st);

        assertSame(v, k.moveLeft());
        verify(st, times(1)).moveKnightLeft();
    }

    @Test
    void moveRightDelegatesToStateAndReturnsItsVector() {
        Knight k = knightWithScene();
        KnightState st = mock(KnightState.class);
        Vector v = new Vector(1.0, 0.0);
        when(st.moveKnightRight()).thenReturn(v);
        k.setState(st);

        assertSame(v, k.moveRight());
        verify(st, times(1)).moveKnightRight();
    }

    @Test
    void jumpDelegatesToStateAndReturnsItsVector() {
        Knight k = knightWithScene();
        KnightState st = mock(KnightState.class);
        Vector v = new Vector(0.0, -5.0);
        when(st.jump()).thenReturn(v);
        k.setState(st);

        assertSame(v, k.jump());
        verify(st, times(1)).jump();
    }

    @Test
    void dashDelegatesToStateAndReturnsItsVector() {
        Knight k = knightWithScene();
        KnightState st = mock(KnightState.class);
        Vector v = new Vector(6.0, 0.0);
        when(st.dash()).thenReturn(v);
        k.setState(st);

        assertSame(v, k.dash());
        verify(st, times(1)).dash();
    }

    // ----------------- isOnGround (kill negation mutants) -----------------

    @Test
    void isOnGroundReturnsTrueWhenSceneCollidesDownTrue() {
        Knight k = knightWithScene();
        when(k.getScene().collidesDown(any(Position.class), any(Position.class))).thenReturn(true);

        assertTrue(k.isOnGround());
    }

    @Test
    void isOnGroundReturnsFalseWhenSceneCollidesDownFalse() {
        Knight k = knightWithScene();
        when(k.getScene().collidesDown(any(Position.class), any(Position.class))).thenReturn(false);

        assertFalse(k.isOnGround());
    }

    // ----------------- PlayerHit (kill early-return / boundary mutants) -----------------

    @Test
    void playerHitDoesNothingIfGotHitAlreadyTrue() {
        Knight k = knightWithScene();
        k.setGotHit(true);
        k.setHP(50);
        k.setState(new IdleState(k));

        k.PlayerHit(10);

        assertAll(
                () -> assertEquals(50, k.getHP()),
                () -> assertTrue(k.getState() instanceof IdleState) // no state change
        );
    }

    @Test
    void playerHitWhenHpZeroSetsHpToOneBeforeApplyingDamage() {
        Knight k = knightWithScene();
        k.setGotHit(false);
        k.setHP(0);

        k.PlayerHit(5);

        // 1 - 5 = -4, and gotHit becomes true
        assertAll(
                () -> assertEquals(-4, k.getHP()),
                () -> assertTrue(k.isGotHit()),
                () -> assertTrue(k.getState() instanceof DamagedState)
        );
    }

    @Test
    void playerHitReducesHpAndSetsDamagedState() {
        Knight k = knightWithScene();
        k.setGotHit(false);
        k.setHP(30);

        k.PlayerHit(10);

        assertAll(
                () -> assertEquals(20, k.getHP()),
                () -> assertTrue(k.isGotHit()),
                () -> assertTrue(k.getState() instanceof DamagedState)
        );
    }

    // ----------------- resetValues (kill wrong constant / removed assignment mutants) -----------------

    @Test
    void resetValuesForcesFacingRightAndFallingState() {
        Knight k = knightWithScene();
        k.setFacingRight(false);
        k.setState(new IdleState(k));

        k.resetValues();

        assertAll(
                () -> assertTrue(k.isFacingRight()),
                () -> assertTrue(k.getState() instanceof FallingState)
        );
    }

    // ----------------- orbs and deaths (kill ++ removed / wrong variable mutants) -----------------

    @Test
    void addOrbsIncrementsExactlyByOne() {
        Knight k = knightWithScene();
        k.setOrbs(7);

        k.addOrbs();

        assertEquals(8, k.getOrbs());
    }

    @Test
    void increaseDeathsIncrementsExactlyByOne() {
        Knight k = knightWithScene();

        k.increaseDeaths();

        assertEquals(1, k.getNumberOfDeaths());
    }
}
