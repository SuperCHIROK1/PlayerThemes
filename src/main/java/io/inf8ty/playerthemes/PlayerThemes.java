package io.inf8ty.playerthemes;

import io.inf8ty.playerthemes.command.AdminCommand;
import io.inf8ty.playerthemes.command.ThemesCommand;
import io.inf8ty.playerthemes.config.MainConfig;
import io.inf8ty.playerthemes.config.section.DatabaseSettings;
import io.inf8ty.playerthemes.database.AbstractDatabase;
import io.inf8ty.playerthemes.database.impl.MySQLDatabase;
import io.inf8ty.playerthemes.database.impl.SQLiteDatabase;
import io.inf8ty.playerthemes.database.redis.RedisManager;
import io.inf8ty.playerthemes.theme.ThemeManager;
import io.inf8ty.playerthemes.color.Text;
import io.inf8ty.playerthemes.update.UpdateChecker;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;

@Getter
@Accessors(fluent = true)
public final class PlayerThemes extends JavaPlugin {

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private final MainConfig config = new MainConfig(this);
    private final ThemeManager themeManager = new ThemeManager(this);
    private RedisManager redisManager;
    private AbstractDatabase database;

    private final UpdateChecker updateChecker = new UpdateChecker(this);

    private boolean isFirstLaunch;

    @Override
    public void onEnable() {
        isFirstLaunch = !getDataFolder().exists();

        saveDefaultConfig();
        start();
        setupCommands();

        sendConsole(" ");
        sendConsole("&b» &7Плагин &6PlayerThemes &7включен!");
        sendConsole("&b» &7Версия: &a" + getDescription().getVersion());
        sendConsole(" ");
        sendConsole("&7- Загружено тем: &a" + this.themeManager.themes().size() + " &8| &7БД: &a" + this.config.database().type());
        sendConsole("&7- Записи игроков: &a" + this.themeManager.players().size());
        sendConsole(" ");

        new PlaceholderExpansion(this).register();

        Bukkit.getPluginManager().registerEvents(updateChecker, this);
        checkForUpdates();

        if (isFirstLaunch) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                sendConsole("&8==================================================");
                sendConsole("&6PlayerThemes &7| &eОбнаружен первый запуск!");
                sendConsole("");
                sendConsole("&fСпасибо, что выбрали наш плагин!");
                sendConsole("&fБудем благодарны, если вы оставите &aотзыв &fна странице плагина.");
                sendConsole("&fНужна помощь? Наш Discord: &bhttps://dsc.gg/amazingplugins");
                sendConsole("");
                sendConsole("&7(Это сообщение выводится только один раз)");
                sendConsole("&8==================================================");
            }, 600L);
        }
    }

    @Override
    public void onDisable() {
        this.database.saveAll();
        this.database.disconnect();
        if (redisManager != null) {
            redisManager.disconnect();
        }

        sendConsole(" ");
        sendConsole("&b» &7Плагин &6PlayerThemes &7выключен!");
        sendConsole("&b» &7Версия: &c" + getDescription().getVersion());
        sendConsole(" ");
    }

    public void setupCommands() {
        AdminCommand adminCommand = new AdminCommand(this);
        PluginCommand pluginCommand = getCommand("pth");
        pluginCommand.setExecutor(adminCommand);

        try {
            CommandMap commandMap = getServer().getCommandMap();

            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            PluginCommand command = constructor.newInstance(this.config.command().mainCommand(), this);
            command.setAliases(this.config.command().aliases());

            ThemesCommand themesCommand = new ThemesCommand(this);
            command.setExecutor(themesCommand);
            commandMap.register(getDescription().getName(), command);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось зарегистрировать команды выбора темы", e);
        }
    }

    public void reload() {
        reloadConfig();
        start();
    }

    private void start() {
        Text.init(getConfig());
        this.config.load();
        this.themeManager.initialize();
        this.database = switch (config.database().type()) {
            case MYSQL -> new MySQLDatabase(this, config.database());
            case SQLITE -> new SQLiteDatabase(this, config.database());
        };
        this.database.connect();
        this.database.load();
        DatabaseSettings.RedisSettings redis = config.database().redis();
        if (redis != null) {
            this.redisManager = new RedisManager(config.database().redis());
            this.redisManager.connect((uuid, theme) -> {
                this.themeManager.players().put(uuid, theme);
            });
            getLogger().info("Redis успешно подключен!");
        }
    }

    public void checkForUpdates() {
        updateChecker.check(version -> {
            if (!config.notifyUpdates()) return;

            String currentVersion = getDescription().getVersion();
            if (!currentVersion.equals(version)) {
                sendConsole("");
                sendConsole("&6PlayerThemes &7| &eОбновление доступно! &c" + currentVersion + " &7-> &a" + version);
                sendConsole("&7Скачать можно на: ");
                sendConsole(" &8- &7https://spigotmc.ru/resources/4767/");
                sendConsole(" &8- &7https://www.spigotmc.org/resources/131023/");
                sendConsole(" &8- &7https://black-minecraft.com/resources/9951/");
                sendConsole("");
                updateChecker.updateAvailable = true;
            }
        });
    }

    private void sendConsole(String message) {
        console.sendMessage(Text.legacy(message));
    }
}
