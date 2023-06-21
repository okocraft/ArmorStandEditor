package net.okocraft.armorstandeditor.util;

import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class TaskScheduler {

    public static final boolean FOLIA;

    static {
        boolean isFolia;

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServerInitEvent");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }

        FOLIA = isFolia;
    }

    public static <T extends Entity> void scheduleEntityTask(@NotNull Supplier<T> entitySupplier, @NotNull Consumer<@Nullable T> task) {
        if (FOLIA) {
            entitySupplier.get().getScheduler().execute(plugin(), () -> task.accept(entitySupplier.get()), null, 1);
        } else {
            Bukkit.getScheduler().runTask(plugin(), () -> task.accept(entitySupplier.get()));
        }
    }

    public static <T extends Entity> void scheduleEntityTasks(@NotNull Supplier<Collection<T>> entitiesSupplier, @NotNull Consumer<T> task) {
        if (FOLIA) {
            entitiesSupplier.get().forEach(entity -> entity.getScheduler().execute(plugin(), () -> task.accept(entity), null, 1));
        } else {
            Bukkit.getScheduler().runTask(plugin(), () -> entitiesSupplier.get().forEach(task));
        }
    }

    public static void cancelTasks() {
        var plugin = plugin();

        if (FOLIA) {
            Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
            Bukkit.getAsyncScheduler().cancelTasks(plugin);
        } else {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
    }

    private static @NotNull JavaPlugin plugin() {
        return JavaPlugin.getPlugin(ArmorStandEditorPlugin.class);
    }

    private TaskScheduler() {
        throw new UnsupportedOperationException();
    }
}
