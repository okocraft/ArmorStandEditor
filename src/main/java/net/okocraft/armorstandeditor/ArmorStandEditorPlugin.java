package net.okocraft.armorstandeditor;

import com.github.siroshun09.configapi.api.Configuration;
import com.github.siroshun09.configapi.api.util.ResourceUtils;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import com.github.siroshun09.translationloader.ConfigurationLoader;
import com.github.siroshun09.translationloader.TranslationLoader;
import com.github.siroshun09.translationloader.directory.TranslationDirectory;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.okocraft.armorstandeditor.command.ArmorStandEditorCommand;
import net.okocraft.armorstandeditor.item.EditToolItem;
import net.okocraft.armorstandeditor.listener.ArmorStandListener;
import net.okocraft.armorstandeditor.listener.InventoryListener;
import net.okocraft.armorstandeditor.listener.PlayerListener;
import net.okocraft.armorstandeditor.menu.ArmorStandEditorMenu;
import net.okocraft.armorstandeditor.util.TaskScheduler;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.jar.JarFile;
import java.util.logging.Level;

@SuppressWarnings("UnstableApiUsage")
public final class ArmorStandEditorPlugin extends JavaPlugin {

    private final YamlConfiguration configuration =
            YamlConfiguration.create(this.getDataFolder().toPath().resolve("config.yml"));
    private final TranslationDirectory translationDirectory =
            TranslationDirectory
                    .newBuilder()
                    .setKey(Key.key("armorstandeditor", "language"))
                    .setDirectory(this.getDataFolder().toPath().resolve("languages"))
                    .setDefaultLocale(Locale.ENGLISH)
                    .setVersion(this.getDescription().getVersion())
                    .onDirectoryCreated(this::saveDefaultLanguages)
                    .setTranslationLoaderCreator(this::getBuiltinTranslation)
                    .build();

    private EditToolItem editToolItem;

    @Override
    public void onLoad() {
        try {
            ResourceUtils.copyFromJarIfNotExists(this.getFile().toPath(), "config.yml", this.configuration.getPath());
            this.configuration.load();
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Failed to load config.yml.", e);
        }

        try {
            this.translationDirectory.load();
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Failed to load languages.", e);
        }
    }

    @Override
    public void onEnable() {
        var manager = this.getServer().getPluginManager();

        manager.registerEvents(new ArmorStandListener(this), this);
        manager.registerEvents(new InventoryListener(), this);
        manager.registerEvents(new PlayerListener(this), this);

        this.editToolItem = new EditToolItem(this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> ArmorStandEditorCommand.register(event.registrar(), this));
    }

    @Override
    public void onDisable() {
        this.getServer().getOnlinePlayers().stream().filter(player -> ArmorStandEditorMenu.isArmorStandEditorMenu(player.getOpenInventory().getTopInventory())).forEach(HumanEntity::closeInventory);
        HandlerList.unregisterAll(this);
        TaskScheduler.cancelTasks();
        this.translationDirectory.unload();
    }

    public @NotNull YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    public @NotNull TranslationDirectory getTranslationDirectory() {
        return this.translationDirectory;
    }

    public @NotNull EditToolItem getEditToolItem() {
        return this.editToolItem;
    }

    private void saveDefaultLanguages(@NotNull Path directory) throws IOException {
        var jarFile = this.getFile().toPath();

        var defaultFileName = "en.yml";
        var defaultFile = directory.resolve(defaultFileName);

        ResourceUtils.copyFromJarIfNotExists(jarFile, defaultFileName, defaultFile);

        var japaneseFileName = "ja_JP.yml";
        var japaneseFile = directory.resolve(japaneseFileName);

        ResourceUtils.copyFromJarIfNotExists(jarFile, japaneseFileName, japaneseFile);
    }

    private @Nullable TranslationLoader getBuiltinTranslation(@NotNull Locale locale) throws IOException {
        var strLocale = locale.toString();

        if (!(strLocale.equals("en") || strLocale.equals("ja_JP"))) {
            return null;
        }

        Configuration source;

        try (var jar = new JarFile(this.getFile(), false);
             var input = ResourceUtils.getInputStreamFromJar(jar, strLocale + ".yml")) {
            source = YamlConfiguration.loadFromInputStream(input);
        }

        var loader = ConfigurationLoader.create(locale, source);
        loader.load();

        return loader;
    }
}
