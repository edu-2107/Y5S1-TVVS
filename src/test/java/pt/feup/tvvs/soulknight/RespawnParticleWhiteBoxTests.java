package pt.feup.tvvs.soulknight;

import com.googlecode.lanterna.TextColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.particle.RespawnParticle;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RespawnParticleWhiteBoxTests {

    private Scene scene;

    static class TestableRespawnParticle extends RespawnParticle {
        private Scene scene;

        public TestableRespawnParticle(int x, int y, Position velocity, TextColor.RGB color) {
            super(x, y, velocity, color);
        }

        public void setScene(Scene scene) {
            this.scene = scene;
        }

        @Override
        public Scene getScene() {
            return scene;
        }

        public Vector exposeApplyCollisions(Vector v) {
            return super.applyCollisions(v);
        }
    }

    @BeforeEach
    void setUp() {
        scene = mock(Scene.class);

        when(scene.collidesUp(any(), any())).thenReturn(false);
        when(scene.collidesDown(any(), any())).thenReturn(false);
        when(scene.collidesLeft(any(), any())).thenReturn(false);
        when(scene.collidesRight(any(), any())).thenReturn(false);
    }

    @Test
    public void applyCollisions_ceilingCollision_bouncesVy() {
        TestableRespawnParticle p = new TestableRespawnParticle(0, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));
        p.setScene(scene);

        when(scene.collidesUp(any(), any())).thenReturn(true);

        Vector out = p.exposeApplyCollisions(new Vector(0.0, -0.5));
        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(0.5, out.y(), 1e-9);
    }

    @Test
    public void applyCollisions_downwardCollision_whileLoopRuns_andStopsWithSmallVy_setsZero() {
        TestableRespawnParticle p = new TestableRespawnParticle(0, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));
        p.setScene(scene);

        when(scene.collidesDown(any(), any())).thenReturn(true);

        Vector out = p.exposeApplyCollisions(new Vector(0.0, 0.5));
        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);
    }

    @Test
    public void applyCollisions_downwardCollision_whileLoopRuns_thenStopsWithoutForcingZero() {
        TestableRespawnParticle p = new TestableRespawnParticle(0, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));
        p.setScene(scene);

        when(scene.collidesDown(any(), any())).thenReturn(true, true, false);

        Vector out = p.exposeApplyCollisions(new Vector(0.0, 0.5));
        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(0.3, out.y(), 1e-9);
    }

    @Test
    public void applyCollisions_horizontalLeftCollision_setsVxZero() {
        TestableRespawnParticle p = new TestableRespawnParticle(0, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));
        p.setScene(scene);

        when(scene.collidesLeft(any(), any())).thenReturn(true);

        Vector out = p.exposeApplyCollisions(new Vector(-0.2, 0.0));
        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);
    }

    @Test
    public void applyCollisions_horizontalRightCollision_setsVxZero() {
        TestableRespawnParticle p = new TestableRespawnParticle(0, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));
        p.setScene(scene);

        when(scene.collidesRight(any(), any())).thenReturn(true);

        Vector out = p.exposeApplyCollisions(new Vector(0.2, 0.0));
        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);
    }

    @Test
    public void applyCollisions_noHorizontalCollision_keepsVx() {
        TestableRespawnParticle p = new TestableRespawnParticle(0, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));
        p.setScene(scene);

        when(scene.collidesLeft(any(), any())).thenReturn(false);

        Vector out = p.exposeApplyCollisions(new Vector(-0.2, 0.0));
        assertEquals(-0.2, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);
    }

    @Test
    public void moveParticle_firstCall_sticksBranch_setsVyZero_andSecondCall_gravityBranch() {
        TestableRespawnParticle p = new TestableRespawnParticle(0, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));
        p.setScene(scene);

        double opacityBefore = p.getOpacity();

        Position pos1 = p.moveParticle(scene, 0);
        assertEquals(0.0, pos1.x(), 1e-9);
        assertEquals(0.0, pos1.y(), 1e-9);
        assertEquals(0.0, p.getVelocity().y(), 1e-9);

        double opacityAfter1 = p.getOpacity();
        assertTrue(opacityAfter1 <= opacityBefore + 1e-9);
        assertTrue(opacityAfter1 >= 0.0);

        Position pos2 = p.moveParticle(scene, 0);
        assertEquals(0.0, pos2.x(), 1e-9);
        assertEquals(0.2, pos2.y(), 1e-9);
        assertEquals(0.2, p.getVelocity().y(), 1e-9);
    }

    @Test
    public void moveParticle_afterStick_gravityThenCeilingCollisionBranch_isCovered() {
        TestableRespawnParticle p = new TestableRespawnParticle(0, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));
        p.setScene(scene);

        p.moveParticle(scene, 0);

        p.setVelocity(new Position(0, -0.5));
        when(scene.collidesUp(any(), any())).thenReturn(true);

        p.moveParticle(scene, 0);

        assertEquals(0.3, p.getVelocity().y(), 1e-9);
    }

    @Test
    public void moveParticle_afterStick_gravityThenDownwardCollisionBranch_isCovered() {
        TestableRespawnParticle p = new TestableRespawnParticle(0, 0, new Position(0, 0), new TextColor.RGB(255, 255, 255));
        p.setScene(scene);

        p.moveParticle(scene, 0);

        when(scene.collidesDown(any(), any())).thenReturn(true);

        p.moveParticle(scene, 0);

        assertEquals(0.0, p.getVelocity().y(), 1e-9);
    }

    @Test
    public void applyCollisions_upwardNoCeilingCollision_keepsVyNegative() {
        TestableRespawnParticle p = new TestableRespawnParticle(
                0, 0, new Position(0, 0),
                new TextColor.RGB(255, 255, 255)
        );
        p.setScene(scene);

        when(scene.collidesUp(any(), any())).thenReturn(false);

        Vector out = p.exposeApplyCollisions(new Vector(0.0, -0.5));
        assertEquals(0.0, out.x(), 1e-9);
        assertEquals(-0.5, out.y(), 1e-9);
    }

    @Test
    public void applyCollisions_noRightCollision_withPositiveVx_keepsVx() {
        TestableRespawnParticle p = new TestableRespawnParticle(
                0, 0, new Position(0, 0),
                new TextColor.RGB(255, 255, 255)
        );
        p.setScene(scene);

        when(scene.collidesRight(any(), any())).thenReturn(false);

        Vector out = p.exposeApplyCollisions(new Vector(0.2, 0.0));
        assertEquals(0.2, out.x(), 1e-9);
        assertEquals(0.0, out.y(), 1e-9);
    }

}
