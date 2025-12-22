package me.superchirok1.playerthemes.manager.color;

import me.superchirok1.playerthemes.config.Config;
import me.superchirok1.playerthemes.manager.color.impl.LegacyColorizer;
import me.superchirok1.playerthemes.manager.color.impl.MiniMessageColorizer;

public class TextColorizer {

    public Colorizer get;

    public void init(Config config) {
        get = config.get.colorizer()
                .equalsIgnoreCase("legacy")
                ? new LegacyColorizer() : new MiniMessageColorizer();
    }

}
