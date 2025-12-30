package net.okocraft.armorstandeditor.util;

import org.bukkit.Axis;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

public final class AngleCalculator {

    public static final EulerAngle INITIAL_ANGLE = new EulerAngle(0, 0, 0);
    private static final double RADIAN = Math.PI / 180;

    private AngleCalculator() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull EulerAngle calculate(@NotNull EulerAngle original, double angleChangeQuantity,
                                                @NotNull Axis axis, boolean reverse) {
        return !reverse ?
            add(original, angleChangeQuantity, axis) :
            subtract(original, angleChangeQuantity, axis);
    }

    private static @NotNull EulerAngle add(@NotNull EulerAngle original,
                                           double angleChangeQuantity, @NotNull Axis axis) {
        var rad = angleChangeQuantity * RADIAN;

        return switch (axis) {
            case X -> original.add(rad, 0, 0);
            case Y -> original.add(0, rad, 0);
            case Z -> original.add(0, 0, rad);
        };
    }

    private static @NotNull EulerAngle subtract(@NotNull EulerAngle original,
                                                double angleChangeQuantity, @NotNull Axis axis) {
        var rad = angleChangeQuantity * RADIAN;

        return switch (axis) {
            case X -> original.subtract(rad, 0, 0);
            case Y -> original.subtract(0, rad, 0);
            case Z -> original.subtract(0, 0, rad);
        };
    }
}
