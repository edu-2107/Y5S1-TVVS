package pt.feup.tvvs.soulknight.view.elements.knight.knightStates;

import pt.feup.tvvs.soulknight.model.game.elements.knight.AfterDashState;
import pt.feup.tvvs.soulknight.model.dataStructs.PairList;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AfterDashAnimation extends StateAnimation{
    public AfterDashAnimation(Class state, int frames) {
        super(state, frames);
    }

    @Override
    public void loadAnimation(String path) throws IOException {
        List<Sprite> DashRight = new ArrayList<>();
        List<Sprite> DashLeft = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            DashRight.add(new Sprite(path + "/movement/dashing/dashing-" + i + ".png"));
            DashLeft.add(new Sprite(path + "/movement/dashing/dashing-" + i + "-reversed.png"));
        }
        setState(AfterDashState.class);
        setAnimation(new PairList<>(DashRight, DashLeft));
        setFrames(10);
    }
}
