package net.okocraft.armorstandeditor.util;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LocationCalculatorTest {

    private static final double DELTA = 1.0E-12;

    @Test
    void testCalculateAddsToSelectedAxis() {
        for (Axis axis : Axis.values()) {
            Location result = LocationCalculator.calculate(new Location(null, 1, 2, 3), 0.5, axis, false);

            Assertions.assertEquals(1 + (axis == Axis.X ? 0.5 : 0), result.getX(), DELTA);
            Assertions.assertEquals(2 + (axis == Axis.Y ? 0.5 : 0), result.getY(), DELTA);
            Assertions.assertEquals(3 + (axis == Axis.Z ? 0.5 : 0), result.getZ(), DELTA);
        }
    }

    @Test
    void testCalculateSubtractsFromSelectedAxisWhenReversed() {
        for (Axis axis : Axis.values()) {
            Location result = LocationCalculator.calculate(new Location(null, 1, 2, 3), 0.5, axis, true);

            Assertions.assertEquals(1 - (axis == Axis.X ? 0.5 : 0), result.getX(), DELTA);
            Assertions.assertEquals(2 - (axis == Axis.Y ? 0.5 : 0), result.getY(), DELTA);
            Assertions.assertEquals(3 - (axis == Axis.Z ? 0.5 : 0), result.getZ(), DELTA);
        }
    }

    @Test
    void testCalculateKeepsRotation() {
        Location original = new Location(null, 1, 2, 3, 45, -15);

        Location result = LocationCalculator.calculate(original, 1, Axis.X, false);

        Assertions.assertEquals(45, result.getYaw(), DELTA);
        Assertions.assertEquals(-15, result.getPitch(), DELTA);
    }
}
