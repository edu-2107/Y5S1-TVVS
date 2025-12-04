package pt.feup.tvvs.soulknight.view.elements.knight;

import pt.feup.tvvs.soulknight.gui.GUI;

import pt.feup.tvvs.soulknight.model.game.elements.knight.*;
import pt.feup.tvvs.soulknight.view.elements.ElementViewer;
import pt.feup.tvvs.soulknight.view.elements.knight.knightStates.*;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class KnightViewer implements ElementViewer<Knight> {

    private final List<StateAnimation> animations;

    public KnightViewer(SpriteLoader spriteLoader) throws IOException {
        this.animations = new ArrayList<>();

        // Initialize each animation state
        animations.add(new DashAnimation(DashState.class, 10));
        animations.add(new AfterDashAnimation(AfterDashState.class, 5));
        animations.add(new DamagedAnimation(DamagedState.class, 10));
        animations.add(new IdleAnimation(IdleState.class, 8));
        animations.add(new MaxVelocityAnimation(MaxVelocityState.class, 10));
        animations.add(new RunningAnimation(RunningState.class, 5));
        animations.add(new WalkingAnimation(WalkingState.class, 5));
        animations.add(new FallingAnimation(FallingState.class, 10));
        animations.add(new JumpAnimation(JumpState.class, 6));
        animations.add(new RespawnAnimation(RespawnState.class, 0)); // No animation
        // Load animations for each state

        for (StateAnimation animation : animations) {
            animation.loadAnimation("sprites/Knight"); // Base path to sprites
        }
    }

    @Override
    public void draw(Knight model, GUI gui, long time, int offsetX, int offsetY) throws IOException {
        StateAnimation animation = findAnimationForState(model.getState().getClass());
        if (animation != null) {
            Sprite sprite = animation.getSprite(time, model.isFacingRight());
            if (sprite != null) {
                int offSetX = 4;
                int offSetY = 1;
                sprite.draw(gui, (int) model.getPosition().x() - offSetX, (int) model.getPosition().y() - offSetY);
            }
        }
    }

    private StateAnimation findAnimationForState(Class<?> stateClass) {
        for (StateAnimation animation : animations) {
            if (animation.getState() == stateClass) {
                return animation;
            }
        }
        return null;
    }

}
