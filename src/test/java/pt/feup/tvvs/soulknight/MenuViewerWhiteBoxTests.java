package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.gui.RescalableGUI;
import pt.feup.tvvs.soulknight.model.menu.MainMenu;
import pt.feup.tvvs.soulknight.model.menu.Menu;
import pt.feup.tvvs.soulknight.model.menu.Option;
import pt.feup.tvvs.soulknight.model.menu.Particle;
import pt.feup.tvvs.soulknight.model.menu.SettingsMenu;
import pt.feup.tvvs.soulknight.view.menu.LogoViewer;
import pt.feup.tvvs.soulknight.view.menu.OptionViewer;
import pt.feup.tvvs.soulknight.view.menu.ParticleViewer;
import pt.feup.tvvs.soulknight.view.states.MenuViewer;
import pt.feup.tvvs.soulknight.view.sprites.ViewerProvider;

import java.lang.reflect.Method;
import java.util.List;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MenuViewerWhiteBoxTests {

    private static Object invoke(Object target, String method, Class<?>[] types, Object... args) {
        try {
            Method m = target.getClass().getDeclaredMethod(method, types);
            m.setAccessible(true);
            return m.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void draw_coversMainMenuAndSettingsMenuBackgroundBranches() throws Exception {
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getEntryViewer()).thenReturn(mock(OptionViewer.class));
        when(vp.getLogoViewer()).thenReturn(mock(LogoViewer.class));

        RescalableGUI gui = mock(RescalableGUI.class, withSettings().extraInterfaces(GUI.class));

        MainMenu mainMenu = mock(MainMenu.class);
        when(mainMenu.getParticles()).thenReturn(List.of());
        when(mainMenu.getOptions()).thenReturn(List.of());
        when(mainMenu.isSelected(anyInt())).thenReturn(false);
        when(mainMenu.getInGame()).thenReturn(false);

        MenuViewer<MainMenu> mainViewer = new MenuViewer<>(mainMenu, vp);
        assertDoesNotThrow(() -> mainViewer.draw((GUI) gui, 1L));

        SettingsMenu settingsMenu = mock(SettingsMenu.class);
        when(settingsMenu.getParticles()).thenReturn(List.of());
        when(settingsMenu.getOptions()).thenReturn(List.of());
        when(settingsMenu.isSelected(anyInt())).thenReturn(false);
        when(settingsMenu.getInGame()).thenReturn(false);

        MenuViewer<SettingsMenu> settingsViewer = new MenuViewer<>(settingsMenu, vp);
        assertDoesNotThrow(() -> settingsViewer.draw((GUI) gui, 2L));

        verify((GUI) gui, atLeastOnce()).drawPixel(anyInt(), anyInt(), any());
        verify((GUI) gui, atLeast(2)).flush();
    }

    @Test
    void drawParticles_coversLoopBody() throws Exception {
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getEntryViewer()).thenReturn(mock(OptionViewer.class));
        when(vp.getLogoViewer()).thenReturn(mock(LogoViewer.class));

        Menu menu = mock(Menu.class);
        MenuViewer<Menu> viewer = new MenuViewer<>(menu, vp);

        GUI gui = mock(GUI.class);
        ParticleViewer particleViewer = mock(ParticleViewer.class);
        Particle p1 = mock(Particle.class);
        Particle p2 = mock(Particle.class);

        invoke(viewer,
                "drawParticles",
                new Class<?>[]{GUI.class, List.class, ParticleViewer.class, long.class},
                gui, List.of(p1, p2), particleViewer, 123L);

        verify(particleViewer, times(2)).draw(any(Particle.class), eq(gui), eq(123L), eq(0), eq(0));
    }

    @Test
    void drawOptions_coversAnimationMovementAndContinueBranch() throws Exception {
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getEntryViewer()).thenReturn(mock(OptionViewer.class));
        when(vp.getLogoViewer()).thenReturn(mock(LogoViewer.class));

        Menu menu = mock(Menu.class);
        when(menu.getInGame()).thenReturn(false);
        when(menu.isSelected(anyInt())).thenReturn(false);

        MenuViewer<Menu> viewer = new MenuViewer<>(menu, vp);
        RescalableGUI gui = mock(RescalableGUI.class);
        OptionViewer optionViewer = mock(OptionViewer.class);

        Option.Type t0 = Option.Type.values()[0];
        Option.Type t1 = Option.Type.values().length > 1 ? Option.Type.values()[1] : t0;
        Option o0 = new Option(10, 5, t0);
        Option o1 = new Option(20, 6, t1);

        invoke(viewer,
                "drawOptions",
                new Class<?>[]{RescalableGUI.class, List.class, OptionViewer.class, long.class},
                gui, List.of(o0, o1), optionViewer, 10L);

        verify(optionViewer, times(1)).draw(any(Option.class), eq(gui), any());
    }

    @Test
    void drawOptions_coversBlinkingVisibleAndInvisibleAndInGameToggle() throws Exception {
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getEntryViewer()).thenReturn(mock(OptionViewer.class));
        when(vp.getLogoViewer()).thenReturn(mock(LogoViewer.class));

        Menu menu = mock(Menu.class);
        when(menu.isSelected(anyInt())).thenReturn(true);
        MenuViewer<Menu> viewer = new MenuViewer<>(menu, vp);

        RescalableGUI gui = mock(RescalableGUI.class);
        OptionViewer optionViewer = mock(OptionViewer.class);
        Option o0 = new Option(10, 5, Option.Type.values()[0]);

        // not in-game, time=80 => (80/8)=10 => visible => draws
        when(menu.getInGame()).thenReturn(false);
        invoke(viewer, "drawOptions",
                new Class<?>[]{RescalableGUI.class, List.class, OptionViewer.class, long.class},
                gui, List.of(o0), optionViewer, 80L);

        when(menu.getInGame()).thenReturn(true);
        invoke(viewer, "drawOptions",
                new Class<?>[]{RescalableGUI.class, List.class, OptionViewer.class, long.class},
                gui, List.of(o0), optionViewer, 84L);

        verify(optionViewer, times(1)).draw(any(Option.class), eq(gui), any());
    }

    @Test
    void drawOptions_coversTimeUnder80BranchAndTimeOver80ElseBranch() throws Exception {
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getEntryViewer()).thenReturn(mock(OptionViewer.class));
        when(vp.getLogoViewer()).thenReturn(mock(LogoViewer.class));

        Menu menu = mock(Menu.class);
        when(menu.getInGame()).thenReturn(false);
        MenuViewer<Menu> viewer = new MenuViewer<>(menu, vp);

        RescalableGUI gui = mock(RescalableGUI.class);
        OptionViewer optionViewer = mock(OptionViewer.class);
        Option o0 = new Option(10, 5, Option.Type.values()[0]);

        when(menu.isSelected(anyInt())).thenReturn(true);
        invoke(viewer, "drawOptions",
                new Class<?>[]{RescalableGUI.class, List.class, OptionViewer.class, long.class},
                gui, List.of(o0), optionViewer, 1L);

        when(menu.isSelected(anyInt())).thenReturn(false);
        invoke(viewer, "drawOptions",
                new Class<?>[]{RescalableGUI.class, List.class, OptionViewer.class, long.class},
                gui, List.of(o0), optionViewer, 200L);

        verify(optionViewer, times(2)).draw(any(Option.class), eq(gui), any());
    }

    @Test
    void drawOptions_timeBeforeFirstOptionStart_skipsDrawingFirstOption() throws Exception {
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getEntryViewer()).thenReturn(mock(OptionViewer.class));
        when(vp.getLogoViewer()).thenReturn(mock(LogoViewer.class));

        Menu menu = mock(Menu.class);
        when(menu.getInGame()).thenReturn(false);
        when(menu.isSelected(anyInt())).thenReturn(false);
        MenuViewer<Menu> viewer = new MenuViewer<>(menu, vp);

        RescalableGUI gui = mock(RescalableGUI.class);
        OptionViewer optionViewer = mock(OptionViewer.class);
        Option o0 = new Option(10, 5, Option.Type.values()[0]);

        // time < 0 triggers: if (time < firstOptionStartTime) continue; for idx=0
        invoke(viewer, "drawOptions",
                new Class<?>[]{RescalableGUI.class, List.class, OptionViewer.class, long.class},
                gui, List.of(o0), optionViewer, -1L);

        verify(optionViewer, never()).draw(any(), any(), any());
    }

    @Test
    void drawOptions_blinkingNotInGameInvisible_doesNotDraw() throws Exception {
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getEntryViewer()).thenReturn(mock(OptionViewer.class));
        when(vp.getLogoViewer()).thenReturn(mock(LogoViewer.class));

        Menu menu = mock(Menu.class);
        when(menu.isSelected(anyInt())).thenReturn(true);
        when(menu.getInGame()).thenReturn(false);
        MenuViewer<Menu> viewer = new MenuViewer<>(menu, vp);

        RescalableGUI gui = mock(RescalableGUI.class);
        OptionViewer optionViewer = mock(OptionViewer.class);
        Option o0 = new Option(10, 5, Option.Type.values()[0]);

        // time=88 => (88/8)=11 (odd) => invisible when not in-game => should NOT draw
        invoke(viewer, "drawOptions",
                new Class<?>[]{RescalableGUI.class, List.class, OptionViewer.class, long.class},
                gui, List.of(o0), optionViewer, 88L);

        verify(optionViewer, never()).draw(any(), any(), any());
    }

    @Test
    void drawOptions_timeAtAndAfterAnimationEnd_doesNotApplyMovementOffset() throws Exception {
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getEntryViewer()).thenReturn(mock(OptionViewer.class));
        when(vp.getLogoViewer()).thenReturn(mock(LogoViewer.class));

        Menu menu = mock(Menu.class);
        when(menu.getInGame()).thenReturn(false);
        when(menu.isSelected(anyInt())).thenReturn(false);
        MenuViewer<Menu> viewer = new MenuViewer<>(menu, vp);

        RescalableGUI gui = mock(RescalableGUI.class);
        OptionViewer optionViewer = mock(OptionViewer.class);
        Option o0 = new Option(10, 5, Option.Type.values()[0]);

        // time=20 is exactly at end boundary => movement condition should be FALSE
        invoke(viewer, "drawOptions",
                new Class<?>[]{RescalableGUI.class, List.class, OptionViewer.class, long.class},
                gui, List.of(o0), optionViewer, 20L);

        ArgumentCaptor<Option> captor = ArgumentCaptor.forClass(Option.class);
        verify(optionViewer).draw(captor.capture(), eq(gui), any());
        assertEquals(10, (int) captor.getValue().getPosition().x());

        reset(optionViewer);

        // time=25 (after end) also should have no movement
        invoke(viewer, "drawOptions",
                new Class<?>[]{RescalableGUI.class, List.class, OptionViewer.class, long.class},
                gui, List.of(o0), optionViewer, 25L);

        captor = ArgumentCaptor.forClass(Option.class);
        verify(optionViewer).draw(captor.capture(), eq(gui), any());
        assertEquals(10, (int) captor.getValue().getPosition().x());
    }
}
