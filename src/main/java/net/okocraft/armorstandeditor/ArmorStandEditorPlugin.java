package net.okocraft.armorstandeditor;

import com.github.siroshun09.configapi.api.util.ResourceUtils;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import dev.siroshun.mcmsgdef.directory.DirectorySource;
import dev.siroshun.mcmsgdef.directory.MessageProcessors;
import dev.siroshun.mcmsgdef.file.PropertiesFile;
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
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public final class ArmorStandEditorPlugin extends JavaPlugin {

    private final YamlConfiguration configuration =
        YamlConfiguration.create(this.getDataFolder().toPath().resolve("config.yml"));

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
            this.loadMessages();
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
    }

    public @NotNull YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    public void loadMessages() throws IOException {
        DirectorySource.propertiesFiles(this.getDataFolder().toPath().resolve("languages"))
            .defaultLocale(Locale.ENGLISH, Locale.JAPANESE)
            .messageProcessor(MessageProcessors.appendMissingMessagesToPropertiesFile(this::loadDefaultMessageMap))
            .loadAndRegister(Key.key("armorstandeditor", "language"));
    }

    private @Nullable Map<String, String> loadDefaultMessageMap(@NotNull Locale locale) throws IOException {
        try (var input = this.getResource("languages/" + locale + ".properties")) {
            return input != null ? PropertiesFile.load(input) : null;
        }
    }

    public @NotNull EditToolItem getEditToolItem() {
        return this.editToolItem;
    }
}
