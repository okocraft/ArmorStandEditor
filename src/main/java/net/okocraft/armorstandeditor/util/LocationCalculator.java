package net.okocraft.armorstandeditor.util;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public final class LocationCalculator {

    private LocationCalculator() {
        throw new UnsupportedOperationException();
    }

    public static Location calculate(@NotNull Location original, double distance,
                                     @NotNull Axis axis, boolean reverse) {
        return !reverse ?
            add(original, distance, axis) :
            subtract(original, distance, axis);
    }

    private static @NotNull Location add(@NotNull Location original, double distance,
                                         @NotNull Axis axis) {
        return switch (axis) {
            case X -> original.add(distance, 0, 0);
            case Y -> original.add(0, distance, 0);
            case Z -> original.add(0, 0, distance);
        };
    }

    private static @NotNull Location subtract(@NotNull Location original, double distance,
                                              @NotNull Axis axis) {
        return switch (axis) {
            case X -> original.subtract(distance, 0, 0);
            case Y -> original.subtract(0, distance, 0);
            case Z -> original.subtract(0, 0, distance);
        };
    }
}
