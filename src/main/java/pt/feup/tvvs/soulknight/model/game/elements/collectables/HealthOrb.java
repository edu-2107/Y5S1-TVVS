package pt.feup.tvvs.soulknight.model.game.elements.collectables;

import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;

public class HealthOrb extends Collectables{
    private int health;
    private final char symbol;
    public HealthOrb(int x, int y, int health, char symbol){
        super(x,y);
        this.health = health;
        this.symbol = symbol;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public void benefit(Knight knight){
        knight.setHP(knight.getHP()+ this.health);
    }

    @Override
    public char getChar() {
        return symbol;
    }
}
