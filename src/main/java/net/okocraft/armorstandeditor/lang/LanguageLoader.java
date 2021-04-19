package net.okocraft.armorstandeditor.lang;

import com.github.siroshun09.configapi.common.FileConfiguration;
import com.github.siroshun09.configapi.common.util.ResourceUtils;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import com.github.siroshun09.mcmessage.translation.AbstractTranslationLoader;
import net.kyori.adventure.key.Key;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

public final class LanguageLoader extends AbstractTranslationLoader {

    private static final String DIRECTORY_NAME = "languages";

    private final ArmorStandEditorPlugin plugin;

    public LanguageLoader(@NotNull ArmorStandEditorPlugin plugin) {
        super(
                plugin.getDataFolder().toPath().resolve(DIRECTORY_NAME),
                Key.key("armorstandeditor", "language"),
                Locale.ENGLISH
        );
        this.plugin = plugin;
    }

    @Override
    protected void saveDefaultIfNotExists() throws IOException {
        var defaultFileName = getDefaultLocale() + getFileExtension();
        var defaultFile = getDirectory().resolve(defaultFileName);

        ResourceUtils.copyFromClassLoaderIfNotExists(
                plugin.getClass().getClassLoader(),
                DIRECTORY_NAME + '/' + defaultFileName,
                defaultFile
        );

        var japaneseFileName = Locale.JAPAN + getFileExtension();
        var japaneseFile = getDirectory().resolve(japaneseFileName);

        ResourceUtils.copyFromClassLoaderIfNotExists(
                plugin.getClass().getClassLoader(),
                DIRECTORY_NAME + '/' + japaneseFileName,
                japaneseFile
        );
    }

    @Override
    protected @NotNull FileConfiguration createFileConfiguration(@NotNull Path path) {
        return YamlConfiguration.create(path);
    }
}
