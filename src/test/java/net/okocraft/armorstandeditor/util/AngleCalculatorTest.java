package net.okocraft.armorstandeditor.util;

import org.bukkit.Axis;
import org.bukkit.util.EulerAngle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AngleCalculatorTest {

    private static final double DELTA = 1.0E-12;

    @Test
    void testCalculateAddsToSelectedAxis() {
        for (Axis axis : Axis.values()) {
            EulerAngle original = new EulerAngle(1, 2, 3);
            EulerAngle result = AngleCalculator.calculate(original, 12, axis, false);
            double change = Math.toRadians(12);

            Assertions.assertEquals(1 + (axis == Axis.X ? change : 0), result.getX(), DELTA);
            Assertions.assertEquals(2 + (axis == Axis.Y ? change : 0), result.getY(), DELTA);
            Assertions.assertEquals(3 + (axis == Axis.Z ? change : 0), result.getZ(), DELTA);
        }
    }

    @Test
    void testCalculateSubtractsFromSelectedAxisWhenReversed() {
        for (Axis axis : Axis.values()) {
            EulerAngle original = new EulerAngle(1, 2, 3);
            EulerAngle result = AngleCalculator.calculate(original, 12, axis, true);
            double change = Math.toRadians(12);

            Assertions.assertEquals(1 - (axis == Axis.X ? change : 0), result.getX(), DELTA);
            Assertions.assertEquals(2 - (axis == Axis.Y ? change : 0), result.getY(), DELTA);
            Assertions.assertEquals(3 - (axis == Axis.Z ? change : 0), result.getZ(), DELTA);
        }
    }

    @Test
    void testCalculateKeepsOriginalAngleUnchanged() {
        EulerAngle original = new EulerAngle(1, 2, 3);

        AngleCalculator.calculate(original, 12, Axis.X, false);

        Assertions.assertEquals(1, original.getX(), DELTA);
        Assertions.assertEquals(2, original.getY(), DELTA);
        Assertions.assertEquals(3, original.getZ(), DELTA);
    }
}
