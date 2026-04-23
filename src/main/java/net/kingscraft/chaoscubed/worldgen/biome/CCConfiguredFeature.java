package net.kingscraft.chaoscubed.worldgen.biome;

import net.kingscraft.chaoscubed.worldgen.WorldGeneration;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class CCConfiguredFeature {

    public static void configure(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        // Register your sulfur cave feature
        context.register(
                WorldGeneration.SULFUR_CAVE_CONFIGURED,
                new ConfiguredFeature<>(
                        WorldGeneration.SULFUR_CAVE_FEATURE,
                        FeatureConfiguration.NONE // Use NONE for DefaultFeatureConfig
                )
        );
    }
}