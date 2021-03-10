package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.menu.EquipmentMenu;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class Equipment extends AbstractEditMode {

    private final Map<UUID, EquipmentMenu> menuMap = new HashMap<>();

    Equipment() {
        super("equipment");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        UUID uuid = armorStand.getUniqueId();
        EquipmentMenu menu = menuMap.get(uuid);

        if (menu == null) {
            menu = new EquipmentMenu(armorStand);
            menuMap.put(uuid, menu);
        }

        editor.getPlayer().openInventory(menu.getInventory());
    }
}
