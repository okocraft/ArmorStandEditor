package net.okocraft.armorstandeditor.menu;

import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class EquipmentMenuProvider {

    private static final ConcurrentMap<UUID, UUID> ACTIVE_VIEWERS = new ConcurrentHashMap<>();

    private EquipmentMenuProvider() {
        throw new UnsupportedOperationException();
    }

    public static boolean openMenu(@NotNull ArmorStand armorStand, @NotNull Player viewer) {
        if (!Bukkit.isOwnedByCurrentRegion(viewer) ||
            !Bukkit.isOwnedByCurrentRegion(armorStand) ||
            armorStand.isDead()) {
            return false;
        }

        var armorStandUuid = armorStand.getUniqueId();
        var viewerUuid = viewer.getUniqueId();

        if (viewerUuid.equals(ACTIVE_VIEWERS.get(armorStandUuid)) && findOpenMenu(viewer, armorStandUuid) != null) {
            return true;
        }

        var currentViewerUuid = ACTIVE_VIEWERS.putIfAbsent(armorStandUuid, viewerUuid);
        if (currentViewerUuid != null) {
            releaseIfInactive(armorStandUuid, currentViewerUuid);
            return false;
        }

        var menu = new EquipmentMenu(armorStandUuid);
        if (menu.open(armorStand, viewer)) {
            return true;
        }

        release(armorStandUuid, viewerUuid);
        return false;
    }

    public static void closeMenu(@NotNull ArmorStand armorStand) {
        var armorStandUuid = armorStand.getUniqueId();
        var viewerUuid = ACTIVE_VIEWERS.get(armorStandUuid);
        if (viewerUuid == null) {
            return;
        }

        var viewer = Bukkit.getPlayer(viewerUuid);
        if (viewer == null) {
            release(armorStandUuid, viewerUuid);
        } else {
            closeMenu(armorStandUuid, viewer);
        }
    }

    public static void release(@NotNull EquipmentMenu menu, @NotNull HumanEntity viewer) {
        release(menu.getArmorStandUuid(), viewer.getUniqueId());
    }

    private static void closeMenu(@NotNull UUID armorStandUuid, @NotNull HumanEntity viewer) {
        var viewerUuid = viewer.getUniqueId();
        viewer.getScheduler().run(
            ArmorStandEditorPlugin.plugin(),
            ignored -> {
                if (findOpenMenu(viewer, armorStandUuid) != null) {
                    viewer.closeInventory();
                }
                release(armorStandUuid, viewerUuid);
            },
            () -> release(armorStandUuid, viewerUuid)
        );
    }

    private static void releaseIfInactive(@NotNull UUID armorStandUuid, @NotNull UUID viewerUuid) {
        var viewer = Bukkit.getPlayer(viewerUuid);
        if (viewer == null) {
            release(armorStandUuid, viewerUuid);
            return;
        }

        if (Bukkit.isOwnedByCurrentRegion(viewer)) {
            if (findOpenMenu(viewer, armorStandUuid) == null) {
                release(armorStandUuid, viewerUuid);
            }
            return;
        }

        viewer.getScheduler().run(
            ArmorStandEditorPlugin.plugin(),
            ignored -> {
                if (findOpenMenu(viewer, armorStandUuid) == null) {
                    release(armorStandUuid, viewerUuid);
                }
            },
            () -> release(armorStandUuid, viewerUuid)
        );
    }

    private static void release(@NotNull UUID armorStandUuid, @NotNull UUID viewerUuid) {
        ACTIVE_VIEWERS.remove(armorStandUuid, viewerUuid);
    }

    private static @Nullable EquipmentMenu findOpenMenu(@NotNull HumanEntity viewer, @NotNull UUID armorStandUuid) {
        var menu = ArmorStandEditorMenu.getMenuFromInventory(
            viewer.getOpenInventory().getTopInventory(),
            EquipmentMenu.class
        );
        return menu != null && menu.isFor(armorStandUuid) ? menu : null;
    }
}
