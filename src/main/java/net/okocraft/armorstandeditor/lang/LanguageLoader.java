package net.okocraft.armorstandeditor.lang;

import com.github.siroshun09.adventureextender.loader.translation.YamlTranslationLoader;
import com.github.siroshun09.configapi.api.util.ResourceUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.TranslationRegistry;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

public final class LanguageLoader extends YamlTranslationLoader {

    private static final String DIRECTORY_NAME = "languages";

    private final ArmorStandEditorPlugin plugin;

    public LanguageLoader(@NotNull ArmorStandEditorPlugin plugin) {
        super(plugin.getDataFolder().toPath().resolve(DIRECTORY_NAME), Locale.ENGLISH);
        this.plugin = plugin;
    }

    @Override
    protected void saveDefaultIfNotExists() throws IOException {
        var defaultFileName = getDefaultLocale() + ".yml";
        var defaultFile = getDirectory().resolve(defaultFileName);

        ResourceUtils.copyFromJarIfNotExists(
                plugin.getJarFile(),
                DIRECTORY_NAME + '/' + defaultFileName,
                defaultFile
        );

        var japaneseFileName = Locale.JAPAN + ".yml";
        var japaneseFile = getDirectory().resolve(japaneseFileName);

        ResourceUtils.copyFromJarIfNotExists(
                plugin.getJarFile(),
                DIRECTORY_NAME + '/' + japaneseFileName,
                japaneseFile
        );
    }

    @Override
    protected @NotNull TranslationRegistry createRegistry() {
        return TranslationRegistry.create(Key.key("armorstandeditor", "language"));
    }
}
