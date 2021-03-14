package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.util.LocationCalculator;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class Movement extends AbstractEditMode {

    Movement() {
        super("movement");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        var location = LocationCalculator.calculate(
                armorStand.getLocation(),
                editor.getMovingDistance(),
                editor.getAxis(),
                reverse
        );

        armorStand.teleport(location);
    }
}
