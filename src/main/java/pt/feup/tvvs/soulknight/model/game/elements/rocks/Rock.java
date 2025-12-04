package pt.feup.tvvs.soulknight.model.game.elements.rocks;

import pt.feup.tvvs.soulknight.model.game.elements.Element;

public class Rock extends Element {
    private final char symbol;
    public Rock(int x, int y, char symbol) {
        super(x, y);
        this.symbol = symbol;
    }

    public char getChar() {return symbol;}
}

