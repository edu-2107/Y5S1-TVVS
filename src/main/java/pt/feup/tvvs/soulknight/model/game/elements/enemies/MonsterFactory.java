package pt.feup.tvvs.soulknight.model.game.elements.enemies;


import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

public class MonsterFactory {
    public static final char SWORD_MONSTER = 'E';
    public static final char GHOST_MONSTER = 'm';
    public static final char PURPLE_MONSTER = 'l';

    public static Enemies createMonster(int x, int y, Scene scene, char type) {
        switch (type) {
            case SWORD_MONSTER:
                return new SwordMonster(x, y, 10, scene, 20, new Position(8, 8), SWORD_MONSTER);
            case GHOST_MONSTER:
                return new GhostMonster(x, y, 1, scene, 10, new Position(2, 2), GHOST_MONSTER);
            case PURPLE_MONSTER:
                return new PurpleMonster(x, y, 25, scene, 5, new Position(8, 9), PURPLE_MONSTER);
            default:
                return null;
        }
    }
}
