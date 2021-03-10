package net.okocraft.armorstandeditor.util;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public final class LocationCalculator {

    private LocationCalculator() {
        throw new UnsupportedOperationException();
    }

    public static Location calculate(@NotNull Location original, double distance,
                                     @NotNull PlayerEditor.Axis axis, boolean reverse) {
        return !reverse ?
                add(original, distance, axis) :
                subtract(original, distance, axis);
    }

    private static @NotNull Location add(@NotNull Location original, double distance,
                                         @NotNull PlayerEditor.Axis axis) {
        switch (axis) {
            case X:
                return original.add(distance, 0, 0);
            case Y:
                return original.add(0, distance, 0);
            case Z:
                return original.add(0, 0, distance);
            default:
                return original;
        }
    }

    private static @NotNull Location subtract(@NotNull Location original, double distance,
                                              @NotNull PlayerEditor.Axis axis) {
        switch (axis) {
            case X:
                return original.subtract(distance, 0, 0);
            case Y:
                return original.subtract(0, distance, 0);
            case Z:
                return original.subtract(0, 0, distance);
            default:
                return original;
        }
    }
}
