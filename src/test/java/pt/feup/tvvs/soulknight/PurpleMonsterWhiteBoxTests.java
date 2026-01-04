package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.enemies.PurpleMonster;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PurpleMonsterWhiteBoxTests {

    static class TestablePurpleMonster extends PurpleMonster {
        TestablePurpleMonster(int x, int y, int HP, Scene scene, int damage, Position size, char symbol) {
            super(x, y, HP, scene, damage, size, symbol);
        }

        Vector exposeApplyCollisions(Vector v) {
            return super.applyCollisions(v);
        }
    }

    @Test
    public void getChar_returnsSymbol() {
        Scene scene = mock(Scene.class);
        TestablePurpleMonster m = new TestablePurpleMonster(
                0, 0, 10, scene, 1, new Position(1, 1), 'P'
        );
        assertEquals('P', m.getChar());
    }

    @Test
    public void updatePosition_noCollisions_movesByVelocity() {
        Scene scene = mock(Scene.class);
        when(scene.collidesLeft(any(), any())).thenReturn(false);
        when(scene.collidesRight(any(), any())).thenReturn(false);

        TestablePurpleMonster m = new TestablePurpleMonster(
                10, 20, 10, scene, 1, new Position(1, 1), 'P'
        );

        Position p = m.updatePosition();
        assertEquals(8.5, p.x(), 1e-9);
        assertEquals(20.0, p.y(), 1e-9);
    }

    @Test
    public void moveMonster_delegatesToUpdatePosition() {
        Scene scene = mock(Scene.class);
        when(scene.collidesLeft(any(), any())).thenReturn(false);
        when(scene.collidesRight(any(), any())).thenReturn(false);

        TestablePurpleMonster m = new TestablePurpleMonster(
                10, 20, 10, scene, 1, new Position(1, 1), 'P'
        );

        Position p = m.moveMonster(); // deve ser igual a updatePosition
        assertEquals(8.5, p.x(), 1e-9);
        assertEquals(20.0, p.y(), 1e-9);
    }

    @Test
    public void applyCollisions_whenMovingLeft_andCollidesLeft_reducesVx_andFlipsVelocity() {
        Scene scene = mock(Scene.class);
        when(scene.collidesLeft(any(), any())).thenReturn(true);
        when(scene.collidesRight(any(), any())).thenReturn(false);

        TestablePurpleMonster m = spy(new TestablePurpleMonster(
                10, 20, 10, scene, 1, new Position(1, 1), 'P'
        ));

        Vector in = new Vector(-1.5, 0.0);
        Vector out = m.exposeApplyCollisions(in);

        // vx = min(vx+1,0) => min(-0.5,0) = -0.5
        assertEquals(-0.5, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);

        // setVelocity(new Vector(-velocity.x(),0)) => -(-1.5)= +1.5
        verify(m).setVelocity(new Vector(1.5, 0.0));
    }

    @Test
    public void applyCollisions_whenMovingRight_andCollidesRight_setsVxTo0_andFlipsVelocity() {
        Scene scene = mock(Scene.class);
        when(scene.collidesLeft(any(), any())).thenReturn(false);
        when(scene.collidesRight(any(), any())).thenReturn(true);

        TestablePurpleMonster m = spy(new TestablePurpleMonster(
                10, 20, 10, scene, 1, new Position(1, 1), 'P'
        ));

        Vector in = new Vector(1.5, 0.0);
        Vector out = m.exposeApplyCollisions(in);

        // vx = min(vx-1,0) => min(0.5,0)=0
        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);

        // setVelocity(new Vector(-velocity.x(),0)) => -(1.5)= -1.5
        verify(m).setVelocity(new Vector(-1.5, 0.0));
    }

    @Test
    public void updatePosition_withLeftCollision_usesResolvedVelocity_andAlsoFlipsInternalVelocity() {
        Scene scene = mock(Scene.class);
        when(scene.collidesLeft(any(), any())).thenReturn(true);
        when(scene.collidesRight(any(), any())).thenReturn(false);

        TestablePurpleMonster m = spy(new TestablePurpleMonster(
                10, 20, 10, scene, 1, new Position(1, 1), 'P'
        ));

        Position p = m.updatePosition();
        assertEquals(9.5, p.x(), 1e-9);
        assertEquals(20.0, p.y(), 1e-9);

        verify(m).setVelocity(new Vector(1.5, 0.0));
    }
}
