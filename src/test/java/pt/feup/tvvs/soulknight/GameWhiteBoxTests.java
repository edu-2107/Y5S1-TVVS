package pt.feup.tvvs.soulknight;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pt.feup.tvvs.soulknight.gui.LanternaGUI;
import pt.feup.tvvs.soulknight.sound.MenuSoundPlayer;
import pt.feup.tvvs.soulknight.state.State;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameWhiteBoxTests {

    // ---------- helpers: Unsafe allocate + set private/final fields ----------

    private static sun.misc.Unsafe unsafe() {
        try {
            Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (sun.misc.Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object allocate() {
        try {
            return unsafe().allocateInstance(Game.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setField(Object obj, String fieldName, Object value) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);

            // set even if final, via Unsafe to avoid Java module/final restrictions
            sun.misc.Unsafe u = unsafe();
            long off = u.objectFieldOffset(f);
            u.putObject(obj, off, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setLongField(Object obj, long value) {
        try {
            Field f = obj.getClass().getDeclaredField("fpsLastUpdate");
            f.setAccessible(true);

            sun.misc.Unsafe u = unsafe();
            long off = u.objectFieldOffset(f);
            u.putLong(obj, off, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setIntField(Object obj, String fieldName) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);

            sun.misc.Unsafe u = unsafe();
            long off = u.objectFieldOffset(f);
            u.putInt(obj, off, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static long getLongField(Object obj) {
        try {
            Field f = obj.getClass().getDeclaredField("fpsLastUpdate");
            f.setAccessible(true);
            return (long) f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int getIntField(Object obj, String fieldName) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return (int) f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void invokeStart(Game game) {
        try {
            Method m = Game.class.getDeclaredMethod("start");
            m.setAccessible(true);
            m.invoke(game);
        } catch (Exception e) {
            // unwrap if needed
            Throwable t = e.getCause() != null ? e.getCause() : e;
            throw new RuntimeException(t);
        }
    }

    // ---------- build a Game instance without running the real constructor ----------

    private static Game makeGameWithMocks(State<?> stateMock, LanternaGUI guiMock, MenuSoundPlayer soundMock, SpriteLoader spriteMock) {
        Game g = (Game) allocate();

        setField(g, "state", stateMock);
        setField(g, "gui", guiMock);
        setField(g, "menuSoundPlayer", soundMock);
        setField(g, "spriteLoader", spriteMock);

        // init fps fields to deterministic values
        setLongField(g, System.currentTimeMillis());
        setIntField(g, "frames");
        setIntField(g, "currentFps");

        return g;
    }

    // ---------- tests for start(): cover ALL branches ----------

    @Test
    void start_whenStateNull_skipsLoop_andClosesGui() throws Exception {
        LanternaGUI gui = mock(LanternaGUI.class);
        MenuSoundPlayer sound = mock(MenuSoundPlayer.class);
        SpriteLoader sprites = mock(SpriteLoader.class);

        Game game = makeGameWithMocks(null, gui, sound, sprites);

        invokeStart(game);

        verify(sound, times(1)).start();
        verify(gui, times(1)).close();
        verify(gui, never()).setFPS(anyInt());
    }

    @Test
    void start_oneIteration_tick0Branch_runsMove_setsFPS_andExitsWhenStateBecomesNull() throws Exception {
        LanternaGUI gui = mock(LanternaGUI.class);
        MenuSoundPlayer sound = mock(MenuSoundPlayer.class);
        SpriteLoader sprites = mock(SpriteLoader.class);

        @SuppressWarnings("unchecked")
        State<Object> state = mock(State.class);

        // on first move, end the game loop
        doAnswer(inv -> {
            Game g = inv.getArgument(0);

            Field f = Game.class.getDeclaredField("state");
            f.setAccessible(true);
            f.set(g, null);

            return null;
        }).when(state).move(any(Game.class), any(), anyLong());


        Game game = makeGameWithMocks(state, gui, sound, sprites);

        invokeStart(game);

        verify(sound, times(1)).start();

        ArgumentCaptor<Long> tickCap = ArgumentCaptor.forClass(Long.class);
        verify(state, times(1)).move(eq(game), eq(gui), tickCap.capture());
        assertEquals(0L, tickCap.getValue());


        verify(gui, atLeastOnce()).setFPS(anyInt());
        verify(gui, times(1)).close();
    }

    @Test
    void start_fpsUpdateBranch_true_whenFpsLastUpdateOld_updatesCurrentFps_andResetsFrames() throws Exception {
        LanternaGUI gui = mock(LanternaGUI.class);
        MenuSoundPlayer sound = mock(MenuSoundPlayer.class);
        SpriteLoader sprites = mock(SpriteLoader.class);

        @SuppressWarnings("unchecked")
        State<Object> state = mock(State.class);

        doAnswer(inv -> {
            Game g = inv.getArgument(0);

            Field f = Game.class.getDeclaredField("state");
            f.setAccessible(true);
            f.set(g, null);

            return null;
        }).when(state).move(any(Game.class), any(), anyLong());


        Game game = makeGameWithMocks(state, gui, sound, sprites);

        // force (currentTime - fpsLastUpdate >= 1000) to be TRUE
        setLongField(game, 0L);
        setIntField(game, "frames");
        setIntField(game, "currentFps");

        invokeStart(game);

        // after 1 frame, currentFps should become 1 and frames reset to 0
        assertEquals(1, getIntField(game, "currentFps"));
        assertEquals(0, getIntField(game, "frames"));
        assertTrue(getLongField(game) > 0);

        verify(gui, atLeastOnce()).setFPS(anyInt());
        verify(gui, times(1)).close();
    }

}
