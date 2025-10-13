package net.okocraft.armorstandeditor.editor;

import net.kyori.adventure.text.ComponentLike;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.menu.EquipmentMenuProvider;
import net.okocraft.armorstandeditor.menu.SelectionMenu;
import net.okocraft.armorstandeditor.permission.Permissions;
import net.okocraft.armorstandeditor.util.AngleCalculator;
import net.okocraft.armorstandeditor.util.ArmorStandRemover;
import net.okocraft.armorstandeditor.util.FoliaSyncTeleporter;
import net.okocraft.armorstandeditor.util.LocationCalculator;
import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.okocraft.armorstandeditor.util.AngleCalculator.INITIAL_ANGLE;

public final class EditMode {
    private static final Map<String, EditMode> BY_NAME = new LinkedHashMap<>();

    public static @Nullable EditMode byName(@NotNull String name) {
        return BY_NAME.get(name);
    }

    public static @NotNull Stream<String> names() {
        return BY_NAME.keySet().stream();
    }

    public static final EditMode NONE = create("none", (editor, armorStand, reverse) -> editor.getPlayer().openInventory(new SelectionMenu(editor.getPlayer()).getInventory()));

    public static final EditMode BASE_PLATE = toggleBooleanState("base-plate", ArmorStand::hasBasePlate, ArmorStand::setBasePlate, Messages.EDIT_BASE_PLATE_ON, Messages.EDIT_BASE_PLATE_OFF);

    public static final EditMode BODY_POSE = changePose("body", ArmorStand::getBodyPose, ArmorStand::setBodyPose);

    public static final EditMode COPY = create("copy", (editor, armorStand, reverse) -> {
        editor.copy(armorStand);
        editor.getPlayer().sendActionBar(Messages.EDIT_COPY.apply(editor.getSelectedCopySlot()));
    });

    public static final EditMode CUSTOM_NAME_VISIBLE = toggleBooleanState("custom-name-visible", ArmorStand::isCustomNameVisible, ArmorStand::setCustomNameVisible, Messages.EDIT_CUSTOM_NAME_VISIBLE_ON, Messages.EDIT_CUSTOM_NAME_VISIBLE_OFF);

    public static final EditMode EQUIPMENT = create(
        "equipment",
        (editor, armorStand, reverse) -> editor.getPlayer().openInventory(EquipmentMenuProvider.getMenu(armorStand).getInventory())
    );

    public static final EditMode GRAVITY = toggleBooleanState("gravity", ArmorStand::hasGravity, ArmorStand::setGravity, Messages.EDIT_GRAVITY_ON, Messages.EDIT_GRAVITY_OFF);

    public static final EditMode HEAD_POSE = changePose("head", ArmorStand::getHeadPose, ArmorStand::setHeadPose);

    public static final EditMode LEFT_ARM_POSE = changePose("left-arm", ArmorStand::getLeftArmPose, ArmorStand::setLeftArmPose);

    public static final EditMode LEFT_LEG_POSE = changePose("left-leg", ArmorStand::getLeftLegPose, ArmorStand::setLeftLegPose);

    public static final EditMode LOCK = create("lock", (editor, armorStand, reverse) -> {
        ComponentLike message;

        if (editor.isLocked(armorStand)) {
            editor.unlock(armorStand);
            message = Messages.EDIT_UNLOCK;
        } else {
            editor.lock(armorStand);
            message = Messages.EDIT_LOCK;
        }

        editor.getPlayer().sendActionBar(message);
    });

    public static final EditMode MOVEMENT = create("movement", (editor, armorStand, reverse) -> {
        var location = LocationCalculator.calculate(
            armorStand.getLocation(),
            editor.getMovingDistance(),
            editor.getAxis(),
            reverse
        );

        if (FoliaSyncTeleporter.isFolia()) {
            FoliaSyncTeleporter.teleport(armorStand, location);
        } else {
            armorStand.teleport(location);
        }
    });

    public static final EditMode PASTE = create("paste", (editor, armorStand, reverse) -> {
        var data = editor.getSelectedArmorStand();

        if (data == null) {
            return;
        }

        data.apply(armorStand, editor.getPlayer().getGameMode() == GameMode.CREATIVE);
        editor.getPlayer().sendActionBar(Messages.EDIT_PASTE.apply(editor.getSelectedCopySlot()));
    });

