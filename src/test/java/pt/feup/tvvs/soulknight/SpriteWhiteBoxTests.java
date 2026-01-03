package pt.feup.tvvs.soulknight;

import com.googlecode.lanterna.TextColor;
import org.junit.jupiter.api.Test;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpriteWhiteBoxTests {

    // ---------------- Unsafe allocate + set final field ----------------

    private static sun.misc.Unsafe unsafe() {
        try {
            Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (sun.misc.Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Sprite allocateSpriteWithoutCtor() {
        try {
            return (Sprite) unsafe().allocateInstance(Sprite.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setFinalField(Object obj, Object value) {
        try {
            Field f = obj.getClass().getDeclaredField("image");
            f.setAccessible(true);
            sun.misc.Unsafe u = unsafe();
            long off = u.objectFieldOffset(f);
            u.putObject(obj, off, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------- Constructor branch: resource == null triggers assert ----------------

    @Test
    void constructor_whenResourceMissing_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Sprite("definitely-not-a-real-file.png"));
    }

    // ---------------- draw() branches ----------------

    @Test
    void draw_skipsFullyTransparentPixels_andDrawsOpaquePixels_withCorrectColorAndCoords() {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);

        int transparent = 0x00000000; // alpha 0
        int opaqueRed   = 0xFFFF0000; // alpha 255, red 255

        img.setRGB(0, 0, transparent);
        img.setRGB(1, 0, opaqueRed);

        Sprite sprite = allocateSpriteWithoutCtor();
        setFinalField(sprite, img);

        GUI gui = mock(GUI.class);

        sprite.draw(gui, 10, 20);

        // opaque pixel drawn
        verify(gui, times(1)).drawPixel(eq(11), eq(20), eq(new TextColor.RGB(255, 0, 0)));
        // transparent pixel skipped
        verify(gui, never()).drawPixel(eq(10), eq(20), any());
    }

    @Test
    void draw_drawsNonTransparentPixel_extractsRGBCorrectly_forAnotherColor() {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        // alpha 255, RGB = (1,2,3)
        int argb = (0xFF << 24) | (0x01 << 16) | (0x02 << 8) | 0x03;
        img.setRGB(0, 0, argb);

        Sprite sprite = allocateSpriteWithoutCtor();
        setFinalField(sprite, img);

        GUI gui = mock(GUI.class);

        sprite.draw(gui, 4, 5);

        verify(gui).drawPixel(eq(4), eq(5), eq(new TextColor.RGB(1, 2, 3)));
    }
}
