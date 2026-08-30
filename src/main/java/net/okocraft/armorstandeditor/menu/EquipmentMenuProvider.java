package net.okocraft.armorstandeditor.menu;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class EquipmentMenuProvider {

    private static final ConcurrentMap<UUID, EquipmentMenu> MENU_MAP = new ConcurrentHashMap<>();

    private EquipmentMenuProvider() {
        throw new UnsupportedOperationException();
    }

    public static boolean openMenu(@NotNull ArmorStand armorStand, @NotNull Player viewer) {
        var armorStandUuid = armorStand.getUniqueId();
        var current = MENU_MAP.get(armorStandUuid);

        if (current != null) {
            if (current.isOpenBy(viewer)) {
                return true;
            }
            if (!current.releaseIfInactive()) {
                return false;
            }
        }

        var menu = new EquipmentMenu(armorStandUuid, viewer.getUniqueId());
        if (MENU_MAP.putIfAbsent(armorStandUuid, menu) != null) {
            return false;
        }

        if (menu.open(armorStand, viewer)) {
            return true;
        }

        release(menu);
        return false;
    }

    public static @Nullable EquipmentMenu getMenuOrNull(@NotNull ArmorStand armorStand) {
        return MENU_MAP.get(armorStand.getUniqueId());
    }

    public static @Nullable EquipmentMenu removeMenu(@NotNull ArmorStand armorStand) {
        return MENU_MAP.remove(armorStand.getUniqueId());
    }

    static void release(@NotNull EquipmentMenu menu) {
        MENU_MAP.remove(menu.getArmorStandUuid(), menu);
    }
}
