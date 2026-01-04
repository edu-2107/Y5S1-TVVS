package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.credits.Credits;
import pt.feup.tvvs.soulknight.view.menu.LogoViewer;
import pt.feup.tvvs.soulknight.view.sprites.ViewerProvider;
import pt.feup.tvvs.soulknight.view.states.CreditsViewer;
import pt.feup.tvvs.soulknight.view.text.TextViewer;

import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

class CreditsViewerWhiteBoxTests {

    private static void invoke(Object target, String method, Class<?>[] types, Object... args) {
        try {
            Method m = target.getClass().getDeclaredMethod(method, types);
            m.setAccessible(true);
            m.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CreditsViewer createViewer(Credits credits) {
        ViewerProvider vp = mock(ViewerProvider.class);
        when(vp.getTextViewer()).thenReturn(mock(TextViewer.class));
        when(vp.getLogoViewer()).thenReturn(mock(LogoViewer.class));
        return new CreditsViewer(credits, vp);
    }

    @Test
    public void drawMessages_and_drawNames_withEmptyArrays_coverSkipBranches() {
        Credits credits = mock(Credits.class);
        when(credits.getMessages()).thenReturn(new String[]{});
        when(credits.getNames()).thenReturn(new String[]{});
        when(credits.getScore()).thenReturn(0);
        when(credits.getDeaths()).thenReturn(0);
        when(credits.getMinutes()).thenReturn(0);
        when(credits.getSeconds()).thenReturn(0);

        CreditsViewer viewer = createViewer(credits);
        GUI gui = mock(GUI.class);

        invoke(viewer, "drawMessages", new Class[]{GUI.class}, gui);
        invoke(viewer, "drawNames", new Class[]{GUI.class}, gui);
    }

    @Test
    public void drawMessages_and_drawNames_withElements_coverLoopBranches() {
        Credits credits = mock(Credits.class);
        when(credits.getMessages()).thenReturn(new String[]{"Hello", "World"});
        when(credits.getNames()).thenReturn(new String[]{"Edu", "TVVS"});
        when(credits.getScore()).thenReturn(1);
        when(credits.getDeaths()).thenReturn(2);
        when(credits.getMinutes()).thenReturn(3);
        when(credits.getSeconds()).thenReturn(4);

        CreditsViewer viewer = createViewer(credits);
        GUI gui = mock(GUI.class);

        invoke(viewer, "drawMessages", new Class[]{GUI.class}, gui);
        invoke(viewer, "drawNames", new Class[]{GUI.class}, gui);
    }

    @Test
    public void drawSmoothBackground_timeZero_hitsNormalRGBBranch() {
        CreditsViewer viewer = createViewer(mock(Credits.class));
        GUI gui = mock(GUI.class);

        invoke(viewer,
                "drawSmoothColorfulBackground",
                new Class[]{GUI.class, long.class},
                gui, 0L);
    }

    @Test
    public void drawSmoothBackground_largePositiveTime_hitsUpperClampBranch() {
        CreditsViewer viewer = createViewer(mock(Credits.class));
        GUI gui = mock(GUI.class);

        invoke(viewer,
                "drawSmoothColorfulBackground",
                new Class[]{GUI.class, long.class},
                gui, 1000L);
    }

    @Test
    public void drawSmoothBackground_negativeTime_hitsLowerClampBranch() {
        CreditsViewer viewer = createViewer(mock(Credits.class));
        GUI gui = mock(GUI.class);

        invoke(viewer,
                "drawSmoothColorfulBackground",
                new Class[]{GUI.class, long.class},
                gui, -1000L);
    }

    @Test
    public void draw_callsAllSubMethods() throws Exception {
        Credits credits = mock(Credits.class);
        when(credits.getMessages()).thenReturn(new String[]{"A"});
        when(credits.getNames()).thenReturn(new String[]{"B"});
        when(credits.getScore()).thenReturn(9);
        when(credits.getDeaths()).thenReturn(8);
        when(credits.getMinutes()).thenReturn(7);
        when(credits.getSeconds()).thenReturn(6);

        CreditsViewer viewer = createViewer(credits);
        GUI gui = mock(GUI.class);

        viewer.draw(gui, 1L);

        verify(gui).cls();
        verify(gui).flush();
    }
}
