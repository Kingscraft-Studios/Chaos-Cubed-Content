package net.kingscraft.chaoscubed.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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

        BiomeDefaultFeatures.addMeadowVegetation(gen);
        BiomeDefaultFeatures.addPlainVegetation(gen);


        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        BiomeDefaultFeatures.addNearWaterVegetation(gen);

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, AquaticPlacements.SEAGRASS_RIVER);

        // Add your cave feature
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION,
                featureLookup.getOrThrow(WorldGeneration.SULFUR_CAVE_PLACED));

        // Simple effects using standard Minecraft colors (Overworld water is 4159233)
        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .waterColor(4159204)// Standard blue water
                .build();

        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();

        BiomeDefaultFeatures.plainsSpawns(spawns);

        spawns.addSpawn(MobCategory.WATER_AMBIENT,
                10,
                new MobSpawnSettings.SpawnerData(EntityType.COD, 3, 6));

        spawns.addSpawn(MobCategory.WATER_AMBIENT,
                5,
                new MobSpawnSettings.SpawnerData(EntityType.SALMON, 2, 5));

        spawns.addSpawn(MobCategory.WATER_CREATURE,
                8,
                new MobSpawnSettings.SpawnerData(EntityType.SQUID, 2, 4));

        spawns.addSpawn(MobCategory.MONSTER,
                5,
                new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 1, 1));

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
