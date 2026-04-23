package net.kingscraft.chaoscubed.worldgen.structure;

import net.kingscraft.chaoscubed.worldgen.WorldGeneration;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

public class CCStructureSets {
    public static void configure(BootstrapContext<StructureSet> context) {
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

        context.register(WorldGeneration.SULFUR_POOL_SET, new StructureSet(
                structures.getOrThrow(WorldGeneration.SULFUR_POOL_STRUCTURE),
                new RandomSpreadStructurePlacement(
                        15, // Spacing: Try once every 15x15 chunks
                        10,  // Separation: Minimum 10 chunks apart
                        RandomSpreadType.LINEAR,
                        143576238
                )
        ));
    }
}
