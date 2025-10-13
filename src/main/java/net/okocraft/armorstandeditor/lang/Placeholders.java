package net.okocraft.armorstandeditor.lang;

import dev.siroshun.mcmsgdef.Placeholder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.okocraft.armorstandeditor.editor.EditMode;
import net.okocraft.armorstandeditor.editor.PlayerEditor;

final class Placeholders {

    static final Placeholder<PlayerEditor.Axis> AXIS_PLACEHOLDER = axis -> Argument.component("axis", Component.translatable("armorstandeditor.axis." + axis.getName()));
    static final Placeholder<EditMode> EDIT_MODE_PLACEHOLDER = mode -> Argument.component("mode", Component.translatable("armorstandeditor.mode." + mode.getName()));
    static final Placeholder<Integer> SLOT_PLACEHOLDER = slot -> Argument.component("slot", Component.text(slot));

    private Placeholders() {
        throw new UnsupportedOperationException();
    }
}
