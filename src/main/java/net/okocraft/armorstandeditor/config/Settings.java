package net.okocraft.armorstandeditor.config;

import com.github.siroshun09.configapi.api.value.ConfigValue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.stream.Collectors;

public final class Settings {

    public static final ConfigValue<Boolean> TOOL_ALLOW_NORMAL_FLINT =
            config -> config.getBoolean("tool.allow-normal-flint");

    public static final ConfigValue<TextComponent> TOOL_DISPLAY_NAME =
            config -> LegacyComponentSerializer.legacyAmpersand().deserialize(config.getString("tool.display-name"));

    public static final ConfigValue<List<Component>> TOOL_LORE =
            config ->
                    config.getStringList("tool.lore")
                            .stream()
                            .map(LegacyComponentSerializer.legacyAmpersand()::deserialize)
                            .collect(Collectors.toList());
}
