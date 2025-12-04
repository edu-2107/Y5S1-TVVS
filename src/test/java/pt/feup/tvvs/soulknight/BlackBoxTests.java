package pt.feup.tvvs.soulknight;

import pt.feup.tvvs.soulknight.controller.menu.ParticleMenuController;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.gui.BufferedImageGUI;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.googlecode.lanterna.TextColor;

import static org.junit.jupiter.api.Assertions.*;

class BlackBoxTests {

    private final ParticleMenuController controller = new ParticleMenuController(null);

    private BufferedImage createBuffer() {
        return new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
    }

    private int[] copyPixels(BufferedImage buffer) {
        return buffer.getRGB(0, 0, buffer.getWidth(), buffer.getHeight(),
                null, 0, buffer.getWidth());
    }

    // ---------- wrapPosition ----------

    @Test
    void wrapPositionKeepsXInsideBoundsWhenXIsValid() {
        Position result = controller.wrapPosition(100, 50);

        assertEquals(new Position(100, 50), result);
    }

    @Test
    void wrapPositionXAtLowerBoundZeroStaysZero() {
        Position result = controller.wrapPosition(0, 10);

        assertEquals(new Position(0, 10), result);
    }

    @Test
    void wrapPositionXJustAboveLowerBoundOneStaysOne() {
        Position result = controller.wrapPosition(1, 10);

        assertEquals(new Position(1, 10), result);
    }

    @Test
    void wrapPositionXJustBelowLowerBoundWrapsToMaxMinusOne() {
        Position result = controller.wrapPosition(-1, 10);

        assertEquals(new Position(219, 10), result);
    }

    @Test
    void wrapPositionXAtUpperBoundWrapsToOne() {
        Position result = controller.wrapPosition(220, 10);

        assertEquals(new Position(1, 10), result);
    }

    @Test
    void wrapPositionYAtLowerBoundZeroStaysZero() {
        Position result = controller.wrapPosition(10, 0);

        assertEquals(new Position(10, 0), result);
    }

    @Test
    void wrapPositionYJustBelowLowerBoundWrapsToMaxMinusOne() {
        Position result = controller.wrapPosition(10, -1);

        assertEquals(new Position(10, 109), result);
    }

    @Test
    void wrapPositionYAtUpperBoundWrapsToOne() {
        Position result = controller.wrapPosition(10, 110);

        assertEquals(new Position(10, 1), result);
    }

    @Test
    void wrapPositionWrapsBothCoordinatesWhenBothOutOfBoundsCase1() {
        Position result = controller.wrapPosition(-10, 1100);

        assertEquals(new Position(219, 1), result);
    }

    @Test
    void wrapPositionWrapsBothCoordinatesWhenBothOutOfBoundsCase2() {
        Position result = controller.wrapPosition(300, -1);

        assertEquals(new Position(1, 109), result);
    }

    // ---------- drawText ----------

    @Test
    void drawTextWithValidArgumentModifiesBuffer() {
        BufferedImage buffer = createBuffer();
        BufferedImageGUI gui = new BufferedImageGUI(buffer);
        TextColor.RGB color = new TextColor.RGB(255, 0, 0);

        int[] before = copyPixels(buffer);

        gui.drawText(10, 20, color, "Hollow Knight");

        int[] after = copyPixels(buffer);

        assertFalse(Arrays.equals(before, after),
                "Buffer should change when drawing non-empty text inside bounds");
    }

    @Test
    void drawTextWithEmptyStringDoesNotModifyBuffer() {
        BufferedImage buffer = createBuffer();
        BufferedImageGUI gui = new BufferedImageGUI(buffer);
        TextColor.RGB color = new TextColor.RGB(255, 0, 0);

        int[] before = copyPixels(buffer);

        gui.drawText(10, 20, color, "");

        int[] after = copyPixels(buffer);

        assertArrayEquals(before, after,
                "Drawing empty string should not change the buffer");
    }

    @Test
    void drawTextWithCoordinatesFarOutsideImageDoesNotModifyBuffer() {
        BufferedImage buffer = createBuffer();
        BufferedImageGUI gui = new BufferedImageGUI(buffer);
        TextColor.RGB color = new TextColor.RGB(0, 255, 0);

        int[] before = copyPixels(buffer);

        gui.drawText(-1000, -1000, color, "Off");
        gui.drawText(1000, 1000, color, "Screen");

        int[] after = copyPixels(buffer);

        assertArrayEquals(before, after,
                "Drawing text completely outside the image should not change the buffer");
    }

    @Test
    void drawTextWithNullTextThrowsNullPointerException() {
        BufferedImage buffer = createBuffer();
        BufferedImageGUI gui = new BufferedImageGUI(buffer);
        TextColor.RGB color = new TextColor.RGB(255, 255, 255);

        assertThrows(NullPointerException.class,
                () -> gui.drawText(10, 10, color, null));
    }

    @Test
    void drawTextWithNullColorThrowsNullPointerException() {
        BufferedImage buffer = createBuffer();
        BufferedImageGUI gui = new BufferedImageGUI(buffer);

        assertThrows(NullPointerException.class,
                () -> gui.drawText(10, 10, null, "Hollow Knight"));
    }
}
