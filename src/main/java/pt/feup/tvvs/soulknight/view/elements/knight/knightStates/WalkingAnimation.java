package pt.feup.tvvs.soulknight.view.elements.knight.knightStates;

import pt.feup.tvvs.soulknight.model.game.elements.knight.WalkingState;
import pt.feup.tvvs.soulknight.model.dataStructs.PairList;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WalkingAnimation extends StateAnimation {
    public WalkingAnimation(Class state, int frames) {
        super(state, frames);
    }

    @Override
    public void loadAnimation(String path) throws IOException {
        List<Sprite> walkingSpriteRight = new ArrayList<>();
        List<Sprite> walkingSpriteLeft = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            walkingSpriteLeft.add(new Sprite(path + "/movement/walking/running-intermediate-" + i + ".png"));
            walkingSpriteRight.add(new Sprite(path + "/movement/walking/running-intermediate-" + i + "-reversed.png"));
        }
        setState(WalkingState.class);
        setAnimation(new PairList<>(walkingSpriteLeft, walkingSpriteRight));
        setFrames(5);
    }
}
