package pt.feup.tvvs.soulknight.view.menu;

import pt.feup.tvvs.soulknight.gui.RescalableGUI;
import pt.feup.tvvs.soulknight.model.menu.Option;
import pt.feup.tvvs.soulknight.view.text.TextViewer;
import com.googlecode.lanterna.TextColor;

public class OptionViewer {
    private final TextViewer textViewer;

    public OptionViewer(TextViewer textViewer) {
        this.textViewer = textViewer;
    }

    public void draw(Option model, RescalableGUI gui, TextColor color) {
        String OptionText = null;
        switch (model.getType()) {
            case START_GAME:
                OptionText = "Start";
                break;
            case SETTINGS:
                OptionText = "Settings";
                break;
            case EXIT:
                OptionText = "Exit";
                break;
            case RESOLUTION:
                OptionText = getResolutionLabel(gui);
                break;
            case TO_MAIN_MENU:
                OptionText = "Go Back";
                break;
        }
        textViewer.draw(OptionText, (int) model.getPosition().x(), (int) model.getPosition().y(), (TextColor.RGB) color, gui);
    }

    private String getResolutionLabel(RescalableGUI gui) {
        final RescalableGUI.ResolutionScale[] resolutions = RescalableGUI.ResolutionScale.values();
        if (gui.getResolutionScale() == null)
            return "Resolution:   Automatic >";

        return String.format(
                "Resolution: < %dX%d %c ",
                gui.getResolutionScale().getWidth(),
                gui.getResolutionScale().getHeight(),
                resolutions[resolutions.length - 1] == gui.getResolutionScale() ? ' ' : '>'
        );
    }
}