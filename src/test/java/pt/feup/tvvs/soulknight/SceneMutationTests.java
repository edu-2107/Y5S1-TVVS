package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.game.elements.collectables.Collectables;
import pt.feup.tvvs.soulknight.model.game.elements.enemies.Enemies;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SceneMutationTests {

    @Test
    public void collectOrbs_whenOrbPresent_callsBenefit_nullsCell_andAddsOrb() {
        // Scene com 2x2 tiles (mapa interno usa [height][width])
        Scene scene = new Scene(2, 2, 0);

        Knight knight = mock(Knight.class);
        scene.setPlayer(knight);

        // Posição e tamanho do player para cair no tile (0,0)
        when(knight.getPosition()).thenReturn(new Position(0, 0));
        when(knight.getWidth()).thenReturn((int) 1.0);
        when(knight.getHeight()).thenReturn((int) 1.0);

        Collectables orb = mock(Collectables.class);
        Collectables[][] orbs = new Collectables[2][2];
        orbs[0][0] = orb;
        scene.setOrbs(orbs);

        scene.collectOrbs(scene.getOrbs());

        // benefit chamado e addOrbs chamado
        verify(orb, times(1)).benefit(knight);
        verify(knight, times(1)).addOrbs();

        // a célula fica null (mata mutantes de remoção)
        assertNull(scene.getOrbs()[0][0]);
    }

    @Test
    public void collectOrbs_whenNoOrb_doesNotCallAddOrbs() {
        Scene scene = new Scene(2, 2, 0);

        Knight knight = mock(Knight.class);
        scene.setPlayer(knight);

        when(knight.getPosition()).thenReturn(new Position(0, 0));
        when(knight.getWidth()).thenReturn((int) 1.0);
        when(knight.getHeight()).thenReturn((int) 1.0);

        Collectables[][] orbs = new Collectables[2][2];
        scene.setOrbs(orbs);

        scene.collectOrbs(scene.getOrbs());

        verify(knight, never()).addOrbs();
    }

    @Test
    public void collideMonsters_whenCollision_callsPlayerHit_withDamage() {
        Scene scene = new Scene(10, 10, 0);

        Knight knight = mock(Knight.class);
        scene.setPlayer(knight);

        // Player AABB: (0,0) com size 2x2
        when(knight.getPosition()).thenReturn(new Position(0, 0));
        when(knight.getWidth()).thenReturn((int) 2.0);
        when(knight.getHeight()).thenReturn((int) 2.0);

        Enemies enemy = mock(Enemies.class);
        when(enemy.getPosition()).thenReturn(new Position(1, 1)); // dentro do AABB do player
        when(enemy.getSize()).thenReturn(new Position(2, 2));
        when(enemy.getDamage()).thenReturn(7);

        scene.collideMonsters(List.of(enemy));

        verify(knight, times(1)).PlayerHit(7);
    }

    @Test
    public void collideMonsters_whenNoCollision_doesNotCallPlayerHit() {
        Scene scene = new Scene(10, 10, 0);

        Knight knight = mock(Knight.class);
        scene.setPlayer(knight);

        when(knight.getPosition()).thenReturn(new Position(0, 0));
        when(knight.getWidth()).thenReturn((int) 2.0);
        when(knight.getHeight()).thenReturn((int) 2.0);

        Enemies enemy = mock(Enemies.class);
        when(enemy.getPosition()).thenReturn(new Position(50, 50)); // fora
        when(enemy.getSize()).thenReturn(new Position(2, 2));
        when(enemy.getDamage()).thenReturn(7);

        scene.collideMonsters(List.of(enemy));

        verify(knight, never()).PlayerHit(anyInt());
    }

    @Test
    public void isAtEndPosition_boundary_isInclusive() {
        Scene scene = new Scene(10, 10, 0);

        Knight knight = mock(Knight.class);
        scene.setPlayer(knight);

        scene.setEndPosition(new Position(5, 0));

        // x == EndPosition.x => deve ser true (mata mutantes >= vs >)
        when(knight.getPosition()).thenReturn(new Position(5, 0));
        assertTrue(scene.isAtEndPosition());

        // x < EndPosition.x => false
        when(knight.getPosition()).thenReturn(new Position(4.999, 0));
        assertFalse(scene.isAtEndPosition());
    }
}
