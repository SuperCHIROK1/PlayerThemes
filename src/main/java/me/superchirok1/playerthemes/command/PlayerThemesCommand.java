package me.superchirok1.playerthemes.command;

import me.superchirok1.playerthemes.PlayerThemes;
import me.superchirok1.playerthemes.config.values.MessagesValues;
import me.superchirok1.playerthemes.service.ThemeService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlayerThemesCommand implements CommandExecutor, TabCompleter {

    private final PlayerThemes pl;

    public PlayerThemesCommand(PlayerThemes pl) {
        this.pl = pl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        ThemeService service = pl.service;
        MessagesValues msgs = pl.localeProvider.get;

        if (args.length == 0) {
            sender.sendMessage(msgs.commandSelectUsage());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "select": {
                if (args.length >= 2) {

                    if (args.length >= 3) {

                        String theme = args[1].toLowerCase();
                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);

                        if (!sender.hasPermission(pl.config.get.permInstallOther())) {
                            sender.sendMessage(msgs.noPerms());
                            return true;
                        }

                        if (service.hasTheme(theme)) {
                            service.set(player.getUniqueId(), theme);
                            sender.sendMessage(String.format(msgs.commandThemeInstalled(), theme));
                        } else {
                            sender.sendMessage(String.format(msgs.commandThemeNotFound(), theme));
                        }

                        return true;

                    }

                    if (!(sender instanceof Player)) {
                        sender.sendMessage(msgs.commandPlayerOnly());
                        return true;
                    }

                    Player player = (Player) sender;
                    String theme = args[1].toLowerCase();

                    if (!player.hasPermission(pl.config.get.permInstall())) {
                        player.sendMessage(msgs.noPerms());
                        return true;
                    }

                    if (!service.hasPermissionToUse(player, theme)) {
                        player.sendMessage(msgs.commandThemeNoPerms());
                        return true;
                    }

                    if (service.hasTheme(theme)) {
                        service.set(player.getUniqueId(), theme);
                        player.sendMessage(String.format(msgs.commandThemeInstalled(), theme));
                    } else {
                        player.sendMessage(String.format(msgs.commandThemeNotFound(), theme));
                    }

                    return true;
                } else {
                    sender.sendMessage(msgs.commandSelectUsage());
                    return true;
                }
            }
            case "reload": {

                if (!sender.hasPermission(pl.config.get.permReload())) {
                    sender.sendMessage(msgs.noPerms());
                    return true;
                }

                pl.reload();
                sender.sendMessage(msgs.adminReloaded());
                return true;

            }
            default: {

                return true;

            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        boolean select = sender.hasPermission(pl.config.get.permInstall())
                || sender.hasPermission(pl.config.get.permInstallOther());

        if (args.length == 1) {
            List<String> list = new ArrayList<>();

            if (sender.hasPermission(pl.config.get.permReload())) {
                list.add("reload");
            }
            if (select) {
                list.add("select");
            }
            return list;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("select") && select) {
            return new ArrayList<>(pl.config.get.themes().getKeys(false));
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("select")
                && sender.hasPermission(pl.config.get.permInstallOther())) {

            return pl.getServer().getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .toList();
        }

        return List.of();

    }
}
