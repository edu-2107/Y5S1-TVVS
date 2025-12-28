package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.game.elements.Element;
import pt.feup.tvvs.soulknight.model.game.elements.Spike;
import pt.feup.tvvs.soulknight.model.game.elements.Tree;
import pt.feup.tvvs.soulknight.model.game.elements.collectables.Collectables;
import pt.feup.tvvs.soulknight.model.game.elements.enemies.Enemies;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.game.elements.rocks.Rock;
import pt.feup.tvvs.soulknight.model.game.elements.tile.Tile;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;
import pt.feup.tvvs.soulknight.model.game.scene.SceneLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SceneLoaderWhiteBoxTests {

    /**
     * Exposes protected getWidth/getHeight from SceneLoader (tests are in a different package).
     * Also allows replacing the content of the private 'lines' list via reflection to force branches.
     */
    static class ExposedSceneLoader extends SceneLoader {
        ExposedSceneLoader(int id) throws IOException { super(id); }

        public int exposedWidth() { return super.getWidth(); }
        public int exposedHeight() { return super.getHeight(); }

        @SuppressWarnings("unchecked")
        public void replaceLines(List<String> newLines) {
            try {
                Field f = SceneLoader.class.getDeclaredField("lines");
                f.setAccessible(true);
                List<String> lines = (List<String>) f.get(this);
                lines.clear();
                lines.addAll(newLines);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Knight mockKnightWithMutablePosition() {
        Knight knight = mock(Knight.class);

        AtomicReference<Position> posRef = new AtomicReference<>(new Position(0, 0));
        doAnswer(inv -> {
            posRef.set(inv.getArgument(0));
            return null;
        }).when(knight).setPosition(any(Position.class));

        when(knight.getPosition()).thenAnswer(inv -> posRef.get());

        // SceneLoader uses setScene(scene) on the knight
        doNothing().when(knight).setScene(any(Scene.class));

        return knight;
    }

    @Test
    void constructor_missingResource_throwsFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> new SceneLoader(999999));
    }

    @Test
    void getWidthAndHeight_viaSubclassWrapper_andWidthMaxBranch() throws IOException {
        ExposedSceneLoader loader = new ExposedSceneLoader(1);

        // Force width to update and not update across iterations:
        // width: starts 0 -> max with "12345" => 5, then max with "12" stays 5, then max with "" stays 5
        loader.replaceLines(List.of("12345", "12", ""));

        assertEquals(5, loader.exposedWidth());
        assertEquals(3, loader.exposedHeight());
    }

    @Test
    void createScene_customLines_coversMapWallsSpikesTreesRocksEndAndPlayer() throws IOException {
        ExposedSceneLoader loader = new ExposedSceneLoader(1);

        /*
          Build a tiny custom level:
          Row0: "P xMGLu^*tTRr."
            P -> player found
            space -> spike branch (isSpaceChar) => null
            x/M/G/L -> map+walls tiles branch
            u -> end position branch
            ^ -> spike branch create (not letter/digit, not space, not '*')
            * -> spike branch excluded => null
            t/T -> trees
            R/r -> rocks
            . -> default/else branches
         */
        loader.replaceLines(List.of(
                "P xMGLu^*tTRr.",
                ".....",
                "....."
        ));

        Knight knight = mockKnightWithMutablePosition();

        Scene scene = loader.createScene(knight);

        assertSame(knight, scene.getPlayer());

        // startPosition should equal player's position after createPlayer()
        assertNotNull(scene.getStartPosition());
        assertEquals(scene.getPlayer().getPosition().x(), scene.getStartPosition().x(), 1e-9);
        assertEquals(scene.getPlayer().getPosition().y(), scene.getStartPosition().y(), 1e-9);

        // Map/Walls: x/M/G/L become tiles, '.' becomes null, 'u' sets end position (no tile there)
        assertNotNull(scene.getTiles());
        assertNotNull(scene.getSpikes());
        assertNotNull(scene.getTrees());
        assertNotNull(scene.getRocks());
        assertNotNull(scene.getOrbs());
        assertNotNull(scene.getMonsters());
        assertNotNull(scene.getParticles());

        // Player at (0, -2) because y=0 and loader sets y*TILE_SIZE - 2
        assertEquals(0, scene.getPlayer().getPosition().x(), 1e-9);
        assertEquals(-2, scene.getPlayer().getPosition().y(), 1e-9);

        // 'x' at index 2 in row0 => tile should exist in walls/tiles
        assertNotNull(scene.getTiles()[0][2]);

        // 'u' at index 6 => end position set (x=6*TILE_SIZE)
        // Scene exposes only isAtEndPosition, so just sanity-check it doesn't crash:
        // (we can't read EndPosition directly)
        when(knight.getPosition()).thenReturn(new Position(0, 0));
        assertDoesNotThrow(scene::isAtEndPosition);

        // Spikes: '^' at index 7 must produce Spike; '*' at index 8 must be null
        assertNotNull(scene.getSpikes()[0][7]);
        assertNull(scene.getSpikes()[0][8]);

        // Trees: 't' at 9 and 'T' at 10
        assertNotNull(scene.getTrees()[0][9]);
        assertNotNull(scene.getTrees()[0][10]);

        // Rocks: 'R' at 11 and 'r' at 12
        assertNotNull(scene.getRocks()[0][11]);
        assertNotNull(scene.getRocks()[0][12]);
    }

    @Test
    void createScene_playerNotFound_throwsIllegalState() throws IOException {
        ExposedSceneLoader loader = new ExposedSceneLoader(1);
        loader.replaceLines(List.of(
                "xxxxx",
                "....."
        ));

        Knight knight = mockKnightWithMutablePosition();

        assertThrows(IllegalStateException.class, () -> loader.createScene(knight));
    }

    @Test
    void setOrbs_setsNewMatrix_andCoversMethod() throws IOException {
        ExposedSceneLoader loader = new ExposedSceneLoader(1);
        loader.replaceLines(List.of(
                "P....",
                "....."
        ));

        Knight knight = mockKnightWithMutablePosition();
        Scene scene = loader.createScene(knight);

        Collectables[][] before = scene.getOrbs();
        loader.setOrbs(scene);
        Collectables[][] after = scene.getOrbs();

        assertNotNull(after);
        assertNotSame(before, after);
    }

    @Test
    void createScene_realLevelFile_exercisesMonsterFactoryBranchNaturally() throws IOException {
        // This test deliberately uses the real level1.lvl content (no replaceLines),
        // because those files usually include at least one monster marker and also non-monster chars,
        // covering both branches: (monster != null) and (monster == null).
        SceneLoader loader = new SceneLoader(1);

        Knight knight = mockKnightWithMutablePosition();
        Scene scene = loader.createScene(knight);

        List<Enemies> monsters = scene.getMonsters();
        assertNotNull(monsters);
        // Don't assert size (depends on the real level), just ensure it was created.
    }
}
