package net.okocraft.armorstandeditor.util;

import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public final class FoliaSyncTeleporter {

    public static final boolean FOLIA;

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
    private static Method toVec3;
    private static Object vec3Zero;
    private static Method getHandle;
    private static Method teleportSyncSameRegion;

    public static void teleport(@NotNull Entity entity, @NotNull Location loc) {
        if (!initialized) {
            synchronized (FoliaSyncTeleporter.class) {
                if (!initialized) { // prevent double initializing
                    try {
                        init(entity);
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
                var handle = getHandle.invoke(entity);
                teleportSyncSameRegion.invoke(handle, toVec3.invoke(null, loc), null, null, vec3Zero);
            } catch (Exception e) {
                logError("Could not invoke methods", e);
            }
        } else {
            entity.teleportAsync(loc).join(); // very unstable...
        }
    }

    private static void init(@NotNull Entity entity) throws Exception {
        // CraftLocation#toVec3
        var craftLocation = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".util.CraftLocation");
        toVec3 = craftLocation.getDeclaredMethod("toVec3", Location.class);
        vec3Zero = toVec3.invoke(null, new Location(null, 0, 0, 0));

        // CraftEntity#getHandle
        getHandle = entity.getClass().getMethod("getHandle");
        getHandle.setAccessible(true);

        var entityClass = getHandle.invoke(entity).getClass().getSuperclass().getSuperclass(); // ArmorStand -> LivingEntity -> Entity

        // Entity#teleportSyncSameRegion(Vec3 pos, Float yaw, Float pitch, Vec3 speedDirectionUpdate)
        teleportSyncSameRegion = entityClass.getDeclaredMethod("teleportSyncSameRegion", vec3Zero.getClass(), Float.class, Float.class, vec3Zero.getClass());
        teleportSyncSameRegion.setAccessible(true);
    }

    private static void logError(@NotNull String msg, @NotNull Throwable e) {
        JavaPlugin.getPlugin(ArmorStandEditorPlugin.class).getSLF4JLogger().error(msg, e);
    }
}
