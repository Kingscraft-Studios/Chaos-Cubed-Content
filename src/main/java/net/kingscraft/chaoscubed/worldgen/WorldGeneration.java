package net.kingscraft.chaoscubed.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.kingscraft.chaoscubed.ChaosCubed;
import net.kingscraft.chaoscubed.blocks.ModBlocks;
import net.kingscraft.chaoscubed.entity.ModEntities;
import net.kingscraft.chaoscubed.worldgen.biome.SulfurCaveFeature;
import net.kingscraft.chaoscubed.worldgen.structure.SulfurPoolPiece;
import net.kingscraft.chaoscubed.worldgen.structure.SulfurPoolStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public final class WorldGeneration {

    public static final ResourceKey<Biome> SULFUR_CAVES =
            ResourceKey.create(Registries.BIOME,
                    Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_caves"));

    public static final Feature<NoneFeatureConfiguration> SULFUR_CAVE_FEATURE =
            new SulfurCaveFeature(NoneFeatureConfiguration.CODEC);
    public static final ResourceKey<PlacedFeature> SULFUR_CAVE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_caves"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> SULFUR_CAVE_CONFIGURED =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_caves"));

    public static StructureType<SulfurPoolStructure> SULFUR_POOL_TYPE;
    public static StructurePieceType SULFUR_POOL_PIECE;

    // --- STRUCTURE KEYS ---
    public static final ResourceKey<Structure> SULFUR_POOL_STRUCTURE = ResourceKey.create(
            Registries.STRUCTURE,
            Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_pool")
    );

    // --- STRUCTURE SET KEYS ---
    public static final ResourceKey<StructureSet> SULFUR_POOL_SET = ResourceKey.create(
            Registries.STRUCTURE_SET,
            Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_pool")
    );

    public static void register() {
        Registry.register(
                BuiltInRegistries.FEATURE,
                Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_caves"),
                SULFUR_CAVE_FEATURE
        );

        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(WorldGeneration.SULFUR_CAVES),
                MobCategory.AMBIENT,
                ModEntities.SULFUR_CUBE,
                40,
                1,
                2
        );

        SpawnPlacements.register(
                ModEntities.SULFUR_CUBE,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING,
                (entityType, world, reason, pos, random) ->
                        !world.canSeeSky(pos) && world.getBlockState(pos.below()).is(ModBlocks.SULFUR_BLOCK)
        );

        SULFUR_POOL_TYPE = Registry.register(BuiltInRegistries.STRUCTURE_TYPE,
                Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_pool"),
                () -> SulfurPoolStructure.CODEC);


        SULFUR_POOL_PIECE = Registry.register(BuiltInRegistries.STRUCTURE_PIECE,
                Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_pool_piece"),
                (context, tag) -> new SulfurPoolPiece(context.structureTemplateManager(), tag));

    }
}
