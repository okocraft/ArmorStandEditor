package net.okocraft.armorstandeditor.editmode;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class Copy extends AbstractEditMode {

    Copy() {
        super("copy");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        editor.copy(armorStand);

        var slot = Component.text(String.valueOf(editor.getSelectedCopySlot()), NamedTextColor.AQUA);
        editor.getPlayer().sendActionBar(Messages.EDIT_COPY.args(slot));
    }
}
