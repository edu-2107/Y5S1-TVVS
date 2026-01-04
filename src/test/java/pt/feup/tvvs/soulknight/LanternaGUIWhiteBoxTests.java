package pt.feup.tvvs.soulknight;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.gui.LanternaGUI;
import pt.feup.tvvs.soulknight.gui.RescalableGUI;
import pt.feup.tvvs.soulknight.gui.ScreenGenerator;

import java.awt.Canvas;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LanternaGUIWhiteBoxTests {

    private ScreenGenerator screenGenerator;
    private Screen screen;
    private TextGraphics tg;
    private LanternaGUI gui;

    private final Canvas source = new Canvas();

    @BeforeEach
    public void setUp() throws Exception {
        screenGenerator = mock(ScreenGenerator.class);
        screen = mock(Screen.class);
        tg = mock(TextGraphics.class);

        when(screenGenerator.createScreen(any(), anyString(), any(KeyAdapter.class))).thenReturn(screen);
        when(screenGenerator.getWidth()).thenReturn(120);
        when(screen.newTextGraphics()).thenReturn(tg);
        gui = new LanternaGUI(screenGenerator, "Title");
    }

    private void press(int keyCode) {
        KeyEvent e = new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED);
        gui.getKeyAdapter().keyPressed(e);
    }

    private void release(int keyCode) {
        KeyEvent e = new KeyEvent(source, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED);
        gui.getKeyAdapter().keyReleased(e);
    }

    @Test
    public void getACTION_whenNoKeyPressed_returnsNULL() throws Exception {
        assertEquals(RescalableGUI.ACTION.NULL, gui.getACTION());
    }

    @Test
    public void spamKey_LEFT_keepsPriorityUntilReleased() throws Exception {
        press(VK_LEFT);

        assertEquals(RescalableGUI.ACTION.LEFT, gui.getACTION());
        assertEquals(RescalableGUI.ACTION.LEFT, gui.getACTION());

        release(VK_LEFT);
        assertEquals(RescalableGUI.ACTION.NULL, gui.getACTION());
    }

    @Test
    public void spamKey_RIGHT_keepsPriorityUntilReleased() throws Exception {
        press(VK_RIGHT);
        assertEquals(RescalableGUI.ACTION.RIGHT, gui.getACTION());
        assertEquals(RescalableGUI.ACTION.RIGHT, gui.getACTION());

        release(VK_RIGHT);
        assertEquals(RescalableGUI.ACTION.NULL, gui.getACTION());
    }

    @Test
    public void nonSpamKey_setsKeyPressedOnly_andReleaseRestoresPriority() throws Exception {
        press(VK_UP);
        assertEquals(RescalableGUI.ACTION.UP, gui.getACTION());

        assertEquals(RescalableGUI.ACTION.NULL, gui.getACTION());

        release(VK_UP);
        assertEquals(RescalableGUI.ACTION.NULL, gui.getACTION());
    }

    @Test
    public void getACTION_coversAllSwitchCases() throws Exception {
        press(VK_DOWN);
        assertEquals(RescalableGUI.ACTION.DOWN, gui.getACTION());

        press(VK_X);
        assertEquals(RescalableGUI.ACTION.DASH, gui.getACTION());

        press(VK_ESCAPE);
        assertEquals(RescalableGUI.ACTION.QUIT, gui.getACTION());

        press(VK_Q);
        assertEquals(RescalableGUI.ACTION.KILL, gui.getACTION());

        press(VK_ENTER);
        assertEquals(RescalableGUI.ACTION.SELECT, gui.getACTION());

        press(VK_SPACE);
        assertEquals(RescalableGUI.ACTION.JUMP, gui.getACTION());

        press(VK_A);
        assertEquals(RescalableGUI.ACTION.NULL, gui.getACTION());
    }

    @Test
    public void setResolutionScale_whenScreenAlreadyExists_closesOldScreen() throws Exception {
        RescalableGUI.ResolutionScale newScale = mock(RescalableGUI.ResolutionScale.class);

        gui.setResolutionScale(newScale);

        verify(screen, atLeastOnce()).close();
        verify(screenGenerator, atLeast(2)).createScreen(any(), anyString(), any(KeyAdapter.class));
    }

    @Test
    public void widthAndHeight_useScreenGeneratorWidth() {
        assertEquals(120, gui.getWidth());
        assertEquals(120, gui.getHeight());
    }

    @Test
    public void drawMethods_callTextGraphics() {
        gui.drawPixel(1, 2, new TextColor.RGB(1, 2, 3));
        verify(screen, atLeastOnce()).newTextGraphics();
        verify(tg, atLeastOnce()).setBackgroundColor(any(TextColor.RGB.class));
        verify(tg, atLeastOnce()).putString(anyInt(), anyInt(), anyString());

        gui.drawText(3, 4, new TextColor.RGB(4, 5, 6), "hi");
        verify(tg, atLeastOnce()).putString(3, 4, "hi");
    }

    @Test
    public void drawHitBox_and_drawRectangle_coverLoops() {
        gui.drawRectangle(0, 0, 2, 2, new TextColor.RGB(10, 10, 10));
        gui.drawHitBox(0, 0, 2, 2, new TextColor.RGB(10, 10, 10));

        gui.drawRectangle(0, 0, 0, 0, new TextColor.RGB(10, 10, 10));
        gui.drawHitBox(0, 0, 0, 0, new TextColor.RGB(10, 10, 10));

        verify(screen, atLeastOnce()).newTextGraphics();
    }

    @Test
    public void cls_flush_close_coverScreenCalls() throws Exception {
        gui.cls();
        verify(screen, atLeastOnce()).clear();

        gui.flush();
        verify(screen, atLeastOnce()).refresh();

        gui.close();
        verify(screen, atLeastOnce()).close();
    }
}
