package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.controller.game.ParticleController;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.game.elements.particle.Particle;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class ParticleControllerWhiteBoxTests {

    private Scene scene;
    private ParticleController controller;

    @BeforeEach
    public void setUp() {
        scene = mock(Scene.class);
        controller = new ParticleController(scene);
    }

    @Test
    public void move_allListsEmpty_coversSkipBranches() {
        when(scene.getParticles()).thenReturn(Collections.emptyList());
        when(scene.getDoubleJumpParticles()).thenReturn(Collections.emptyList());
        when(scene.getJumpParticles()).thenReturn(Collections.emptyList());
        when(scene.getRespawnParticles()).thenReturn(Collections.emptyList());
        when(scene.getDashParticles()).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> controller.move(null, GUI.ACTION.NULL, 10L));

        verify(scene, times(1)).getParticles();
        verify(scene, times(1)).getDoubleJumpParticles();
        verify(scene, times(1)).getJumpParticles();
        verify(scene, times(1)).getRespawnParticles();
        verify(scene, times(1)).getDashParticles();
        verifyNoMoreInteractions(scene);
    }

    @Test
    public void move_eachListHasOneParticle_coversEnterBranches_andSetsSceneAndPosition() throws IOException {
        Particle p1 = mock(Particle.class);
        Particle p2 = mock(Particle.class);
        Particle p3 = mock(Particle.class);
        Particle p4 = mock(Particle.class);
        Particle p5 = mock(Particle.class);

        when(scene.getParticles()).thenReturn(List.of(p1));
        when(scene.getDoubleJumpParticles()).thenReturn(List.of(p2));
        when(scene.getJumpParticles()).thenReturn(List.of(p3));
        when(scene.getRespawnParticles()).thenReturn(List.of(p4));
        when(scene.getDashParticles()).thenReturn(List.of(p5));

        when(p1.moveParticle(scene, 10L)).thenReturn(new Position(1, 1));
        when(p2.moveParticle(scene, 10L)).thenReturn(new Position(2, 2));
        when(p3.moveParticle(scene, 10L)).thenReturn(new Position(3, 3));
        when(p4.moveParticle(scene, 10L)).thenReturn(new Position(4, 4));
        when(p5.moveParticle(scene, 10L)).thenReturn(new Position(5, 5));

        controller.move(null, GUI.ACTION.NULL, 10L);

        verify(p1).setScene(scene);
        verify(p1).moveParticle(scene, 10L);
        verify(p1).setPosition(new Position(1, 1));

        verify(p2).setScene(scene);
        verify(p2).moveParticle(scene, 10L);
        verify(p2).setPosition(new Position(2, 2));

        verify(p3).setScene(scene);
        verify(p3).moveParticle(scene, 10L);
        verify(p3).setPosition(new Position(3, 3));

        verify(p4).setScene(scene);
        verify(p4).moveParticle(scene, 10L);
        verify(p4).setPosition(new Position(4, 4));

        verify(p5).setScene(scene);
        verify(p5).moveParticle(scene, 10L);
        verify(p5).setPosition(new Position(5, 5));
    }
}
