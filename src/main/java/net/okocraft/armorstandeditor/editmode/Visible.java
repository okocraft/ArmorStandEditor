package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class Visible extends AbstractEditMode {

    Visible() {
        super("visible");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        boolean current = armorStand.isVisible();

        armorStand.setVisible(!current);

        var message = armorStand.isVisible() ? Messages.EDIT_VISIBLE_ON : Messages.EDIT_VISIBLE_OFF;
        editor.getPlayer().sendActionBar(message);
    }
}
