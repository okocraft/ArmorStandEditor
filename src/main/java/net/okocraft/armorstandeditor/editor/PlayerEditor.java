package net.okocraft.armorstandeditor.editor;

import net.okocraft.armorstandeditor.data.ArmorStandData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerEditor {

    private final Player player;
    private final Map<Integer, ArmorStandData> copiedArmorStands = Collections.synchronizedMap(new HashMap<>());
    private final Set<UUID> lockedArmorStand = Collections.synchronizedSet(new HashSet<>());

    private Axis axis = Axis.X;
    private EditMode mode = EditMode.NONE;
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

    public @NotNull EditMode getMode() {
        return mode;
    }

    public void setMode(@NotNull EditMode mode) {
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
        copiedArmorStands.put(selectedCopySlot, ArmorStandData.create(source));
    }

    public @Nullable ArmorStandData getSelectedArmorStand() {
        return copiedArmorStands.get(selectedCopySlot);
    }

    public void lock(@NotNull ArmorStand armorStand) {
        lockedArmorStand.add(armorStand.getUniqueId());
    }

    public void unlock(@NotNull ArmorStand armorStand) {
        lockedArmorStand.remove(armorStand.getUniqueId());
    }

    public boolean isLocked(@NotNull ArmorStand armorStand) {
        return lockedArmorStand.contains(armorStand.getUniqueId());
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