    public static final EditMode REMOVAL = create("removal", ArmorStandRemover::remove);

    public static final EditMode RESET_POSE = create("reset-pose", (editor, armorStand, reverse) -> {
        armorStand.setHeadPose(INITIAL_ANGLE);
        armorStand.setBodyPose(INITIAL_ANGLE);
        armorStand.setLeftArmPose(INITIAL_ANGLE);
        armorStand.setRightArmPose(INITIAL_ANGLE);
        armorStand.setLeftLegPose(INITIAL_ANGLE);
        armorStand.setRightLegPose(INITIAL_ANGLE);

        editor.getPlayer().sendActionBar(Messages.EDIT_RESET_POSE);
    });

    public static final EditMode RIGHT_ARM_POSE = changePose("right-arm", ArmorStand::getRightArmPose, ArmorStand::setRightArmPose);

    public static final EditMode RIGHT_LEG_POSE = changePose("right-leg", ArmorStand::getRightLegPose, ArmorStand::setRightLegPose);

    public static final EditMode ROTATION = create("rotation", (editor, armorStand, reverse) -> {
        double diff = editor.getAngleChangeQuantity() * (reverse ? -1 : 1);
        armorStand.setRotation((float) (armorStand.getYaw() + diff), armorStand.getPitch());
    });

    public static final EditMode SHOW_ARMS = toggleBooleanState("show-arms", ArmorStand::hasArms, ArmorStand::setArms, Messages.EDIT_ARMS_ON, Messages.EDIT_ARMS_OFF);

    public static final EditMode SIZE = toggleBooleanState("size", ArmorStand::isSmall, ArmorStand::setSmall, Messages.EDIT_SIZE_SMALL, Messages.EDIT_SIZE_NORMAL);

    public static final EditMode VISIBLE = toggleBooleanState("visible", ArmorStand::isVisible, ArmorStand::setVisible, Messages.EDIT_VISIBLE_ON, Messages.EDIT_VISIBLE_OFF);

    @Contract(value = "_, _ -> new", pure = true)
    private static @NotNull EditMode create(@NotNull String name, @NotNull Editor editor) {
        var mode = new EditMode(name, editor);
        BY_NAME.put(mode.getName(), mode);
        return new EditMode(name, editor);
    }

    private static @NotNull EditMode toggleBooleanState(@NotNull String name, @NotNull Function<ArmorStand, Boolean> getter, @NotNull BiConsumer<ArmorStand, Boolean> setter, @NotNull ComponentLike on, @NotNull ComponentLike off) {
        return create(name, (editor, armorStand, reverse) -> {
            boolean currentOn = getter.apply(armorStand);
            setter.accept(armorStand, !currentOn);
            editor.getPlayer().sendActionBar(currentOn ? off : on);
        });
    }

    private static @NotNull EditMode changePose(@NotNull String part, @NotNull Function<ArmorStand, EulerAngle> getter, @NotNull BiConsumer<ArmorStand, EulerAngle> setter) {
        return create(part + "-pose", (editor, armorStand, reverse) -> {
            var current = getter.apply(armorStand);
            var newAngle = AngleCalculator.calculate(current, editor.getAngleChangeQuantity(), editor.getAxis(), reverse);
            setter.accept(armorStand, newAngle);
        });
    }

    private final String name;
    private final String permission;
    private final Editor editor;

    EditMode(@NotNull String name, @NotNull Editor editor) {
        this.name = name;
        this.permission = Permissions.MODE_PREFIX + name;
        this.editor = editor;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull String getPermission() {
        return this.permission;
    }

    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        if (this != LOCK && editor.isLocked(armorStand)) {
            return;
        }

        if (editor.getPlayer().hasPermission(this.permission)) {
            this.editor.edit(editor, armorStand, reverse);
        } else {
            editor.getPlayer().sendMessage(Messages.EDIT_MODE_NO_PERMISSION);
        }
    }

    private interface Editor {
        void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse);
    }
}
