package net.okocraft.armorstandeditor.lang;

import com.github.siroshun09.configapi.common.util.FileUtils;
import com.github.siroshun09.configapi.common.util.ResourceUtils;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import com.github.siroshun09.mcmessage.loader.MessageLoader;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class LanguageLoader {

    private static TranslationRegistry REGISTRY;

    private LanguageLoader() {
        throw new UnsupportedOperationException();
    }

    public static void load(@NotNull ArmorStandEditorPlugin plugin) throws IOException {
        var directory = plugin.getDataFolder().toPath().resolve("languages");

        FileUtils.createDirectoriesIfNotExists(directory);

        REGISTRY = TranslationRegistry.create(Key.key("armorstandeditor", "language"));

        loadDefault(plugin, directory);
        REGISTRY.defaultLocale(Locale.JAPAN);
        GlobalTranslator.get().addSource(REGISTRY);
    }

    public static void reload(@NotNull ArmorStandEditorPlugin plugin) throws IOException {
        GlobalTranslator.get().removeSource(REGISTRY);
        load(plugin);
    }

    private static void loadDefault(@NotNull ArmorStandEditorPlugin plugin, @NotNull Path directory) throws IOException {
        var defaultLocale = Locale.JAPAN;
        var defaultFile = directory.resolve(defaultLocale.toString() + ".yml");

        ResourceUtils.copyFromClassLoaderIfNotExists(plugin.getClass().getClassLoader(), "languages/ja_JP.yml", defaultFile);

        if (!Files.exists(defaultFile)) {
            throw new IllegalStateException();
        }

        loadFile(defaultFile);
    }

    private static void loadFile(@NotNull Path yamlPath) throws IOException {
        var yaml = YamlConfiguration.create(yamlPath);
        var loader = MessageLoader.fromFileConfiguration(yaml);

        loader.load();
        loader.registerToRegistry(REGISTRY);
    }
}
