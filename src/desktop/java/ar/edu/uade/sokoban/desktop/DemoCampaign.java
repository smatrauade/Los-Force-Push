package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.session.LevelCatalog;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/** Shipped, ordered TXT references shared by the menu and acceptance tests. */
final class DemoCampaign {
    private DemoCampaign() { }
    static LevelCatalog catalog(Path levels) {
        Objects.requireNonNull(levels, "levels");
        return new LevelCatalog(List.of(
                new LevelCatalog.Entry("demo", levels.resolve("demo.txt"), LevelCatalog.Format.STANDARD),
                new LevelCatalog.Entry("campaign_b", levels.resolve("campaign-b.txt"), LevelCatalog.Format.EXTENDED),
                new LevelCatalog.Entry("ice", levels.resolve("ice_demo.txt"), LevelCatalog.Format.STANDARD),
                new LevelCatalog.Entry("fragile", levels.resolve("fragile_demo.txt"), LevelCatalog.Format.EXPERIMENTAL_FRAGILE),
                new LevelCatalog.Entry("fragile_ice", levels.resolve("fragile-ice-demo.txt"), LevelCatalog.Format.EXPERIMENTAL_FRAGILE)));
    }
}
