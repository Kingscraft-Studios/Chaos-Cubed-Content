package net.kingscraft.chaoscubed.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.kingscraft.chaoscubed.worldgen.biome.CCBiome;
import net.kingscraft.chaoscubed.worldgen.biome.CCConfiguredFeature;
import net.kingscraft.chaoscubed.worldgen.biome.CCPlacedFeature;
import net.kingscraft.chaoscubed.worldgen.structure.CCStructureSets;
import net.kingscraft.chaoscubed.worldgen.structure.CCStructures;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class DatagenInit implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {

        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(WorldGenProvider::new);

    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, CCConfiguredFeature::configure);
        registryBuilder.add(Registries.PLACED_FEATURE, CCPlacedFeature::configure);
        registryBuilder.add(Registries.BIOME, CCBiome::configure);

        registryBuilder.add(Registries.STRUCTURE, CCStructures::configure);
        registryBuilder.add(Registries.STRUCTURE_SET, CCStructureSets::configure);
    }
}