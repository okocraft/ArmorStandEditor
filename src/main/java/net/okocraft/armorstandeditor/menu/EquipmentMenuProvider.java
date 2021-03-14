package net.okocraft.armorstandeditor.menu;

import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EquipmentMenuProvider {

    private static final Map<UUID, EquipmentMenu> MENU_MAP = new HashMap<>();

    private EquipmentMenuProvider() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull EquipmentMenu getMenu(@NotNull ArmorStand armorStand) {
        var uuid = armorStand.getUniqueId();
        var menu = MENU_MAP.get(uuid);

        if (menu == null) {
            menu = new EquipmentMenu(armorStand);
            MENU_MAP.put(uuid, menu);
        }

        return menu;
    }
}
