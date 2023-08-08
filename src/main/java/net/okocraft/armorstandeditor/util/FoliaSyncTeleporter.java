package net.okocraft.armorstandeditor.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public final class FoliaSyncTeleporter {

    private static volatile boolean initialized = false;
    private static Method getHandle;
    private static Method moveTo;

    public static void teleport(@NotNull Entity entity, @NotNull Location loc) {
        if (!initialized) {
            synchronized (FoliaSyncTeleporter.class) {
                if (!initialized) { // prevent double initializing
                    try {
                        init(entity);
                    } catch (Exception ignored) {
                    } finally {
                        initialized = true;
                    }
                }
            }
        }

        if (moveTo != null) {
            try {
                moveTo.invoke(getHandle.invoke(entity), loc.x(), loc.y(), loc.z());
            } catch (Exception ignored) {
            }
        } else {
            entity.teleportAsync(loc).join(); // very unstable...
        }
    }

    private static void init(@NotNull Entity entity) throws Exception {
        // CraftEntity#getHandle
        getHandle = entity.getClass().getMethod("getHandle");
        getHandle.setAccessible(true);

        var handle = getHandle.invoke(entity);

        // Entity#moveTo(double x, double y, double z)
        moveTo = handle.getClass().getMethod(getObfNameOfMoveTo(), double.class, double.class, double.class);
        moveTo.setAccessible(true);
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private static String getObfNameOfMoveTo() {
        /*
          Steps to get obfuscation name:
            1. Get the server obfuscation maps (ex: Go https://minecraft.fandom.com/wiki/Java_Edition_%VERSION%, and open the "Server" link in "Obfuscation maps")
            2. Looking for "void moveTo(double,double,double) ->" in the page
            3. The name after "->" is the obfuscated method name
        */
        return switch (Bukkit.getMinecraftVersion()) {
            default /* case "1.19.4", "1.20", "1.20.1" */ -> "d"; // Folia is only available in 1.19.4+
        };
    }
}
