package pt.feup.tvvs.soulknight.view.elements.knight.knightStates;

import pt.feup.tvvs.soulknight.model.game.elements.knight.DamagedState;
import pt.feup.tvvs.soulknight.model.dataStructs.PairList;
import pt.feup.tvvs.soulknight.view.sprites.Sprite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DamagedAnimation extends StateAnimation{
    public DamagedAnimation(Class state, int frames) {
        super(state, frames);
    }

    @Override
    public void loadAnimation(String path) throws IOException {
        List<Sprite> DamagedRight = new ArrayList<>();
        List<Sprite> DamagedLeft = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            DamagedRight.add(new Sprite(path + "/damaged/movement/falling/damaged-falling-"+i + ".png"));
            DamagedLeft.add(new Sprite(path + "/damaged/movement/falling/damaged-falling-"+i+"-reversed.png"));
        }
        setState(DamagedState.class);
        setAnimation(new PairList<>(DamagedRight, DamagedLeft));
        setFrames(4);
    }
}
