package net.kingscraft.chaoscubed.worldgen;

import net.kingscraft.chaoscubed.ChaosCubed;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class WorldGeneration {

    public static final Feature<NoneFeatureConfiguration> SULFUR_CAVE_FEATURE =
            new SulfurCaveFeature(NoneFeatureConfiguration.CODEC);

    public static final ResourceKey<PlacedFeature> SULFUR_CAVE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_caves"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> SULFUR_CAVE_CONFIGURED =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_caves"));

    public static final ResourceKey<Biome> SULFUR_CAVES =
            ResourceKey.create(Registries.BIOME,
                    Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_caves"));

    public static void register() {
        Registry.register(
                BuiltInRegistries.FEATURE,
                Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_caves"),
                SULFUR_CAVE_FEATURE
        );
    }
}