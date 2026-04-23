package net.kingscraft.chaoscubed.worldgen.structure;

import com.mojang.serialization.MapCodec;
import net.kingscraft.chaoscubed.ChaosCubed;
import net.kingscraft.chaoscubed.worldgen.WorldGeneration;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

public class SulfurPoolStructure extends Structure {
    public static final MapCodec<SulfurPoolStructure> CODEC = simpleCodec(SulfurPoolStructure::new);

    public SulfurPoolStructure(StructureSettings structureSettings) {
        super(structureSettings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        // 1. Get the X and Z center, but we don't care about Y=0 here
        BlockPos centerPos = context.chunkPos().getMiddleBlockPosition(0);

        // [Biome Check Logic - Keep this as is, it works!]
        int quarterX = centerPos.getX() >> 2;
        int quarterY = -5;
        int quarterZ = centerPos.getZ() >> 2;
        var biomeAtDepth = context.biomeSource().getNoiseBiome(quarterX, quarterY, quarterZ, context.randomState().sampler());

        if (!biomeAtDepth.is(WorldGeneration.SULFUR_CAVES)) {
            return Optional.empty();
        }

        // 2. Find the actual Surface Y
        int surfaceY = context.chunkGenerator().getFirstFreeHeight(
                centerPos.getX(),
                centerPos.getZ(),
                Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(),
                context.randomState()
        );

        // Create the final position on the surface
        BlockPos finalSpawnPos = new BlockPos(centerPos.getX(), surfaceY, centerPos.getZ());

        ChaosCubed.LOGGER.info("[SulfurPool] SUCCESS: Found Sulfur Cave! Placing at Y: " + surfaceY);

        return Optional.of(new GenerationStub(finalSpawnPos, (builder) -> {
            builder.addPiece(new SulfurPoolPiece(
                    context.structureTemplateManager(),
                    Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_pool"),
                    finalSpawnPos
            ));
        }));
    }

    @Override
    public StructureType<?> type() {
        // Change this from null to your registered type
        return WorldGeneration.SULFUR_POOL_TYPE;
    }
}
