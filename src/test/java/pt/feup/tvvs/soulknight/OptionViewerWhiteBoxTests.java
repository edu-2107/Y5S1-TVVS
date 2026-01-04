package pt.feup.tvvs.soulknight;

import com.googlecode.lanterna.TextColor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pt.feup.tvvs.soulknight.gui.RescalableGUI;
import pt.feup.tvvs.soulknight.model.menu.Option;
import pt.feup.tvvs.soulknight.view.menu.OptionViewer;
import pt.feup.tvvs.soulknight.view.text.TextViewer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OptionViewerWhiteBoxTests {

    private static final TextColor.RGB COLOR = new TextColor.RGB(10, 20, 30);

    @Test
    public void draw_startGame_writesStart() {
        TextViewer tv = mock(TextViewer.class);
        OptionViewer viewer = new OptionViewer(tv);

        Option opt = new Option(7, 9, Option.Type.START_GAME);
        RescalableGUI gui = mock(RescalableGUI.class);

        viewer.draw(opt, gui, COLOR);

        verify(tv).draw(eq("Start"), eq(7.0), eq(9.0), eq(COLOR), eq(gui));
    }

    @Test
    public void draw_settings_writesSettings() {
        TextViewer tv = mock(TextViewer.class);
        OptionViewer viewer = new OptionViewer(tv);

        Option opt = new Option(1, 2, Option.Type.SETTINGS);
        RescalableGUI gui = mock(RescalableGUI.class);

        viewer.draw(opt, gui, COLOR);

        verify(tv).draw(eq("Settings"), eq(1.0), eq(2.0), eq(COLOR), eq(gui));
    }

    @Test
    public void draw_exit_writesExit() {
        TextViewer tv = mock(TextViewer.class);
        OptionViewer viewer = new OptionViewer(tv);

        Option opt = new Option(3, 4, Option.Type.EXIT);
        RescalableGUI gui = mock(RescalableGUI.class);

        viewer.draw(opt, gui, COLOR);

        verify(tv).draw(eq("Exit"), eq(3.0), eq(4.0), eq(COLOR), eq(gui));
    }

    @Test
    public void draw_toMainMenu_writesGoBack() {
        TextViewer tv = mock(TextViewer.class);
        OptionViewer viewer = new OptionViewer(tv);

        Option opt = new Option(11, 12, Option.Type.TO_MAIN_MENU);
        RescalableGUI gui = mock(RescalableGUI.class);

        viewer.draw(opt, gui, COLOR);

        verify(tv).draw(eq("Go Back"), eq(11.0), eq(12.0), eq(COLOR), eq(gui));
    }

    @Test
    public void draw_resolution_whenScaleNull_writesAutomaticLabel() {
        TextViewer tv = mock(TextViewer.class);
        OptionViewer viewer = new OptionViewer(tv);

        Option opt = new Option(5, 6, Option.Type.RESOLUTION);
        RescalableGUI gui = mock(RescalableGUI.class);
        when(gui.getResolutionScale()).thenReturn(null);

        viewer.draw(opt, gui, COLOR);

        verify(tv).draw(eq("Resolution:   Automatic >"), eq(5.0), eq(6.0), eq(COLOR), eq(gui));
    }

    @Test
    public void draw_resolution_whenScaleIsLast_endsWithBlank() {
        TextViewer tv = mock(TextViewer.class);
        OptionViewer viewer = new OptionViewer(tv);

        Option opt = new Option(5, 6, Option.Type.RESOLUTION);
        RescalableGUI gui = mock(RescalableGUI.class);

        RescalableGUI.ResolutionScale[] values = RescalableGUI.ResolutionScale.values();
        RescalableGUI.ResolutionScale last = values[values.length - 1];
        when(gui.getResolutionScale()).thenReturn(last);

        viewer.draw(opt, gui, COLOR);

        ArgumentCaptor<String> textCap = ArgumentCaptor.forClass(String.class);
        verify(tv).draw(textCap.capture(), eq(5.0), eq(6.0), eq(COLOR), eq(gui));

        String text = textCap.getValue();
        assertTrue(text.startsWith("Resolution: < "));
        // quando é a última resolução: ternário devolve ' ' (espaço) em vez de '>'
        assertTrue(text.endsWith("  "), "last resolution should end with space + space");
        assertTrue(text.contains(last.getWidth() + "X" + last.getHeight()));
    }

    @Test
    public void draw_resolution_whenScaleIsNotLast_forcesArrowGreaterThan_evenIfEnumHasOneValue() {
        TextViewer tv = mock(TextViewer.class);
        OptionViewer viewer = new OptionViewer(tv);

        Option opt = new Option(5, 6, Option.Type.RESOLUTION);
        RescalableGUI gui = mock(RescalableGUI.class);

        // "last" real do enum (pode ser o único valor)
        RescalableGUI.ResolutionScale[] values = RescalableGUI.ResolutionScale.values();
        RescalableGUI.ResolutionScale last = values[values.length - 1];

        // criamos um mock diferente do "last" para forçar (resolutions[last] == guiScale) ser FALSE
        RescalableGUI.ResolutionScale notLast = mock(RescalableGUI.ResolutionScale.class);
        when(notLast.getWidth()).thenReturn(1280);
        when(notLast.getHeight()).thenReturn(720);

        // garante que é diferente do last (mesmo se houver só 1 valor no enum)
        assertNotSame(last, notLast);

        when(gui.getResolutionScale()).thenReturn(notLast);

        viewer.draw(opt, gui, COLOR);

        ArgumentCaptor<String> textCap = ArgumentCaptor.forClass(String.class);
        verify(tv).draw(textCap.capture(), eq(5.0), eq(6.0), eq(COLOR), eq(gui));

        String text = textCap.getValue();
        assertTrue(text.startsWith("Resolution: < "));
        assertTrue(text.endsWith("> "), "non-last resolution should end with '> '");
        assertTrue(text.contains("1280X720"));
    }
}
