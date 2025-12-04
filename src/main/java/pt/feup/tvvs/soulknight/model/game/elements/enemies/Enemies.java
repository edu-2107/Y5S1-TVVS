package pt.feup.tvvs.soulknight.model.game.elements.enemies;

import pt.feup.tvvs.soulknight.model.dataStructs.Position;
import pt.feup.tvvs.soulknight.model.dataStructs.Vector;
import pt.feup.tvvs.soulknight.model.game.elements.Element;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

public abstract class Enemies extends Element {
    private Scene scene;
    private int damage;
    private Vector velocity;

    private Position size;

    public Enemies(int x, int y,int HP, Scene scene, int damage, Position size) {
        super(x,y);
        this.scene = scene;
        this.damage = damage;
        this.velocity = new Vector(1.5,1.5);
        this.size = size;
    }

    public abstract char getChar();

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public Scene getScene() {
        return scene;
    }

    public int getDamage() {
        return damage;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public abstract Position updatePosition();

    protected abstract Vector applyCollisions(Vector velocity);

    public abstract Position moveMonster();
    public Position getSize() {
        return size;
    }
}
