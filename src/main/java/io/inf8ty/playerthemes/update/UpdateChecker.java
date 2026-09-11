package io.inf8ty.playerthemes.update;

import io.inf8ty.playerthemes.PlayerThemes;
import io.inf8ty.playerthemes.color.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.Consumer;

public class UpdateChecker implements Listener {

    private final PlayerThemes plugin;

    private static final String LINK = "https://raw.githubusercontent.com/SuperCHIROK1/PlayerThemes/main/VERSION";
    public boolean updateAvailable = false;
    private String version;

    private TextComponent component;

    public UpdateChecker(PlayerThemes plugin) {
        this.plugin = plugin;
        this.component = new TextComponent(Text.legacy(" &8» &fСкачать: "));

        TextComponent spigotRu = new TextComponent(Text.legacy("&6&n[SpigotRU]&r "));
        spigotRu.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://spigotmc.ru/resources/4767/"));

        TextComponent spigotOrg = new TextComponent(Text.legacy("&e&n[SpigotORG]&r "));
        spigotOrg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/131023/"));

        TextComponent bm = new TextComponent(Text.legacy("&a&n[Black-Minecraft]&r"));
        bm.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://black-minecraft.com/resources/9951/"));

        this.component.addExtra(spigotRu);
        this.component.addExtra(spigotOrg);
        this.component.addExtra(bm);
    }

    public void check(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(LINK).openStream()))) {
                version = reader.readLine().trim();
                consumer.accept(version);
            } catch (IOException e) {
                plugin.getLogger().warning("Unable to check update: " + e.getMessage());
            }
        }, 20L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!updateAvailable) return;
        Player player = event.getPlayer();
        if (!player.hasPermission("playerthemes.notify") && !player.hasPermission("playerthemes.admin")) return;
        player.sendMessage("");
        player.sendMessage(Text.legacy(" &6&lPlayerThemes &8• &7Обновление доступно! &8• &c" + plugin.getDescription().getVersion() + " &7-> &a" + version));
        player.sendMessage(component);
        player.sendMessage("");
    }

}