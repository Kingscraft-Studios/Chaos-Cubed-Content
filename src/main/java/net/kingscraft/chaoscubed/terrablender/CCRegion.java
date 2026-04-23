package net.kingscraft.chaoscubed.terrablender;

import com.mojang.datafixers.util.Pair;
import net.kingscraft.chaoscubed.worldgen.WorldGeneration;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
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
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            // We create a very specific point that only targets the "Deep Underground"
            // and "Warm/Dry" conditions.
            Climate.ParameterPoint sulfurCaveCondition = new ParameterUtils.ParameterPointListBuilder()
                    .temperature(ParameterUtils.Temperature.WARM, ParameterUtils.Temperature.HOT)
                    .humidity(ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.NEUTRAL)
                    // We use the 'Caves' depth helper from TerraBlender
                    .depth(ParameterUtils.Depth.UNDERGROUND, ParameterUtils.Depth.FLOOR)
                    .build().get(0);

            // Instead of adding a new point that leaks, we tell TerraBlender:
            // "In areas that match sulfurCaveCondition, replace the local cave with Sulfur Caves"
            builder.replaceBiome(Biomes.LUSH_CAVES, WorldGeneration.SULFUR_CAVES);
            builder.replaceBiome(Biomes.DRIPSTONE_CAVES, WorldGeneration.SULFUR_CAVES);
        });
    }
}
