package net.kingscraft.chaoscubed.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

public class CCBiome {
    public static void configure(BootstrapContext<Biome> context) {
        var featureLookup = context.lookup(Registries.PLACED_FEATURE);
        var carverLookup = context.lookup(Registries.CONFIGURED_CARVER);

        // Default generation settings
        BiomeGenerationSettings.Builder gen = new BiomeGenerationSettings.Builder(featureLookup, carverLookup);

        BiomeDefaultFeatures.addDefaultCarversAndLakes(gen); // Carves out the actual cave shapes
        BiomeDefaultFeatures.addDefaultCrystalFormations(gen); // Amethyst, etc.
        BiomeDefaultFeatures.addDefaultMonsterRoom(gen); // Spawner Dungeons
        BiomeDefaultFeatures.addDefaultUndergroundVariety(gen); // Dirt/Gravel/Tuff patches
        BiomeDefaultFeatures.addDefaultOres(gen); // Coal, Iron, Copper, Gold, Redstone, Lapis, Diamond
        BiomeDefaultFeatures.addDefaultSprings(gen); // Water and Lava drips/pools

        // Add your cave feature
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION,
                featureLookup.getOrThrow(WorldGeneration.SULFUR_CAVE_PLACED));

        // Simple effects using standard Minecraft colors (Overworld water is 4159233)
        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .waterColor(4159233)// Standard blue water
                .build();

        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns(spawns);

        context.register(WorldGeneration.SULFUR_CAVES, new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.5f)
                .downfall(0.5f)
                .specialEffects(effects)
                .generationSettings(gen.build())
                .mobSpawnSettings(spawns.build())
                .build());
    }
}
