package net.okocraft.armorstandeditor.editmode;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class Paste extends AbstractEditMode {

    Paste() {
        super("paste");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        var data = editor.getSelectedArmorStand();

        if (data == null) {
            return;
        }

        data.apply(armorStand, editor.getPlayer().getGameMode() == GameMode.CREATIVE);

        var slot = Component.text(String.valueOf(editor.getSelectedCopySlot()), NamedTextColor.AQUA);
        editor.getPlayer().sendActionBar(Messages.EDIT_PASTE.args(slot));
    }
}
