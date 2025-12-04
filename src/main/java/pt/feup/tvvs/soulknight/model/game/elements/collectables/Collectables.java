package pt.feup.tvvs.soulknight.model.game.elements.collectables;

import pt.feup.tvvs.soulknight.model.game.elements.Element;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;

public abstract class Collectables extends Element {

    public Collectables(int x, int y){
        super(x, y);
    }
    public abstract void benefit(Knight knight);

    public abstract char getChar();
}
