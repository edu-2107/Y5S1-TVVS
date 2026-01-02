package pt.feup.tvvs.soulknight;

import com.googlecode.lanterna.TextColor;
import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.gui.RescalableGUI;
import pt.feup.tvvs.soulknight.model.menu.Menu;
import pt.feup.tvvs.soulknight.model.menu.Option;
import pt.feup.tvvs.soulknight.view.menu.LogoViewer;
import pt.feup.tvvs.soulknight.view.menu.OptionViewer;
import pt.feup.tvvs.soulknight.view.sprites.ViewerProvider;
import pt.feup.tvvs.soulknight.view.states.MenuViewer;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuViewerMutationTests {

    private static void invokeDrawOptions(
            MenuViewer<Menu> viewer,
            RescalableGUI gui,
            List<Option> options,
            OptionViewer ov,
            long time
    ) throws Exception {
        Method m = MenuViewer.class.getDeclaredMethod(
                "drawOptions",
                RescalableGUI.class, List.class, OptionViewer.class, long.class
        );
        m.setAccessible(true);
        m.invoke(viewer, gui, options, ov, time);
    }

    @Test
    void secondOptionIsNotDrawnBeforeItsStartTime_killsContinueMutant() throws Exception {
        Option.Type type = Option.Type.START_GAME;

        Option o1 = new Option(50, 10, type); // start at t=0
        Option o2 = new Option(60, 20, type); // start at t=20

        Menu menu = mock(Menu.class);
        when(menu.getOptions()).thenReturn(List.of(o1, o2));
        when(menu.isSelected(anyInt())).thenReturn(false);
        when(menu.getInGame()).thenReturn(false);

        OptionViewer ov = mock(OptionViewer.class);
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getEntryViewer()).thenReturn(ov);
        when(vp.getLogoViewer()).thenReturn(mock(LogoViewer.class));

        MenuViewer<Menu> viewer = new MenuViewer<>(menu, vp);
        RescalableGUI gui = mock(RescalableGUI.class, withSettings().extraInterfaces(GUI.class));

        invokeDrawOptions(viewer, gui, menu.getOptions(), ov, 0L);

        // Se o mutante remover o "continue", isto passa a 2 → teste mata o mutante
        verify(ov, times(1))
                .draw(any(Option.class), eq(gui), any(TextColor.RGB.class));
    }

    @Test
    void slidingAnimationChangesXPosition_killsArithmeticMutants() throws Exception {
        Option o = new Option(50, 10, Option.Type.START_GAME);

        Menu menu = mock(Menu.class);
        when(menu.getOptions()).thenReturn(List.of(o));
        when(menu.isSelected(0)).thenReturn(false);
        when(menu.getInGame()).thenReturn(false);

        OptionViewer ov = mock(OptionViewer.class);
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getEntryViewer()).thenReturn(ov);
        when(vp.getLogoViewer()).thenReturn(mock(LogoViewer.class));

        MenuViewer<Menu> viewer = new MenuViewer<>(menu, vp);
        RescalableGUI gui = mock(RescalableGUI.class, withSettings().extraInterfaces(GUI.class));

        // time=10 → offset ≈ 20 → x=70
        invokeDrawOptions(viewer, gui, menu.getOptions(), ov, 10L);

        var captor = org.mockito.ArgumentCaptor.forClass(Option.class);
        verify(ov).draw(captor.capture(), eq(gui), any());

        Option drawn = captor.getValue();
        assertEquals(70, drawn.getPosition().x());
        assertEquals(10, drawn.getPosition().y());
    }

    @Test
    void selectedOptionBlinksAfter80_killsVisibilityMutants() throws Exception {
        Option o = new Option(50, 10, Option.Type.START_GAME);

        Menu menu = mock(Menu.class);
        when(menu.getOptions()).thenReturn(List.of(o));
        when(menu.isSelected(0)).thenReturn(true);
        when(menu.getInGame()).thenReturn(false);

        OptionViewer ov = mock(OptionViewer.class);
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getEntryViewer()).thenReturn(ov);
        when(vp.getLogoViewer()).thenReturn(mock(LogoViewer.class));

        MenuViewer<Menu> viewer = new MenuViewer<>(menu, vp);
        RescalableGUI gui = mock(RescalableGUI.class, withSettings().extraInterfaces(GUI.class));

        // time=88 → invisible → não deve desenhar
        invokeDrawOptions(viewer, gui, menu.getOptions(), ov, 88L);

        verify(ov, never()).draw(any(), any(), any());
    }
}
