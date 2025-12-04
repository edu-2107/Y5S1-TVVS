package pt.feup.tvvs.soulknight.view.elements.knight.knightStates;

import pt.feup.tvvs.soulknight.model.dataStructs.PairList;

public class RespawnAnimation extends StateAnimation{
    public RespawnAnimation(Class state, int frames) {
        super(state, frames);
    }

    @Override
    public void loadAnimation(String path) {
        setState(RunningAnimation.class);
        setAnimation(new PairList<>(null, null));
        setFrames(1);
    }
}
