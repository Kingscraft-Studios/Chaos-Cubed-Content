package net.kingscraft.chaoscubed.worldgen.biome;

import com.mojang.serialization.Codec;
import net.kingscraft.chaoscubed.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class SulfurCaveFeature extends Feature<NoneFeatureConfiguration> {

    public SulfurCaveFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        SimplexNoise noiseSampler = new SimplexNoise(random);

        if (origin.getY() > 50) {
            // ... (keep surface vent logic)
        } else {
            if (random.nextFloat() < 0.65F) { // Slightly higher chance
                int blobs = 8 + random.nextInt(6); // More blobs for complexity

                for (int i = 0; i < blobs; i++) {
                    // VERTICAL JITTER: random.nextInt(10) - 5
                    // This creates height variation so it's not a flat floor.
                    BlockPos blobCenter = origin.offset(
                            random.nextInt(10) - 5,
                            random.nextInt(10) - 5,
                            random.nextInt(10) - 5
                    );

                    // VARIABLE SHAPE: Some blobs are flat, some are tall "domes"
                    double baseSize = 14.0 + random.nextDouble() * 6.0;
                    double rx = baseSize;
                    double rz = baseSize;
                    // Randomly make some sections very tall (up to 12-15 blocks high)
                    double ry = 5.0 + random.nextDouble() * (random.nextBoolean() ? 10.0 : 3.0);

                    carveEllipsoid(world, blobCenter, rx, ry, rz, noiseSampler, origin);
                }
                return true;
            }
        }
        return false;
    }

    private void carveEllipsoid(WorldGenLevel world, BlockPos center, double rx, double ry, double rz, SimplexNoise noise, BlockPos origin) {
        int maxRx = (int) rx + 1;
        int maxRy = (int) ry + 1;
        int maxRz = (int) rz + 1;

        double blockScale = 0.11; // For the mineral veins
        double shapeScale = 0.04; // For the cave wall jaggedness

        for (int x = -maxRx; x <= maxRx; x++) {
            for (int y = -maxRy; y <= maxRy; y++) {
                for (int z = -maxRz; z <= maxRz; z++) {
                    // CAVE JITTER: We modify the distance check with noise
                    // This makes the walls look "rocky" instead of a smooth ball.
                    double noiseModifier = noise.getValue(
                            (center.getX() + x) * shapeScale,
                            (center.getY() + y) * shapeScale,
                            (center.getZ() + z) * shapeScale
                    ) * 0.2; // 20% variation in wall thickness

                    double distance = (double) (x * x) / (rx * rx) + (double) (y * y) / (ry * ry) + (double) (z * z) / (rz * rz);

                    // If distance + noise is too high, don't place anything (creates jagged walls)
                    if (distance + noiseModifier > 1.0) continue;

                    BlockPos pos = center.offset(x, y, z);

                    if (Math.abs(pos.getX() - origin.getX()) > 16 || Math.abs(pos.getZ() - origin.getZ()) > 16) continue;
                    if (pos.getY() < world.getMinY()) continue;

                    BlockState state = world.getBlockState(pos);
                    if (!state.getFluidState().isEmpty() || state.is(Blocks.GLOW_LICHEN)) continue;

                    if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
                        double noiseValue = noise.getValue(pos.getX() * blockScale, pos.getY() * blockScale, pos.getZ() * blockScale);

                        if (noiseValue > 0.35) {
                            world.setBlock(pos, ModBlocks.SULFUR_BLOCK.defaultBlockState(), 3);
                        } else if (noiseValue < -0.35) {
                            world.setBlock(pos, ModBlocks.CINNABAR_BLOCK.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }
}