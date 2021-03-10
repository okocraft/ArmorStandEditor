package net.okocraft.armorstandeditor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class ArmorStandEditorPlugin extends JavaPlugin {

    private static Plugin PLUGIN;

    public static void runTask(@NotNull Runnable runnable) {
        if (PLUGIN != null && PLUGIN.isEnabled()) {
            Bukkit.getScheduler().runTaskLater(PLUGIN, runnable, 1);
        }
    }
}
