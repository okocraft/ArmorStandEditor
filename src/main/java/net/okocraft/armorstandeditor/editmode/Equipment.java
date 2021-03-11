package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.menu.EquipmentMenuProvider;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class Equipment extends AbstractEditMode {

    Equipment() {
        super("equipment");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        var menu = EquipmentMenuProvider.getMenu(armorStand);
        editor.getPlayer().openInventory(menu.getInventory());
    }
}
