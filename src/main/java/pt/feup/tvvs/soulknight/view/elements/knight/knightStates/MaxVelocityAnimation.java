package pt.feup.tvvs.soulknight.view.elements.knight.knightStates;

import pt.feup.tvvs.soulknight.model.game.elements.knight.MaxVelocityState;
import pt.feup.tvvs.soulknight.model.dataStructs.PairList;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MaxVelocityAnimation extends StateAnimation{
    public MaxVelocityAnimation(Class state, int frames) {
        super(state, frames);
    }

    @Override
    public void loadAnimation(String path) throws IOException {
        List<Sprite> maxVelocityRight = new ArrayList<>();
        List<Sprite> maxVelocityLeft = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            maxVelocityRight.add(new Sprite(path + "/movement/maxVelocity/frame-" + i + ".png"));
            maxVelocityLeft.add(new Sprite(path + "/movement/maxVelocity/frame-" + i + "-reversed.png"));
        }
        setState(MaxVelocityState.class);
        setAnimation(new PairList<>(maxVelocityRight, maxVelocityLeft));
        setFrames(10);
    }
}
