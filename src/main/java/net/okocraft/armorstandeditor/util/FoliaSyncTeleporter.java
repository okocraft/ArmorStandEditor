package net.okocraft.armorstandeditor.util;

import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public final class FoliaSyncTeleporter {

    private static volatile boolean initialized = false;
    private static Method toVec3D;
    private static Object vec3DZero;
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
                teleportSyncSameRegion.invoke(handle, toVec3D.invoke(null, loc), null, null, vec3DZero);
            } catch (Exception e) {
                logError("Could not invoke methods", e);
            }
        } else {
            entity.teleportAsync(loc).join(); // very unstable...
        }
    }

    private static void init(@NotNull Entity entity) throws Exception {
        // CraftLocation#toVec3D
        var craftLocation = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".util.CraftLocation");
        toVec3D = craftLocation.getDeclaredMethod("toVec3D", Location.class);
        vec3DZero = toVec3D.invoke(null, new Location(null, 0, 0, 0));

        // CraftEntity#getHandle
        getHandle = entity.getClass().getMethod("getHandle");
        getHandle.setAccessible(true);

        var entityClass = getHandle.invoke(entity).getClass().getSuperclass().getSuperclass(); // ArmorStand -> LivingEntity -> Entity

        // Entity#teleportSyncSameRegion(Vec3 pos, Float yaw, Float pitch, Vec3 speedDirectionUpdate)
        teleportSyncSameRegion = entityClass.getDeclaredMethod("teleportSyncSameRegion", vec3DZero.getClass(), Float.class, Float.class, vec3DZero.getClass());
        teleportSyncSameRegion.setAccessible(true);
    }

    private static void logError(@NotNull String msg, @NotNull Throwable e) {
        JavaPlugin.getPlugin(ArmorStandEditorPlugin.class).getSLF4JLogger().error(msg, e);
    }
}
