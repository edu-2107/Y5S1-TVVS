package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.game.elements.Element;
import pt.feup.tvvs.soulknight.model.game.elements.Spike;
import pt.feup.tvvs.soulknight.model.game.elements.collectables.Collectables;
import pt.feup.tvvs.soulknight.model.game.elements.enemies.Enemies;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.game.elements.tile.Tile;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SceneWhiteBoxTests {

    private Scene newSmallScene() {
        // Keep it small but valid for Tile.SIZE indexing
        int w = Tile.SIZE * 4; // pixel width
        int h = Tile.SIZE * 4; // pixel height
        return new Scene(w, h, 7);
    }

    @Test
    void gettersAndSetters_basic() {
        Scene scene = newSmallScene();

        assertEquals(7, scene.getSceneID());
        assertEquals(Tile.SIZE * 4, scene.getWidth());
        assertEquals(Tile.SIZE * 4, scene.getHeight());

        Knight k = mock(Knight.class);
        scene.setPlayer(k);
        assertSame(k, scene.getPlayer());

        assertEquals(0.25, scene.getGravity(), 1e-9);

        Position start = new Position(1, 2);
        scene.setStartPosition(start);
        assertSame(start, scene.getStartPosition());

        Position end = new Position(10, 0);
        scene.setEndPosition(end);
        // isAtEndPosition tested below
    }

    @Test
    void collidesLeft_outsideScene_returnsTrue() {
        Scene scene = newSmallScene();

        // Map layer exists by default
        // Outside on the left: x < 0 triggers isOutSideScene => collision = true
        assertTrue(scene.collidesLeft(new Position(-1, 0), new Position(10, 10)));
    }

    @Test
    void collidesRight_insideEmptyMap_returnsFalse() {
        Scene scene = newSmallScene();
        // Empty map -> no collision
        assertFalse(scene.collidesRight(new Position(5, 5), new Position(10, 10)));
    }

    @Test
    void collidesDown_withBlockingElement_returnsTrue() {
        Scene scene = newSmallScene();

        // Put a non-null Element in the map at the tile cell we will query
        Element[][] map = new Element[scene.getHeight()][scene.getWidth()];
        int tileX = 1;
        int tileY = 2;
        map[tileY][tileX] = mock(Element.class);
        scene.setMap(map);

        // Choose a position whose "down" query hits tile (1,2)
        // collidesDown uses: x..x+size.x-1 and y+size.y-2 .. y+size.y-1
        // We'll make y+size.y-2 be inside tileY*SIZE
        double x = tileX * Tile.SIZE + 1;
        double y = tileY * Tile.SIZE - 8;      // so that y+size.y-2 lands in tileY
        Position pos = new Position(x, y);
        Position size = new Position(10, 10);

        assertTrue(scene.collidesDown(pos, size));
    }

    @Test
    void collidesUp_insideButNoBlock_returnsFalse() {
        Scene scene = newSmallScene();

        Element[][] map = new Element[scene.getHeight()][scene.getWidth()];
        scene.setMap(map);

        Position pos = new Position(Tile.SIZE + 2, Tile.SIZE + 2);
        Position size = new Position(10, 10);

        assertFalse(scene.collidesUp(pos, size));
    }

    @Test
    void collectOrbs_collectsAndAlsoNoOpWhenNone() {
        Scene scene = newSmallScene();

        Knight player = mock(Knight.class);
        scene.setPlayer(player);

        // Place player so it spans two tile cells in X and Y, to exercise both loops
        // tileX values: floor(x/SIZE) and floor((x+width-1)/SIZE)
        // tileY values: floor(y/SIZE) and floor((y+height-1)/SIZE)
        when(player.getPosition()).thenReturn(new Position(Tile.SIZE - 2, Tile.SIZE - 2));
        when(player.getWidth()).thenReturn((int) 6.0);
        when(player.getHeight()).thenReturn((int) 6.0);

        Collectables[][] orbs = new Collectables[scene.getHeight()][scene.getWidth()];

        int tX1 = (int) ((Tile.SIZE - 2) / Tile.SIZE);                 // 0
        int tX2 = (int) (((Tile.SIZE - 2) + 6.0 - 1) / Tile.SIZE);     // 1
        int tY1 = (int) ((Tile.SIZE - 2) / Tile.SIZE);                 // 0
        int tY2 = (int) (((Tile.SIZE - 2) + 6.0 - 1) / Tile.SIZE);     // 1

        Collectables c1 = mock(Collectables.class);
        Collectables c2 = mock(Collectables.class);

        orbs[tY1][tX1] = c1;
        orbs[tY2][tX2] = c2;

        // First call: collects both
        scene.collectOrbs(orbs);

        verify(c1, times(1)).benefit(player);
        verify(c2, times(1)).benefit(player);
        verify(player, times(2)).addOrbs();
        assertNull(orbs[tY1][tX1]);
        assertNull(orbs[tY2][tX2]);

        // Second call: nothing there -> no more interactions
        scene.collectOrbs(orbs);
        verifyNoMoreInteractions(c1, c2);
        verify(player, times(2)).addOrbs(); // unchanged
    }

    @Test
    void collideMonsters_hitsOnlyWhenColliding() {
        Scene scene = newSmallScene();

        Knight player = mock(Knight.class);
        scene.setPlayer(player);
        when(player.getPosition()).thenReturn(new Position(10, 10));
        when(player.getWidth()).thenReturn((int) 10.0);
        when(player.getHeight()).thenReturn((int) 10.0);

        Enemies colliding = mock(Enemies.class);
        when(colliding.getPosition()).thenReturn(new Position(15, 15)); // overlaps
        when(colliding.getSize()).thenReturn(new Position(10, 10));
        when(colliding.getDamage()).thenReturn(3);

        Enemies notColliding = mock(Enemies.class);
        when(notColliding.getPosition()).thenReturn(new Position(2000, 2000)); // far away
        when(notColliding.getSize()).thenReturn(new Position(10, 10));
        when(notColliding.getDamage()).thenReturn(999);

        scene.collideMonsters(List.of(colliding, notColliding));

        verify(player, times(1)).PlayerHit(3);
        verify(player, never()).PlayerHit(999);
    }

    @Test
    void collideSpike_outsideAndInsideBranches() {
        Scene scene = newSmallScene();

        Knight player = mock(Knight.class);
        scene.setPlayer(player);
        when(player.getWidth()).thenReturn((int) 10.0);
        when(player.getHeight()).thenReturn((int) 10.0);

        Spike[][] spikes = new Spike[scene.getHeight()][scene.getWidth()];
        scene.setSpikes(spikes);

        // Case A: outside scene -> checkCollision(...) returns true
        when(player.getPosition()).thenReturn(new Position(-5, 0));
        assertTrue(scene.collideSpike());

        // Case B: inside scene and spike cell non-null -> true via layer[tileY][tileX] != null
        int tileX = 1, tileY = 1;
        spikes[tileY][tileX] = mock(Spike.class);

        when(player.getPosition()).thenReturn(new Position(tileX * Tile.SIZE + 1, tileY * Tile.SIZE + 1));
        assertTrue(scene.collideSpike());

        // Case C: inside scene and no spike -> false
        spikes[tileY][tileX] = null;
        assertFalse(scene.collideSpike());
    }

    @Test
    void collideMonsters_coversAllShortCircuitBranchesOfCheckCollision() {
        Scene scene = newSmallScene();

        Knight player = mock(Knight.class);
        scene.setPlayer(player);

        // Fixed player AABB
        when(player.getPosition()).thenReturn(new Position(10, 10));
        when(player.getWidth()).thenReturn((int) 10.0);
        when(player.getHeight()).thenReturn((int) 10.0);

        // Enemy sizes (all same for simplicity)
        Position enemySize = new Position(10, 10);

        // 1) Fail 1st clause: playerX < enemyX + enemyW  (10 < 0+10 is false)
        Enemies eFail1 = mock(Enemies.class);
        when(eFail1.getPosition()).thenReturn(new Position(0, 10));
        when(eFail1.getSize()).thenReturn(enemySize);

        // 2) Pass 1st, fail 2nd: playerX + playerW > enemyX  (20 > 20 is false)
        Enemies eFail2 = mock(Enemies.class);
        when(eFail2.getPosition()).thenReturn(new Position(20, 10));
        when(eFail2.getSize()).thenReturn(enemySize);

        // 3) Pass first two, fail 3rd: playerY < enemyY + enemyH  (10 < 0+10 is false)
        Enemies eFail3 = mock(Enemies.class);
        when(eFail3.getPosition()).thenReturn(new Position(15, 0));
        when(eFail3.getSize()).thenReturn(enemySize);

        // 4) Pass first three, fail 4th: playerY + playerH > enemyY  (20 > 20 is false)
        Enemies eFail4 = mock(Enemies.class);
        when(eFail4.getPosition()).thenReturn(new Position(15, 20));
        when(eFail4.getSize()).thenReturn(enemySize);

        // 5) All clauses true => collision true
        Enemies eHit = mock(Enemies.class);
        when(eHit.getPosition()).thenReturn(new Position(15, 15));
        when(eHit.getSize()).thenReturn(enemySize);
        when(eHit.getDamage()).thenReturn(7);

        scene.collideMonsters(List.of(eFail1, eFail2, eFail3, eFail4, eHit));

        verify(player, times(1)).PlayerHit(7);

        verify(eFail1, never()).getDamage();
        verify(eFail2, never()).getDamage();
        verify(eFail3, never()).getDamage();
        verify(eFail4, never()).getDamage();
        verify(eHit, times(1)).getDamage();
    }
    @Test
    void isOutsideScene_allOrBranchesCovered_viaPublicCollisions() {
        Scene scene = newSmallScene();

        // 1) x1 < 0  (left outside)
        assertTrue(scene.collidesLeft(new Position(-1, Tile.SIZE), new Position(10, 10)));

        // 2) x2 >= width  (right outside)
        // collidesRight calls checkCollision(x + size.x - 1, x + size.x - 1, ...)
        double xNearRight = scene.getWidth() - 5;
        assertTrue(scene.collidesRight(new Position(xNearRight, Tile.SIZE), new Position(10, 10)));

        // 3) y1 < 0  (up outside)
        assertTrue(scene.collidesUp(new Position(Tile.SIZE, -1), new Position(10, 10)));

        // 4) y2 >= height (down outside)
        // collidesDown calls checkCollision(..., y + size.y - 2, y + size.y - 1)
        double yNearBottom = scene.getHeight() - 5;
        assertTrue(scene.collidesDown(new Position(Tile.SIZE, yNearBottom), new Position(10, 10)));
    }

    @Test
    void isAtEndPosition_trueAndFalse() {
        Scene scene = newSmallScene();

        Knight player = mock(Knight.class);
        scene.setPlayer(player);

        scene.setEndPosition(new Position(50, 0));

        when(player.getPosition()).thenReturn(new Position(49, 0));
        assertFalse(scene.isAtEndPosition());

        when(player.getPosition()).thenReturn(new Position(50, 0));
        assertTrue(scene.isAtEndPosition());

        when(player.getPosition()).thenReturn(new Position(999, 0));
        assertTrue(scene.isAtEndPosition());
    }

    @Test
    void particlesLists_settersAndGetters() {
        Scene scene = newSmallScene();

        assertNotNull(scene.getParticles());
        assertNotNull(scene.getJumpParticles());
        assertNotNull(scene.getDoubleJumpParticles());
        assertNotNull(scene.getRespawnParticles());
        assertNotNull(scene.getDashParticles());

        scene.setParticles(new ArrayList<>());
        scene.setJumpParticles(new ArrayList<>());
        scene.setDoubleJumpParticles(new ArrayList<>());
        scene.setRespawnParticles(new ArrayList<>());
        scene.setDashParticles(new ArrayList<>());

        assertNotNull(scene.getParticles());
        assertNotNull(scene.getJumpParticles());
        assertNotNull(scene.getDoubleJumpParticles());
        assertNotNull(scene.getRespawnParticles());
        assertNotNull(scene.getDashParticles());
    }
}
