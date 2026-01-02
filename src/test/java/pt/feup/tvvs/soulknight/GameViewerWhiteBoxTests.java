package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.game.elements.collectables.Collectables;
import pt.feup.tvvs.soulknight.model.game.elements.enemies.Enemies;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.game.elements.particle.Particle;
import pt.feup.tvvs.soulknight.model.game.elements.rocks.Rock;
import pt.feup.tvvs.soulknight.model.game.elements.Spike;
import pt.feup.tvvs.soulknight.model.game.elements.Tree;
import pt.feup.tvvs.soulknight.model.game.elements.tile.Tile;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;
import pt.feup.tvvs.soulknight.view.elements.collectables.OrbViewer;
import pt.feup.tvvs.soulknight.view.elements.knight.KnightViewer;
import pt.feup.tvvs.soulknight.view.elements.monsters.MonsterViewer;
import pt.feup.tvvs.soulknight.view.elements.particle.ParticleViewer;
import pt.feup.tvvs.soulknight.view.elements.rocks.RockViewer;
import pt.feup.tvvs.soulknight.view.elements.spike.SpikeViewer;
import pt.feup.tvvs.soulknight.view.elements.tile.TileViewer;
import pt.feup.tvvs.soulknight.view.elements.tree.TreeViewer;
import pt.feup.tvvs.soulknight.view.sprites.ViewerProvider;

