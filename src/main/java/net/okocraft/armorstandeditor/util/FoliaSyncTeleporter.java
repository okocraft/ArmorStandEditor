package net.okocraft.armorstandeditor.util;

import net.minecraft.world.phys.Vec3;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public final class FoliaSyncTeleporter {

    private static final boolean FOLIA;

    static {
        boolean isFolia;

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }

        FOLIA = isFolia;
    }

    public static boolean isFolia() {
        return FOLIA;
    }

    private static volatile boolean initialized = false;
    private static Method teleportSyncSameRegion;

    public static void teleport(@NotNull Entity entity, @NotNull Location loc) {
        if (!initialized) {
            synchronized (FoliaSyncTeleporter.class) {
                if (!initialized) { // prevent double initializing
                    try {
                        init();
                    } catch (Exception e) {
                        logError("Could not initialize reflections.", e);
                    } finally {
                        initialized = true;
                    }
                }
            }
        }

        if (teleportSyncSameRegion != null) {
            try {
                var handle = ((CraftEntity) entity).getHandle();
                teleportSyncSameRegion.invoke(handle, CraftLocation.toVec3(loc), null, null, Vec3.ZERO);
            } catch (Exception e) {
                logError("Could not invoke methods", e);
            }
        } else {
            entity.teleportAsync(loc).join(); // very unstable...
        }
    }

    private static void init() throws Exception {
        // Entity#teleportSyncSameRegion(Vec3 pos, Float yaw, Float pitch, Vec3 speedDirectionUpdate)
        teleportSyncSameRegion = net.minecraft.world.entity.Entity.class.getDeclaredMethod(
                "teleportSyncSameRegion",
                Vec3.class,
                Float.class,
                Float.class,
                Vec3.class
        );
        teleportSyncSameRegion.setAccessible(true);
    }

    private static void logError(@NotNull String msg, @NotNull Throwable e) {
        JavaPlugin.getPlugin(ArmorStandEditorPlugin.class).getSLF4JLogger().error(msg, e);
    }
}
