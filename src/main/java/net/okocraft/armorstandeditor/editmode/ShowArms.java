package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class ShowArms extends AbstractEditMode {

    ShowArms() {
        super("show-arms");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        boolean current = armorStand.hasArms();

        armorStand.setArms(!current);

        var message = armorStand.hasArms() ? Messages.EDIT_ARMS_ON : Messages.EDIT_ARMS_OFF;
        editor.getPlayer().sendActionBar(message);
    }
}
