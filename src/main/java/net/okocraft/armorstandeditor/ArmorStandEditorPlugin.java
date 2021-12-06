package net.okocraft.armorstandeditor;

import com.github.siroshun09.configapi.api.util.ResourceUtils;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import com.github.siroshun09.mccommand.paper.PaperCommandFactory;
import com.github.siroshun09.mccommand.paper.listener.AsyncTabCompleteListener;
import com.github.siroshun09.translationloader.directory.TranslationDirectory;
import net.kyori.adventure.key.Key;
import net.okocraft.armorstandeditor.command.ArmorStandEditorCommand;
import net.okocraft.armorstandeditor.item.EditToolItem;
import net.okocraft.armorstandeditor.listener.ArmorStandListener;
import net.okocraft.armorstandeditor.listener.InventoryListener;
import net.okocraft.armorstandeditor.listener.PlayerListener;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;

public final class ArmorStandEditorPlugin extends JavaPlugin {

    private final YamlConfiguration configuration =
            YamlConfiguration.create(getDataFolder().toPath().resolve("config.yml"));
    private final TranslationDirectory translationDirectory =
            TranslationDirectory.create(getDataFolder().toPath().resolve("languages"), Key.key("armorstandeditor", "language"));

    private EditToolItem editToolItem;

    @Override
    public void onLoad() {
        try {
            ResourceUtils.copyFromJarIfNotExists(getFile().toPath(), "config.yml", configuration.getPath());
            configuration.load();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to load config.yml.", e);
        }

        try {
            translationDirectory.createDirectoryIfNotExists(this::saveDefaultLanguages);
            translationDirectory.load();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to load languages.", e);
        }
    }

    @Override
    public void onEnable() {
        var manager = getServer().getPluginManager();

        manager.registerEvents(new ArmorStandListener(this), this);
        manager.registerEvents(new InventoryListener(), this);
        manager.registerEvents(new PlayerListener(this), this);

        var command = new ArmorStandEditorCommand(this);

        Optional.ofNullable(getCommand("armorstandeditor"))
                .ifPresent(target -> {
                    PaperCommandFactory.register(target, command);
                    AsyncTabCompleteListener.register(this, command);
                });

        editToolItem = new EditToolItem(this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        translationDirectory.unload();
    }

    public @NotNull YamlConfiguration getConfiguration() {
        return configuration;
    }

    public @NotNull TranslationDirectory getTranslationDirectory() {
        return translationDirectory;
    }

    public @NotNull EditToolItem getEditToolItem() {
        return editToolItem;
    }

    private void saveDefaultLanguages(@NotNull Path directory) throws IOException {
        var jarFile = getFile().toPath();

        var defaultFileName = "en.yml";
        var defaultFile = directory.resolve(defaultFileName);

        ResourceUtils.copyFromJarIfNotExists(jarFile, defaultFileName, defaultFile);

        var japaneseFileName = "ja_JP.yml";
        var japaneseFile = directory.resolve(japaneseFileName);

        ResourceUtils.copyFromJarIfNotExists(jarFile, japaneseFileName, japaneseFile);
    }
}
