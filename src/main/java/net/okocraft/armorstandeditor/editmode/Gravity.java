package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class Gravity extends AbstractEditMode {

    Gravity() {
        super("gravity");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        boolean current = armorStand.hasGravity();

        armorStand.setGravity(!current);

        var message = armorStand.hasBasePlate() ? Messages.EDIT_GRAVITY_ON : Messages.EDIT_GRAVITY_OFF;
        editor.getPlayer().sendActionBar(message);
    }
}
