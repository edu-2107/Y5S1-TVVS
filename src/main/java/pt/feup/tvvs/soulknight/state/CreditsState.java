package pt.feup.tvvs.soulknight.state;

import pt.feup.tvvs.soulknight.controller.Controller;
import pt.feup.tvvs.soulknight.controller.credits.CreditsController;
import pt.feup.tvvs.soulknight.model.credits.Credits;
import pt.feup.tvvs.soulknight.view.sprites.SpriteLoader;
import pt.feup.tvvs.soulknight.view.sprites.ViewerProvider;
import pt.feup.tvvs.soulknight.view.states.CreditsViewer;
import pt.feup.tvvs.soulknight.view.states.ScreenViewer;

import java.io.IOException;

public class CreditsState extends State<Credits>{
    public CreditsState(Credits model, SpriteLoader spriteLoader) throws IOException {
        super(model, spriteLoader);
    }

    @Override
    protected ScreenViewer<Credits> createScreenViewer(ViewerProvider viewerProvider) {
        return new CreditsViewer(getModel(), viewerProvider);
    }

    @Override
    protected Controller<Credits> createController() {
        return new CreditsController(getModel());
    }
}
