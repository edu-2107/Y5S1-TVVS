package pt.feup.tvvs.soulknight;

import com.googlecode.lanterna.TextColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.particle.RainParticle;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RainParticleWhiteBoxTests {

    private Scene scene;

    // Helper para cobrir o ramo currentPos == null
    static class NullPosRainParticle extends RainParticle {
        public NullPosRainParticle(int x, int y, Position velocity, TextColor.RGB color) {
            super(x, y, velocity, color);
        }

        @Override
        public Position getPosition() {
            return null;
        }
    }

    @BeforeEach
    void setUp() {
        scene = mock(Scene.class);
        when(scene.getWidth()).thenReturn(10);
        when(scene.getHeight()).thenReturn(10);
    }

    @Test
    public void moveParticle_whenPositionNull_throwsIllegalStateException() {
        RainParticle p = new NullPosRainParticle(0, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));
        assertThrows(IllegalStateException.class, () -> p.moveParticle(scene, 0));
    }

    @Test
    public void moveParticle_whenNewXNegative_wrapsToWidthMinus1() {
        RainParticle p = new RainParticle(-10, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));

        Position out = p.moveParticle(scene, 0);

        assertEquals(9.0, out.x(), 1e-9); // width - 1
        assertEquals(2.0, out.y(), 1e-9); // y + 2 (não faz reset)
    }

    @Test
    public void moveParticle_whenNewXTooLarge_setsTo1() {
        RainParticle p = new RainParticle(10, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));

        Position out = p.moveParticle(scene, 0);

        assertEquals(1.0, out.x(), 1e-9);
        assertEquals(2.0, out.y(), 1e-9);
    }

    @Test
    public void moveParticle_whenNewXInBounds_keepsComputedX_andYInBounds() {
        RainParticle p = new RainParticle(5, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));

        Position out = p.moveParticle(scene, 0);

        assertTrue(out.x() >= 0 && out.x() < 10, "x should be within bounds");
        assertEquals(2.0, out.y(), 1e-9);
    }

    @Test
    public void moveParticle_whenNewYReachesBottom_resetsTo0() {

        RainParticle p = new RainParticle(0, 8, new Position(0, 0), new TextColor.RGB(255, 255, 255));

        Position out = p.moveParticle(scene, 0);

        assertTrue(out.x() >= 0 && out.x() < 10, "x should be within bounds");
        assertEquals(0.0, out.y(), 1e-9);
    }
}
