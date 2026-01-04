package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.game.elements.enemies.Enemies;
import pt.feup.tvvs.soulknight.view.elements.monsters.MonsterViewer;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MonsterViewerWhiteBoxTests {

    private MonsterViewer makeViewerWithMockSprites(Sprite minhot, Sprite purple, Sprite sword0, Sprite sword1) throws IOException {
        SpriteLoader loader = mock(SpriteLoader.class);

        when(loader.get(anyString())).thenAnswer(inv -> {
            String path = inv.getArgument(0, String.class);

            if (path.equals("sprites/Enemies/MinhoteMonster.png")) return minhot;
            if (path.equals("sprites/Enemies/PurpleMonster.png")) return purple;
            if (path.equals("sprites/Enemies/swordMonster-Attack0.png")) return sword0;
            if (path.equals("sprites/Enemies/swordMonster-Attack1.png")) return sword1;

            throw new IllegalArgumentException("Unexpected sprite path in test: " + path);
        });

        return new MonsterViewer(loader);
    }

    private Enemies enemy(char c, double x, double y) {
        Enemies e = mock(Enemies.class);
        when(e.getChar()).thenReturn(c);
        when(e.getPosition()).thenReturn(new Position(x, y));
        return e;
    }

    @Test
    public void draw_whenCharUnknown_throwsIllegalArgumentException() throws Exception {
        MonsterViewer viewer = makeViewerWithMockSprites(
                mock(Sprite.class), mock(Sprite.class), mock(Sprite.class), mock(Sprite.class)
        );

        GUI gui = mock(GUI.class);
        Enemies unknown = enemy('Z', 10, 20);

        assertThrows(IllegalArgumentException.class, () -> viewer.draw(unknown, gui, 0L, 0, 0));
    }

    @Test
    public void draw_purpleMonster_l_drawsSpriteWithMinus4Minus1Offsets() throws Exception {
        Sprite minhot = mock(Sprite.class);
        Sprite purple = mock(Sprite.class);
        Sprite sword0 = mock(Sprite.class);
        Sprite sword1 = mock(Sprite.class);

        MonsterViewer viewer = makeViewerWithMockSprites(minhot, purple, sword0, sword1);

        GUI gui = mock(GUI.class);
        Enemies purpleEnemy = enemy('l', 50, 60);

        viewer.draw(purpleEnemy, gui, 0L, 0, 0);

        // 'l' => sprites.get(0).draw(gui, x-4, y-1)
        verify(purple, times(1)).draw(eq(gui), eq(46), eq(59));
        verifyNoInteractions(minhot, sword0, sword1);

        verify(gui, never()).drawHitBox(anyInt(), anyInt(), anyInt(), anyInt(), any());
    }

    @Test
    public void draw_ghostMonster_m_drawsSpriteAndHitBox() throws Exception {
        Sprite minhot = mock(Sprite.class);
        Sprite purple = mock(Sprite.class);
        Sprite sword0 = mock(Sprite.class);
        Sprite sword1 = mock(Sprite.class);

        MonsterViewer viewer = makeViewerWithMockSprites(minhot, purple, sword0, sword1);

        GUI gui = mock(GUI.class);
        Enemies ghost = enemy('m', 10, 20);

        viewer.draw(ghost, gui, 123L, 0, 0);

        // 'm' => sprites.get(0).draw(gui, x-4, y-6)
        verify(minhot, times(1)).draw(eq(gui), eq(6), eq(14));

        // plus ghost specifics
        verify(gui, times(1)).drawHitBox(eq(10), eq(20), eq(4), eq(4), any());
        verifyNoInteractions(purple, sword0, sword1);
    }

    @Test
    public void draw_swordMonster_E_animatesFrame0AtTick0_andFrame1AtTick5() throws Exception {
        Sprite minhot = mock(Sprite.class);
        Sprite purple = mock(Sprite.class);
        Sprite sword0 = mock(Sprite.class);
        Sprite sword1 = mock(Sprite.class);

        MonsterViewer viewer = makeViewerWithMockSprites(minhot, purple, sword0, sword1);

        GUI gui = mock(GUI.class);
        Enemies sword = enemy('E', 100, 40);

        // animationFPS=6, gameFPS=30 => frameTime = 30/6 = 5 ticks
        viewer.draw(sword, gui, 0L, 0, 0);   // tick 0 -> frameIndex 0
        viewer.draw(sword, gui, 5L, 0, 0);   // tick 5 -> frameIndex 1

        // 'E' => sprite.draw(gui, x-4, y)
        verify(sword0, times(1)).draw(eq(gui), eq(96), eq(40));
        verify(sword1, times(1)).draw(eq(gui), eq(96), eq(40));

        verifyNoInteractions(minhot, purple);
        verify(gui, never()).drawHitBox(anyInt(), anyInt(), anyInt(), anyInt(), any());
    }
}
