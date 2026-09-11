package io.inf8ty.playerthemes.config.section;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public record CommandSettings(
        String mainCommand,
        List<String> aliases,
        CooldownSettings select,
        CooldownSettings selectOther
) {
    public record CooldownSettings(
            long cooldown,
            boolean cooldownBypass
    ) {
        public boolean enabled() {
            return cooldown != 0 && !(cooldown < 0);
        }

        public static CooldownSettings parseCooldown(ConfigurationSection section) {
            if (section == null) return new CooldownSettings(0L, false);

            return new CooldownSettings(
                    section.getLong("command-cooldown", 0L),
                    section.getBoolean("cooldown-bypass", true)
            );
        }
    }
}
