package net.okocraft.armorstandeditor.editmode;

import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class Lock extends AbstractEditMode {

    Lock() {
        super("lock");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        Component message;

        if (editor.isLocked(armorStand)) {
            editor.unlock(armorStand);
            message = Messages.EDIT_UNLOCK;
        } else {
            editor.lock(armorStand);
            message = Messages.EDIT_LOCK;
        }

        editor.getPlayer().sendActionBar(message);
    }
}
