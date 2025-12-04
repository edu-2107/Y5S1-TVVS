package pt.feup.tvvs.soulknight.view.sprites;

import java.io.IOException;

public interface SpriteLoader {
    Sprite get(String spriteFilepath) throws IOException;
}