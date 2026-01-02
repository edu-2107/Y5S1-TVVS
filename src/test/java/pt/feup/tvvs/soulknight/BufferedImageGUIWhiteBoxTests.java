package pt.feup.tvvs.soulknight.gui;

import com.googlecode.lanterna.TextColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class BufferedImageGUIWhiteBoxTests {

    private BufferedImage buffer;
    private BufferedImageGUI gui;

    @BeforeEach
    void setUp() {
        // Use ARGB so we can observe alpha values if needed
        buffer = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        gui = new BufferedImageGUI(buffer);
    }

    @Test
    void getWidth_returnsBufferWidthight() {
        assertEquals(10, gui.getWidth());
    }

    @Test
    void getHeight_returnsBufferHeight() {
        assertEquals(10, gui.getHeight());
    }

    @Test
    void drawPixel_insideBounds_setsRGB() {
        TextColor.RGB c = new TextColor.RGB(255, 0, 0); // red
        gui.drawPixel(3, 4, c);

        int rgb = buffer.getRGB(3, 4);
        // red in ARGB is 0xFFFF0000 == -65536
        assertEquals(0xFFFF0000, rgb);
    }

    @Test
    void drawPixel_outsideBounds_doesNothing() {
        // Paint a known pixel so we can verify nothing changes
        buffer.setRGB(0, 0, 0xFF00FF00); // green

        TextColor.RGB c = new TextColor.RGB(255, 0, 0); // red
        gui.drawPixel(-1, 0, c);
        gui.drawPixel(0, -1, c);
        gui.drawPixel(10, 0, c);
        gui.drawPixel(0, 10, c);

        assertEquals(0xFF00FF00, buffer.getRGB(0, 0));
    }

    @Test
    void drawRectangle_fillsArea() {
        TextColor.RGB blue = new TextColor.RGB(0, 0, 255);
        gui.drawRectangle(2, 2, 3, 4, blue);

        assertEquals(0xFF0000FF, buffer.getRGB(2, 2));
        assertEquals(0xFF0000FF, buffer.getRGB(4, 5)); // bottom-right within fill
    }

    @Test
    void cls_withTransparentColor_doesNotOverwriteExistingPixels_dueToSrcOver() {
        // Put a red pixel
        buffer.setRGB(1, 1, 0xFFFF0000);

        gui.cls();

        // With default Graphics2D composite (SRC_OVER), painting fully transparent
        // does NOT clear/overwrite existing pixels -> red remains.
        assertEquals(0xFFFF0000, buffer.getRGB(1, 1));
    }

    @Test
    void flush_isNoOp() {
        assertDoesNotThrow(() -> gui.flush());
    }

    @Test
    void close_isNoOp() {
        assertDoesNotThrow(() -> gui.close());
    }

    @Test
    void unsupportedMethods_throwUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> gui.getACTION());
        assertThrows(UnsupportedOperationException.class, () -> gui.getGUI());
        assertThrows(UnsupportedOperationException.class, () -> gui.getFPS());
        assertThrows(UnsupportedOperationException.class, () -> gui.setFPS(60));
        assertThrows(UnsupportedOperationException.class, () -> gui.drawHitBox(0, 0, 1, 1, new TextColor.RGB(1, 2, 3)));
    }

    @Test
    void drawText_doesNotThrow() {
        assertDoesNotThrow(() ->
                gui.drawText(1, 1, new TextColor.RGB(255, 255, 255), "Hi")
        );
    }
}
