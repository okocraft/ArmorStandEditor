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

    private static final ConcurrentMap<UUID, UUID> VIEWER_MAP = new ConcurrentHashMap<>();

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
        var currentViewerUuid = VIEWER_MAP.get(armorStandUuid);

        if (currentViewerUuid != null) {
            if (currentViewerUuid.equals(viewerUuid) && getOpenMenu(viewer, armorStandUuid) != null) {
                return true;
            }
            if (!releaseIfInactive(armorStandUuid, currentViewerUuid)) {
                return false;
            }
        }

        if (VIEWER_MAP.putIfAbsent(armorStandUuid, viewerUuid) != null) {
            return false;
        }

        var menu = new EquipmentMenu(armorStandUuid);
        if (menu.open(armorStand, viewer)) {
            return true;
        }

        release(armorStandUuid, viewerUuid);
        return false;
    }

    public static void refreshMenu(@NotNull ArmorStand armorStand) {
        var armorStandUuid = armorStand.getUniqueId();
        if (!VIEWER_MAP.containsKey(armorStandUuid)) {
            return;
        }

        armorStand.getScheduler().run(
            ArmorStandEditorPlugin.plugin(),
            ignored -> refreshMenuNow(armorStand),
            null
        );
    }

    public static void closeMenu(@NotNull ArmorStand armorStand) {
        var viewerUuid = VIEWER_MAP.get(armorStand.getUniqueId());
        if (viewerUuid == null) {
            return;
        }

        var viewer = Bukkit.getPlayer(viewerUuid);
        if (viewer == null) {
            release(armorStand.getUniqueId(), viewerUuid);
            return;
        }

        closeMenu(armorStand.getUniqueId(), viewer);
    }

    static boolean isViewer(@NotNull UUID armorStandUuid, @NotNull HumanEntity viewer) {
        return viewer.getUniqueId().equals(VIEWER_MAP.get(armorStandUuid));
    }

    static void closeMenu(@NotNull UUID armorStandUuid, @NotNull HumanEntity viewer) {
        var viewerUuid = viewer.getUniqueId();
        boolean owner = viewerUuid.equals(VIEWER_MAP.get(armorStandUuid));

        viewer.getScheduler().run(
            ArmorStandEditorPlugin.plugin(),
            ignored -> {
                if (getOpenMenu(viewer, armorStandUuid) != null) {
                    viewer.closeInventory();
                }
                if (owner) {
                    release(armorStandUuid, viewerUuid);
                }
            },
            owner ? () -> release(armorStandUuid, viewerUuid) : null
        );
    }

    static void release(@NotNull UUID armorStandUuid, @NotNull UUID viewerUuid) {
        VIEWER_MAP.remove(armorStandUuid, viewerUuid);
    }

    private static boolean releaseIfInactive(@NotNull UUID armorStandUuid, @NotNull UUID viewerUuid) {
        var viewer = Bukkit.getPlayer(viewerUuid);
        if (viewer == null) {
            return VIEWER_MAP.remove(armorStandUuid, viewerUuid);
        }

        if (Bukkit.isOwnedByCurrentRegion(viewer)) {
            if (getOpenMenu(viewer, armorStandUuid) != null) {
                return false;
            }
            return VIEWER_MAP.remove(armorStandUuid, viewerUuid);
        }

        viewer.getScheduler().run(
            ArmorStandEditorPlugin.plugin(),
            ignored -> {
                if (getOpenMenu(viewer, armorStandUuid) == null) {
                    release(armorStandUuid, viewerUuid);
                }
            },
            () -> release(armorStandUuid, viewerUuid)
        );
        return false;
    }

    private static void refreshMenuNow(@NotNull ArmorStand armorStand) {
        var armorStandUuid = armorStand.getUniqueId();
        var viewerUuid = VIEWER_MAP.get(armorStandUuid);
        if (viewerUuid == null) {
            return;
        }

        if (armorStand.isDead()) {
            closeMenu(armorStand);
            return;
        }

        var viewer = Bukkit.getPlayer(viewerUuid);
        if (viewer == null) {
            release(armorStandUuid, viewerUuid);
            return;
        }

        if (!Bukkit.isOwnedByCurrentRegion(viewer)) {
            closeMenu(armorStandUuid, viewer);
            return;
        }

        var menu = getOpenMenu(viewer, armorStandUuid);
        if (menu == null) {
            release(armorStandUuid, viewerUuid);
            return;
        }

        menu.renderItems(armorStand.getEquipment());
    }

    private static @Nullable EquipmentMenu getOpenMenu(@NotNull HumanEntity viewer, @NotNull UUID armorStandUuid) {
        var menu = ArmorStandEditorMenu.getMenuFromInventory(
            viewer.getOpenInventory().getTopInventory(),
            EquipmentMenu.class
        );
        return menu != null && menu.isFor(armorStandUuid) ? menu : null;
    }
}
