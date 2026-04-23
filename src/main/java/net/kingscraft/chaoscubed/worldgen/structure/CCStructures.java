package net.kingscraft.chaoscubed.worldgen.structure;

import net.kingscraft.chaoscubed.worldgen.WorldGeneration;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

import java.util.Map;

public class CCStructures {
    public static void configure(BootstrapContext<Structure> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        context.register(WorldGeneration.SULFUR_POOL_STRUCTURE, new SulfurPoolStructure(
                new Structure.StructureSettings(
                        // Allow the structure to TRY spawning in any Overworld biome
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        Map.of(),
                        GenerationStep.Decoration.SURFACE_STRUCTURES,
                        TerrainAdjustment.NONE
                )
        ));
    }
}