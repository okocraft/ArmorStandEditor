package net.okocraft.armorstandeditor;

import net.okocraft.armorstandeditor.lang.LanguageLoader;
import net.okocraft.armorstandeditor.listener.ArmorStandListener;
import net.okocraft.armorstandeditor.listener.InventoryListener;
import net.okocraft.armorstandeditor.listener.PlayerListener;
import net.okocraft.armorstandeditor.util.EditItemChecker;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public final class ArmorStandEditorPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        var manager = getServer().getPluginManager();

        try {
            LanguageLoader.load(this);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to load languages.", e);
            manager.disablePlugin(this);
            return;
        }

        EditItemChecker.setNamespacedKey(this);

        manager.registerEvents(new ArmorStandListener(), this);
        manager.registerEvents(new InventoryListener(), this);
        manager.registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
    }
}
