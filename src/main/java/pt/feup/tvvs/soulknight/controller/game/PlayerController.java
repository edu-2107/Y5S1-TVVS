package pt.feup.tvvs.soulknight.controller.game;

import pt.feup.tvvs.soulknight.Game;
import pt.feup.tvvs.soulknight.controller.Controller;
import pt.feup.tvvs.soulknight.gui.GUI;
import pt.feup.tvvs.soulknight.model.game.elements.knight.IdleState;
import pt.feup.tvvs.soulknight.model.game.elements.knight.Knight;
import pt.feup.tvvs.soulknight.model.game.elements.knight.RespawnState;
import pt.feup.tvvs.soulknight.model.game.scene.Scene;

import java.io.IOException;

public class PlayerController extends Controller<Scene> {
    public PlayerController(Scene scene) {
        super(scene);
    }

    @Override
    public void move(Game game, GUI.ACTION action, long time) throws IOException {
        Knight knight = getModel().getPlayer();

        switch (action) {
            case LEFT:
                knight.setVelocity(knight.moveLeft());
                knight.setFacingRight(false);
                break;
            case RIGHT:
                knight.setVelocity(knight.moveRight());
                knight.setFacingRight(true);
                break;
            case JUMP:
                knight.setVelocity(knight.jump());
                break;
            case DASH:
                knight.setVelocity(knight.dash());
                break;
            case KILL:
                knight.setState(new RespawnState(knight, 30));
                break;
            default:
                knight.setVelocity(knight.updateVelocity());
        }

        knight.setPosition(knight.updatePosition());
        knight.setScene(getModel());
        knight.setState(knight.getNextState());

        if (knight.getState() == null) {
            knight.setState(new IdleState(knight));
        }
    }

}
