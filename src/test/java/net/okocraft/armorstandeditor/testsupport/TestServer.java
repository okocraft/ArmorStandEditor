package net.okocraft.armorstandeditor.testsupport;

import io.papermc.paper.command.brigadier.PaperCommands;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.flag.FeatureFlags;
import org.bukkit.craftbukkit.CraftRegistry;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Initializes the Paper/Minecraft registries required by real item stacks and command argument types in tests.
 * No world, network, or Bukkit server is started.
 */
public final class TestServer {

    private static boolean setUp;

    public static synchronized void setUp() {
        if (setUp) {
            return;
        }

        setUp = true;
        LoggerFactory.getLogger(TestServer.class).info("Setting the test server up.");

        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        RegistryAccess.Frozen registries = loadRegistries();
        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registries)
            .forEach(DataComponentInitializers.PendingComponents::apply);

        CraftRegistry.setMinecraftRegistry(registries);
        setUpArgumentTypes(registries);
    }

    private static RegistryAccess.Frozen loadRegistries() {
        PackRepository packs = ServerPacksSource.createVanillaTrustedRepository();
        packs.reload();
        packs.setSelected(packs.getAvailableIds(), false);

        ResourceManager resources = new MultiPackResourceManager(PackType.SERVER_DATA, packs.openAllSelected());

        List<Registry.PendingTags<?>> tags = TagLoader.loadTagsForExistingRegistries(resources, RegistryLayer.STATIC_ACCESS);
        tags.forEach(Registry.PendingTags::apply);

        RegistryAccess.Frozen loaded = RegistryDataLoader.load(
            resources,
            TagLoader.buildUpdatedLookups(RegistryLayer.STATIC_ACCESS, tags),
            RegistryDataLoader.WORLDGEN_REGISTRIES,
            Runnable::run
        ).join();

        LayeredRegistryAccess<RegistryLayer> layers = RegistryLayer.createRegistryAccess()
            .replaceFrom(RegistryLayer.WORLDGEN, loaded);
        return layers.compositeAccess().freeze();
    }

    private static void setUpArgumentTypes(RegistryAccess.Frozen registries) {
        CommandBuildContext context = CommandBuildContext.simple(registries, FeatureFlags.REGISTRY.allFlags());
        PaperCommands.INSTANCE.setDispatcher(new Commands(Commands.CommandSelection.ALL, context), context);
    }

    private TestServer() {
        throw new UnsupportedOperationException();
    }
}
