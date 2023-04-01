package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class CustomNameVisible extends AbstractEditMode {

    CustomNameVisible() {
        super("custom-name-visible");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        armorStand.setCustomNameVisible(!armorStand.isCustomNameVisible());

        var message = armorStand.isCustomNameVisible() ? Messages.EDIT_CUSTOM_NAME_VISIBLE_ON : Messages.EDIT_CUSTOM_NAME_VISIBLE_OFF;
        editor.getPlayer().sendActionBar(message);
    }
}
