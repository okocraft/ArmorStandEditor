package net.okocraft.armorstandeditor.util;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

public final class AngleCalculator {

    public static final EulerAngle INITIAL_ANGLE = new EulerAngle(0, 0, 0);
    private static final double RADIAN = Math.PI / 180;

    private AngleCalculator() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull EulerAngle calculate(@NotNull EulerAngle original, double angleChangeQuantity,
                                                @NotNull PlayerEditor.Axis axis, boolean reverse) {
        return !reverse ?
                add(original, angleChangeQuantity, axis) :
                subtract(original, angleChangeQuantity, axis);
    }

    private static @NotNull EulerAngle add(@NotNull EulerAngle original,
                                           double angleChangeQuantity, @NotNull PlayerEditor.Axis axis) {
        var rad = angleChangeQuantity * RADIAN;

        switch (axis) {
            case X:
                return original.add(rad, 0, 0);
            case Y:
                return original.add(0, rad, 0);
            case Z:
                return original.add(0, 0, rad);
            default:
                return original;
        }
    }

    private static @NotNull EulerAngle subtract(@NotNull EulerAngle original,
                                                double angleChangeQuantity, @NotNull PlayerEditor.Axis axis) {
        var rad = angleChangeQuantity * RADIAN;

        switch (axis) {
            case X:
                return original.subtract(rad, 0, 0);
            case Y:
                return original.subtract(0, rad, 0);
            case Z:
                return original.subtract(0, 0, rad);
            default:
                return original;
        }
    }
}
