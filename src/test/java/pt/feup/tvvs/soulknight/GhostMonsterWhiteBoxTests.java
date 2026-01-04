package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.enemies.GhostMonster;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GhostMonsterWhiteBoxTests {

    private GhostMonster makeMonster(Scene scene, double x, double y) {
        GhostMonster g = new GhostMonster(
                0, 0, 10, scene, 1, new Position(1, 1), 'G'
        );
        g.setPosition(new Position(x, y));

        g.setAmplitude(0);
        g.setFrequency(1.0);
        g.setHorizontalSpeed(0);
        return g;
    }

    @Test
    public void getChar_returnsSymbol() {
        Scene scene = mock(Scene.class);
        when(scene.getWidth()).thenReturn(100);
        when(scene.getHeight()).thenReturn(50);

        GhostMonster g = makeMonster(scene, 0, 0);
        assertEquals('G', g.getChar());
    }

    @Test
    public void setters_setAmplitudeFrequencyHorizontalSpeed_coverLines() {
        Scene scene = mock(Scene.class);
        when(scene.getWidth()).thenReturn(100);
        when(scene.getHeight()).thenReturn(50);

        GhostMonster g = makeMonster(scene, 0, 0);

        g.setAmplitude(3);
        g.setFrequency(0.09);
        g.setHorizontalSpeed(2);

        Position p = g.updatePosition();
        assertNotNull(p);
    }

    @Test
    public void applyCollisions_returnsSameVector() {
        Scene scene = mock(Scene.class);
        when(scene.getWidth()).thenReturn(100);
        when(scene.getHeight()).thenReturn(50);

        GhostMonster g = makeMonster(scene, 0, 0);

        Vector v = new Vector(1.2, -3.4);
        class Exposed extends GhostMonster {
            Exposed() { super(0,0,10,scene,1,new Position(1,1),'G'); }
            Vector expose(Vector vv){ return super.applyCollisions(vv); }
        }
        Exposed e = new Exposed();
        assertEquals(v, e.expose(v));
    }

    @Test
    public void updatePosition_wrapsRightToLeft_whenNewXGreaterThanScreenWidth() {
        Scene scene = mock(Scene.class);
        when(scene.getWidth()).thenReturn(100);
        when(scene.getHeight()).thenReturn(50);

        GhostMonster g = makeMonster(scene, 99, 10);
        g.setHorizontalSpeed(5);

        Position p = g.updatePosition();
        assertEquals(0.0, p.x(), 1e-9);
        assertEquals(10.0, p.y(), 1e-9);
    }

    @Test
    public void updatePosition_wrapsLeftToRight_whenNewXLessThanZero() {
        Scene scene = mock(Scene.class);
        when(scene.getWidth()).thenReturn(100);
        when(scene.getHeight()).thenReturn(50);

        GhostMonster g = makeMonster(scene, -1, 10);
        g.setHorizontalSpeed(0);

        Position p = g.updatePosition();
        assertEquals(100.0, p.x(), 1e-9);
        assertEquals(10.0, p.y(), 1e-9);
    }

    @Test
    public void updatePosition_wrapsBottomToTop_whenNewYGreaterThanScreenHeight() {
        Scene scene = mock(Scene.class);
        when(scene.getWidth()).thenReturn(100);
        when(scene.getHeight()).thenReturn(50);

        GhostMonster g = makeMonster(scene, 10, 60);

        Position p = g.updatePosition();
        assertEquals(10.0, p.x(), 1e-9);
        assertEquals(0.0, p.y(), 1e-9);
    }

    @Test
    public void updatePosition_wrapsTopToBottom_whenNewYLessThanZero() {
        Scene scene = mock(Scene.class);
        when(scene.getWidth()).thenReturn(100);
        when(scene.getHeight()).thenReturn(50);

        GhostMonster g = makeMonster(scene, 10, -2);

        Position p = g.updatePosition();
        assertEquals(10.0, p.x(), 1e-9);
        assertEquals(50.0, p.y(), 1e-9);
    }

    @Test
    public void updatePosition_normalMovement_noWrapping_changesX_andYUsingSine() {
        Scene scene = mock(Scene.class);
        when(scene.getWidth()).thenReturn(100);
        when(scene.getHeight()).thenReturn(50);

        GhostMonster g = makeMonster(scene, 10, 10);
        g.setHorizontalSpeed(2);
        g.setAmplitude(2);
        g.setFrequency(1.0);


        Position p = g.updatePosition();

        assertEquals(12.0, p.x(), 1e-9);
        assertEquals(10.0 + 2.0 * Math.sin(1.0), p.y(), 1e-9);
    }

    @Test
    public void moveMonster_setsPosition_andReturnsSamePosition() {
        Scene scene = mock(Scene.class);
        when(scene.getWidth()).thenReturn(100);
        when(scene.getHeight()).thenReturn(50);

        GhostMonster g = makeMonster(scene, 10, 10);
        g.setHorizontalSpeed(3);

        Position p = g.moveMonster();
        assertEquals(p, g.getPosition());
    }
}
