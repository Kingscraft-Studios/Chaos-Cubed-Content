package net.kingscraft.chaoscubed.terrablender;

import com.mojang.datafixers.util.Pair;
import net.kingscraft.chaoscubed.worldgen.WorldGeneration;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.ParameterUtils;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class CCRegion extends Region {
    public CCRegion(Identifier name, RegionType type, int weight) {
        super(name, type, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // Register sulfur caves in a small subset of underground climate points so they form pockets
        // instead of replacing all dripstone caves across the world.
        this.addBiome(
                mapper,
                ParameterUtils.Temperature.WARM,
                ParameterUtils.Humidity.DRY,
                ParameterUtils.Continentalness.MID_INLAND,
                ParameterUtils.Erosion.EROSION_4,
                ParameterUtils.Weirdness.LOW_SLICE_NORMAL_DESCENDING,
                ParameterUtils.Depth.UNDERGROUND,
                0.0F,
                WorldGeneration.SULFUR_CAVES
        );
        this.addBiome(
                mapper,
                ParameterUtils.Temperature.WARM,
                ParameterUtils.Humidity.NEUTRAL,
                ParameterUtils.Continentalness.FAR_INLAND,
                ParameterUtils.Erosion.EROSION_5,
                ParameterUtils.Weirdness.VALLEY,
                ParameterUtils.Depth.UNDERGROUND,
                0.0F,
                WorldGeneration.SULFUR_CAVES
        );
        this.addBiome(
                mapper,
                ParameterUtils.Temperature.HOT,
                ParameterUtils.Humidity.DRY,
                ParameterUtils.Continentalness.FAR_INLAND,
                ParameterUtils.Erosion.EROSION_4,
                ParameterUtils.Weirdness.MID_SLICE_NORMAL_ASCENDING,
                ParameterUtils.Depth.UNDERGROUND,
                0.0F,
                WorldGeneration.SULFUR_CAVES
        );
    }
}
