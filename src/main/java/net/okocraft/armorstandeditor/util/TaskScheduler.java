package net.okocraft.armorstandeditor.util;

import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public  final class TaskScheduler {

    public static void runNextTick(@NotNull Runnable task) {
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(ArmorStandEditorPlugin.class), task);
    }

    private TaskScheduler() {
        throw new UnsupportedOperationException();
    }
}
