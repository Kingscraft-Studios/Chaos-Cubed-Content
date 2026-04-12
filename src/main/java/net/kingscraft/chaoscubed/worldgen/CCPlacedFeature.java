package net.kingscraft.chaoscubed.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class CCPlacedFeature {

    // 1. PlacedFeature does NOT use <?, ?> in Mojang mappings
    public static void configure(BootstrapContext<PlacedFeature> context) {

        // 2. method is lookup() not getRegistryLookup()
        HolderGetter<ConfiguredFeature<?, ?>> lookup = context.lookup(Registries.CONFIGURED_FEATURE);

        // 3. getOrThrow() returns a Holder.Reference
        var sulfurConfigured = lookup.getOrThrow(WorldGeneration.SULFUR_CAVE_CONFIGURED);

        // 4. Corrected PlacementModifier names and YOffset -> VerticalAnchor
        List<PlacementModifier> modifiers = List.of(
                CountPlacement.of(10),
                InSquarePlacement.spread(), // SquarePlacementModifier -> InSquarePlacement
                HeightRangePlacement.uniform( // HeightRangePlacementModifier -> HeightRangePlacement
                        VerticalAnchor.bottom(), // YOffset -> VerticalAnchor
                        VerticalAnchor.absolute(50) // YOffset.fixed -> VerticalAnchor.absolute
                ),
                BiomeFilter.biome() // BiomePlacementModifier -> BiomeFilter
        );

        context.register(
                WorldGeneration.SULFUR_CAVE_PLACED,
                new PlacedFeature(sulfurConfigured, modifiers)
        );
    }
}