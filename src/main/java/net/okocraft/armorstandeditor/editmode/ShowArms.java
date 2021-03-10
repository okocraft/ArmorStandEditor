package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
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
    }
}
