package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.menu.SelectionMenu;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class None extends AbstractEditMode {

    None() {
        super("none");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        var player = editor.getPlayer();
        var menu = new SelectionMenu(player);

        player.openInventory(menu.getInventory());
    }
}
