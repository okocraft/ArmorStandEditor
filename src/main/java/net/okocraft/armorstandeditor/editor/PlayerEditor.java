package net.okocraft.armorstandeditor.editor;

import net.okocraft.armorstandeditor.editmode.Mode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PlayerEditor {

    private final Player player;
    private final Map<Integer, ArmorStand> copiedArmorStands = new HashMap<>();

    private Axis axis = Axis.X;
    private Mode mode = Mode.NONE;
    private int selectedCopySlot = 1;
    private double angleChangeQuantity = 12;
    private double movingDistance = 1;

    public PlayerEditor(@NotNull Player player) {
        this.player = player;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Axis getAxis() {
        return axis;
    }

    public void setAxis(@NotNull Axis axis) {
        this.axis = axis;
    }

    public @NotNull Mode getMode() {
        return mode;
    }

    public void setMode(@NotNull Mode mode) {
        this.mode = mode;
    }

    public double getAngleChangeQuantity() {
        return angleChangeQuantity;
    }

    public void setAngleChangeQuantity(double angleChangeQuantity) {
        this.angleChangeQuantity = angleChangeQuantity;
    }

    public double getMovingDistance() {
        return movingDistance;
    }

    public void setMovingDistance(double movingDistance) {
        this.movingDistance = movingDistance;
    }

    public int getSelectedCopySlot() {
        return selectedCopySlot;
    }

    public void setSelectedCopySlot(int selectedCopySlot) {
        this.selectedCopySlot = selectedCopySlot;
    }

    public void copy(@NotNull ArmorStand source) {
        copiedArmorStands.put(selectedCopySlot, source);
    }

    public @Nullable ArmorStand getSelectedArmorStand() {
        return copiedArmorStands.get(selectedCopySlot);
    }

    public enum Axis {
        X("x"),
        Y("y"),
        Z("z");

        private final String name;

        Axis(@NotNull String name) {
            this.name = name;
        }

        public @NotNull String getName() {
            return name;
        }
    }
}
