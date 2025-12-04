package pt.feup.tvvs.soulknight.view.sprites;

import pt.feup.tvvs.soulknight.view.elements.particle.ParticleViewer;
import pt.feup.tvvs.soulknight.view.elements.spike.SpikeViewer;
import pt.feup.tvvs.soulknight.view.elements.collectables.OrbViewer;
import pt.feup.tvvs.soulknight.view.elements.knight.KnightViewer;
import pt.feup.tvvs.soulknight.view.elements.monsters.MonsterViewer;
import pt.feup.tvvs.soulknight.view.elements.rocks.RockViewer;
import pt.feup.tvvs.soulknight.view.elements.tile.TileViewer;
import pt.feup.tvvs.soulknight.view.elements.tree.TreeViewer;
import pt.feup.tvvs.soulknight.view.menu.LogoViewer;
import pt.feup.tvvs.soulknight.view.menu.OptionViewer;
import pt.feup.tvvs.soulknight.view.text.GameTextViewer;
import pt.feup.tvvs.soulknight.view.text.TextViewer;

import java.io.IOException;

public class ViewerProvider {
    private final ParticleViewer particleViewer;
    private final KnightViewer playerViewer;
    private final SpikeViewer spikeViewer;
    private final OrbViewer orbViewer;
    private final TreeViewer treeViewer;
    private final RockViewer rockViewer;
    private final TileViewer tileViewer;
    private final MonsterViewer monsterViewer;
    private final TextViewer textViewer;
    private final OptionViewer entryViewer;
    private final LogoViewer logoViewer;

    public ViewerProvider(SpriteLoader spriteLoader) throws IOException {
        this.particleViewer = new ParticleViewer();
        this.playerViewer = new KnightViewer(spriteLoader);
        this.spikeViewer = new SpikeViewer(spriteLoader);
        this.treeViewer = new TreeViewer(spriteLoader);
        this.rockViewer = new RockViewer(spriteLoader);
        this.orbViewer = new OrbViewer(spriteLoader);
        this.tileViewer = new TileViewer(spriteLoader);
        this.monsterViewer = new MonsterViewer(spriteLoader);
        this.textViewer = new GameTextViewer();
        this.entryViewer = new OptionViewer(textViewer);
        this.logoViewer = new LogoViewer(spriteLoader);
    }

    public ParticleViewer getParticleViewer() {
        return particleViewer;
    }

    public KnightViewer getPlayerViewer() {
        return playerViewer;
    }

    public SpikeViewer getSpikeViewer() {
        return spikeViewer;
    }

    public TreeViewer getTreeViewer() {
        return treeViewer;
    }
    public OrbViewer getOrbViewer(){return orbViewer;}
    public RockViewer getRockViewer(){return rockViewer;}
    public TileViewer getTileViewer() {
        return tileViewer;
    }

    public TextViewer getTextViewer() {
        return textViewer;
    }

    public OptionViewer getEntryViewer() {
        return entryViewer;
    }

    public LogoViewer getLogoViewer() {
        return logoViewer;
    }

    public MonsterViewer getMonsterViewer() {return monsterViewer;}
}