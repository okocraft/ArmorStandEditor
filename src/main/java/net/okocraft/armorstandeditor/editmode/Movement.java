package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.util.FoliaSyncTeleporter;
import net.okocraft.armorstandeditor.util.LocationCalculator;
import net.okocraft.armorstandeditor.util.TaskScheduler;
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

        if (TaskScheduler.FOLIA) {
            FoliaSyncTeleporter.teleport(armorStand, location);
        } else {
            armorStand.teleport(location);
        }
    }
}
