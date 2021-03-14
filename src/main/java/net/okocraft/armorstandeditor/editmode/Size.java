package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class Size extends AbstractEditMode {

    Size() {
        super("size");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        boolean current = armorStand.isSmall();

        armorStand.setSmall(!current);

        var message = armorStand.isSmall() ? Messages.EDIT_SIZE_SMALL : Messages.EDIT_SIZE_NORMAL;
        editor.getPlayer().sendActionBar(message);
    }
}
