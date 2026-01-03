package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.enemies.SwordMonster;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SwordMonsterWhiteBoxTests {

    static class TestableSwordMonster extends SwordMonster {
        TestableSwordMonster(int x, int y, int HP, Scene scene, int damage, Position size, char symbol) {
            super(x, y, HP, scene, damage, size, symbol);
        }

        Vector exposeApplyCollisions(Vector v) {
            return super.applyCollisions(v);
        }
    }

    @Test
    void getChar_returnsSymbol() {
        Scene scene = mock(Scene.class);
        TestableSwordMonster m = new TestableSwordMonster(0, 0, 10, scene, 1, new Position(1, 1), 'S');
        assertEquals('S', m.getChar());
    }

    @Test
    void updatePosition_noCollisions_movesByVelocity() {
        Scene scene = mock(Scene.class);
        when(scene.collidesLeft(any(), any())).thenReturn(false);
        when(scene.collidesRight(any(), any())).thenReturn(false);

        TestableSwordMonster m = new TestableSwordMonster(10, 20, 10, scene, 1, new Position(1, 1), 'S');

        Position p = m.updatePosition();
        assertEquals(11.0, p.x(), 1e-9);
        assertEquals(20.0, p.y(), 1e-9);
    }

    @Test
    void moveMonster_delegatesToUpdatePosition() {
        Scene scene = mock(Scene.class);
        when(scene.collidesLeft(any(), any())).thenReturn(false);
        when(scene.collidesRight(any(), any())).thenReturn(false);

        TestableSwordMonster m = new TestableSwordMonster(10, 20, 10, scene, 1, new Position(1, 1), 'S');

        Position p = m.moveMonster();
        assertEquals(11.0, p.x(), 1e-9);
        assertEquals(20.0, p.y(), 1e-9);
    }

    @Test
    void applyCollisions_whenMovingRight_andCollidesRight_setsVxTo0_andFlipsVelocity() {
        Scene scene = mock(Scene.class);
        when(scene.collidesRight(any(), any())).thenReturn(true);
        when(scene.collidesLeft(any(), any())).thenReturn(false);

        TestableSwordMonster m = spy(new TestableSwordMonster(10, 20, 10, scene, 1, new Position(1, 1), 'S'));

        Vector in = new Vector(1.0, 0.0);
        Vector out = m.exposeApplyCollisions(in);

        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);

        verify(m).setVelocity(new Vector(-1.0, 0.0));
    }

    @Test
    void applyCollisions_whenMovingLeft_andCollidesLeft_setsVxTo0_andFlipsVelocity() {
        Scene scene = mock(Scene.class);
        when(scene.collidesLeft(any(), any())).thenReturn(true);
        when(scene.collidesRight(any(), any())).thenReturn(false);

        TestableSwordMonster m = spy(new TestableSwordMonster(10, 20, 10, scene, 1, new Position(1, 1), 'S'));

        Vector in = new Vector(-1.0, 0.0);
        Vector out = m.exposeApplyCollisions(in);

        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);

        verify(m).setVelocity(new Vector(1.0, 0.0));
    }

    @Test
    void updatePosition_withRightCollision_usesResolvedVelocity_andAlsoFlipsInternalVelocity() {
        Scene scene = mock(Scene.class);
        when(scene.collidesRight(any(), any())).thenReturn(true);
        when(scene.collidesLeft(any(), any())).thenReturn(false);

        TestableSwordMonster m = spy(new TestableSwordMonster(10, 20, 10, scene, 1, new Position(1, 1), 'S'));

        Position p = m.updatePosition();

        assertEquals(10.0, p.x(), 1e-9);
        assertEquals(20.0, p.y(), 1e-9);

        verify(m).setVelocity(new Vector(-1.0, 0.0));
    }
}
