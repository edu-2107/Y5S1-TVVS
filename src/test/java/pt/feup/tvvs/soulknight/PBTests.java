package pt.feup.tvvs.soulknight;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import pt.feup.tvvs.soulknight.model.dataStructs.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PBTests {

    private static final int MIN_COORD = -1_000;
    private static final int MAX_COORD =  1_000;

    @Property
    void movingLeftThenRightReturnsToOriginal(
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int x,
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int y
    ) {
        Position start = new Position(x, y);

        Position result = start.getLeft().getRight();

        assertEquals(start, result);
    }

    @Property
    void movingRightThenLeftReturnsToOriginal(
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int x,
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int y
    ) {
        Position start = new Position(x, y);

        Position result = start.getRight().getLeft();

        assertEquals(start, result);
    }

    @Property
    void movingUpThenDownReturnsToOriginal(
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int x,
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int y
    ) {
        Position start = new Position(x, y);

        Position result = start.getUp().getDown();

        assertEquals(start, result);
    }

    @Property
    void movingDownThenUpReturnsToOriginal(
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int x,
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int y
    ) {
        Position start = new Position(x, y);

        Position result = start.getDown().getUp();

        assertEquals(start, result);
    }

    @Property
    void movingLeftDecreasesXByOneAndKeepsY(
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int x,
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int y
    ) {
        Position start = new Position(x, y);

        Position left = start.getLeft();

        assertEquals(new Position(x - 1, y), left);
    }

    @Property
    void movingUpDecreasesYByOneAndKeepsX(
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int x,
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int y
    ) {
        Position start = new Position(x, y);

        Position up = start.getUp();

        assertEquals(new Position(x, y - 1), up);
    }

    @Property
    void movingRightIncreasesXByOneAndKeepsY(
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int x,
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int y
    ) {
        Position start = new Position(x, y);

        Position right = start.getRight();

        assertEquals(new Position(x + 1, y), right);
    }

    @Property
    void movingDownIncreasesYByOneAndKeepsX(
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int x,
            @ForAll @IntRange(min = MIN_COORD, max = MAX_COORD) int y
    ) {
        Position start = new Position(x, y);

        Position down = start.getDown();

        assertEquals(new Position(x, y + 1), down);
    }
}