import com.googlecode.lanterna.TextColor;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameViewerWhiteBoxTests {

    private static Scene mockSceneForDraw(
            List<Particle> particles,
            List<Particle> doubleJump,
            List<Particle> jump,
            List<Particle> respawn,
            List<Particle> dash,
            Spike[][] spikes,
            Tile[][] tiles,
            Tree[][] trees,
            Collectables[][] orbs,
            Rock[][] rocks,
            Knight player,
            List<Enemies> monsters
    ) {
        Scene scene = mock(Scene.class);

        when(scene.getParticles()).thenReturn(particles);
        when(scene.getDoubleJumpParticles()).thenReturn(doubleJump);
        when(scene.getJumpParticles()).thenReturn(jump);
        when(scene.getRespawnParticles()).thenReturn(respawn);
        when(scene.getDashParticles()).thenReturn(dash);

        when(scene.getSpikes()).thenReturn(spikes);
        when(scene.getTiles()).thenReturn(tiles);
        when(scene.getTrees()).thenReturn(trees);
        when(scene.getOrbs()).thenReturn(orbs);
        when(scene.getRocks()).thenReturn(rocks);

        when(scene.getPlayer()).thenReturn(player);
        when(scene.getMonsters()).thenReturn(monsters);

        return scene;
    }

    private static ViewerProvider mockViewerProvider(
            ParticleViewer particleViewer,
            KnightViewer knightViewer,
            TileViewer tileViewer,
            SpikeViewer spikeViewer,
            TreeViewer treeViewer,
            OrbViewer orbViewer,
            RockViewer rockViewer,
            MonsterViewer monsterViewer
    ) throws IOException {
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getParticleViewer()).thenReturn(particleViewer);
        when(vp.getPlayerViewer()).thenReturn(knightViewer);
        when(vp.getTileViewer()).thenReturn(tileViewer);
        when(vp.getSpikeViewer()).thenReturn(spikeViewer);
        when(vp.getTreeViewer()).thenReturn(treeViewer);
        when(vp.getOrbViewer()).thenReturn(orbViewer);
        when(vp.getRockViewer()).thenReturn(rockViewer);
        when(vp.getMonsterViewer()).thenReturn(monsterViewer);
        return vp;
    }

    @Test
    void draw_flashActive_coversIOExceptionCatch_andArrayNullBranch() throws Exception {
        // GUI
        GUI gui = mock(GUI.class);

        // Viewers
        ParticleViewer particleViewer = mock(ParticleViewer.class);
        KnightViewer knightViewer = mock(KnightViewer.class);
        TileViewer tileViewer = mock(TileViewer.class);
        SpikeViewer spikeViewer = mock(SpikeViewer.class);
        TreeViewer treeViewer = mock(TreeViewer.class);
        OrbViewer orbViewer = mock(OrbViewer.class);
        RockViewer rockViewer = mock(RockViewer.class);
        MonsterViewer monsterViewer = mock(MonsterViewer.class);

        ViewerProvider vp = mockViewerProvider(
                particleViewer, knightViewer, tileViewer, spikeViewer,
                treeViewer, orbViewer, rockViewer, monsterViewer
        );

        // Elements for list drawElements (cover try + catch(IOException))
        Particle pBoom = mock(Particle.class);
        when(pBoom.getPosition()).thenReturn(new Position(10.9, 20.2));
        Particle pOk = mock(Particle.class);
        when(pOk.getPosition()).thenReturn(new Position(30.1, 40.8));

        // Force IOException for first particle
        doThrow(new IOException("boom"))
                .when(particleViewer)
                .draw(eq(pBoom), eq(gui), anyLong(), anyInt(), anyInt());

        // Second particle draws OK
        doNothing()
                .when(particleViewer)
                .draw(eq(pOk), eq(gui), anyLong(), anyInt(), anyInt());

        // Arrays for drawElements(T[][]...) null/non-null branch
        Spike s1 = mock(Spike.class);
        when(s1.getPosition()).thenReturn(new Position(8.0, 8.0));
        Spike[][] spikes = new Spike[][] { new Spike[] { s1, null } };

        Tile t1 = mock(Tile.class);
        when(t1.getPosition()).thenReturn(new Position(16.0, 8.0));
        Tile[][] tiles = new Tile[][] { new Tile[] { null, t1 } };

        Tree[][] trees = new Tree[][] { new Tree[] { null, null } }; // all null OK
        Collectables[][] orbs = new Collectables[][] { new Collectables[] { null, null } };
        Rock[][] rocks = new Rock[][] { new Rock[] { null, null } };

        Knight player = mock(Knight.class);
        when(player.getPosition()).thenReturn(new Position(50.7, 60.9));
        List<Enemies> monsters = List.of(); // empty list OK

        Scene scene = mockSceneForDraw(
                List.of(pBoom, pOk),  // particles
                List.of(), List.of(), List.of(), List.of(), // other particle lists empty
                spikes, tiles, trees, orbs, rocks,
                player, monsters
        );

        pt.feup.tvvs.soulknight.view.states.GameViewer gv =
                new pt.feup.tvvs.soulknight.view.states.GameViewer(scene, vp);

        gv.draw(gui, 0L);

        verify(gui, times(1)).cls();
        verify(gui, times(1)).flush();

        verify(gui, atLeastOnce()).drawPixel(anyInt(), anyInt(), argThat(c ->
                (c instanceof TextColor.RGB) &&
                        ((TextColor.RGB) c).getRed() == 255 &&
                        ((TextColor.RGB) c).getGreen() == 255 &&
                        ((TextColor.RGB) c).getBlue() == 255
        ));

        verify(particleViewer, times(1)).draw(eq(pBoom), eq(gui), anyLong(), anyInt(), anyInt());
        verify(particleViewer, times(1)).draw(eq(pOk), eq(gui), anyLong(), anyInt(), anyInt());
        verify(spikeViewer, times(1)).draw(eq(s1), eq(gui), anyLong(), eq(8), eq(8));
        verify(tileViewer, times(1)).draw(eq(t1), eq(gui), anyLong(), eq(16), eq(8));
        verify(knightViewer, times(1)).draw(eq(player), eq(gui), anyLong(), eq(50), eq(60));
    }

    @Test
    void draw_afterEffectActive_coversAfterEffectBranch() throws Exception {
        GUI gui = mock(GUI.class);

        ParticleViewer particleViewer = mock(ParticleViewer.class);
        KnightViewer knightViewer = mock(KnightViewer.class);
        TileViewer tileViewer = mock(TileViewer.class);
        SpikeViewer spikeViewer = mock(SpikeViewer.class);
        TreeViewer treeViewer = mock(TreeViewer.class);
        OrbViewer orbViewer = mock(OrbViewer.class);
        RockViewer rockViewer = mock(RockViewer.class);
        MonsterViewer monsterViewer = mock(MonsterViewer.class);

        ViewerProvider vp = mockViewerProvider(
                particleViewer, knightViewer, tileViewer, spikeViewer,
                treeViewer, orbViewer, rockViewer, monsterViewer
        );

        Knight player = mock(Knight.class);
        when(player.getPosition()).thenReturn(new Position(0, 0));

        Scene scene = mockSceneForDraw(
                List.of(), List.of(), List.of(), List.of(), List.of(),
                new Spike[][] { new Spike[] { null } },
                new Tile[][] { new Tile[] { null } },
                new Tree[][] { new Tree[] { null } },
                new Collectables[][] { new Collectables[] { null } },
                new Rock[][] { new Rock[] { null } },
                player,
                List.of()
        );

        pt.feup.tvvs.soulknight.view.states.GameViewer gv =
                new pt.feup.tvvs.soulknight.view.states.GameViewer(scene, vp);

        gv.draw(gui, 30L);

        verify(gui, atLeastOnce()).drawPixel(anyInt(), anyInt(), (TextColor.RGB) any(TextColor.class));
        verify(gui, times(1)).cls();
        verify(gui, times(1)).flush();
    }

    @Test
    void draw_normalGradient_coversNormalElseBranch() throws Exception {
        GUI gui = mock(GUI.class);

        ParticleViewer particleViewer = mock(ParticleViewer.class);
        KnightViewer knightViewer = mock(KnightViewer.class);
        TileViewer tileViewer = mock(TileViewer.class);
        SpikeViewer spikeViewer = mock(SpikeViewer.class);
        TreeViewer treeViewer = mock(TreeViewer.class);
        OrbViewer orbViewer = mock(OrbViewer.class);
        RockViewer rockViewer = mock(RockViewer.class);
        MonsterViewer monsterViewer = mock(MonsterViewer.class);

        ViewerProvider vp = mockViewerProvider(
                particleViewer, knightViewer, tileViewer, spikeViewer,
                treeViewer, orbViewer, rockViewer, monsterViewer
        );

        Knight player = mock(Knight.class);
        when(player.getPosition()).thenReturn(new Position(0, 0));

        Scene scene = mockSceneForDraw(
                List.of(), List.of(), List.of(), List.of(), List.of(),
                new Spike[][] { new Spike[] { null } },
                new Tile[][] { new Tile[] { null } },
                new Tree[][] { new Tree[] { null } },
                new Collectables[][] { new Collectables[] { null } },
                new Rock[][] { new Rock[] { null } },
                player,
                List.of()
        );

        pt.feup.tvvs.soulknight.view.states.GameViewer gv =
                new pt.feup.tvvs.soulknight.view.states.GameViewer(scene, vp);

        // time=100 => normal gradient branch (not flash, not afterEffect)
        gv.draw(gui, 100L);

        verify(gui, atLeastOnce()).drawPixel(anyInt(), anyInt(), (TextColor.RGB) any(TextColor.class));
        verify(gui, times(1)).cls();
        verify(gui, times(1)).flush();
    }
}
