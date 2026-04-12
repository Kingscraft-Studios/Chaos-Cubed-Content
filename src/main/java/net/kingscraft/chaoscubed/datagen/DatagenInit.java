package net.kingscraft.chaoscubed.datagen;



import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.kingscraft.chaoscubed.worldgen.CCBiome;
import net.kingscraft.chaoscubed.worldgen.CCConfiguredFeature;
import net.kingscraft.chaoscubed.worldgen.CCPlacedFeature;
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
    }
}