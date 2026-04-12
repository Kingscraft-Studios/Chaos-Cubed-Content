package net.kingscraft.chaoscubed.worldgen;

import com.mojang.serialization.Codec; // Assuming your block class
import net.kingscraft.chaoscubed.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SulfurCaveFeature extends Feature<NoneFeatureConfiguration> {

    public SulfurCaveFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int topY = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, origin.getX(), origin.getZ());
        BlockPos surfacePos = new BlockPos(origin.getX(), topY, origin.getZ());

        if (origin.getY() > 50) {
            if (random.nextFloat() < 0.002F) {
                generateSurfaceVent(world, surfacePos, random);
                return true;
            }
        } else {
            if (random.nextFloat() < 0.25F) {
                int blobs = 3 + random.nextInt(3);
                for (int i = 0; i < blobs; i++) {
                    BlockPos blobCenter = origin.offset(random.nextInt(12) - 6, random.nextInt(4) - 2, random.nextInt(12) - 6);

                    double rx = 12.0 + random.nextDouble() * 4.0;
                    double ry = 5.0 + random.nextDouble() * 2.0;
                    double rz = 12.0 + random.nextDouble() * 4.0;

                    carveEllipsoid(world, blobCenter, rx, ry, rz, random, origin);
                }
                return true;
            }
        }
        return false;
    }

    private void carveEllipsoid(WorldGenLevel world, BlockPos center, double rx, double ry, double rz, RandomSource random, BlockPos origin) {
        int maxRx = (int) rx + 1;
        int maxRy = (int) ry + 1;
        int maxRz = (int) rz + 1;

        for (int x = -maxRx; x <= maxRx; x++) {
            for (int y = -maxRy; y <= maxRy; y++) {
                for (int z = -maxRz; z <= maxRz; z++) {
                    double distance = (double) (x * x) / (rx * rx) + (double) (y * y) / (ry * ry) + (double) (z * z) / (rz * rz);
                    if (distance > 1.0) continue;

                    BlockPos pos = center.offset(x, y, z);

                    // Chunk boundary check
                    if (Math.abs(pos.getX() - origin.getX()) > 16 || Math.abs(pos.getZ() - origin.getZ()) > 16) continue;
                    if (pos.getY() < world.getMinY()) continue;

                    BlockState state = world.getBlockState(pos);

                    if (!state.getFluidState().isEmpty()) continue;
                    if (state.is(Blocks.GLOW_LICHEN)) continue;

                    if (state.is(BlockTags.BASE_STONE_OVERWORLD)) {
                        float chance = random.nextFloat();
                        // Update CCBlocks to match your actual blocks class name
                        if (chance < 0.35F) world.setBlock(pos, ModBlocks.SULFUR_BLOCK.defaultBlockState(), 3);
                        else if (chance < 0.70F) world.setBlock(pos, ModBlocks.CINNABAR_BLOCK.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private void generateSurfaceVent(WorldGenLevel world, BlockPos pos, RandomSource random) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos p = pos.offset(x, y, z);
                    double dist = x * x + z * z;

                    if (dist < 5.0) {
                        if (y == -1) {
                            world.setBlock(p, ModBlocks.SULFUR_BLOCK.defaultBlockState(), 3);
                        } else if (y == 0) {
                            if (dist < 1.5) {
                                world.setBlock(p, Blocks.WATER.defaultBlockState(), 3);
                            } else {
                                world.setBlock(p, ModBlocks.SULFUR_BLOCK.defaultBlockState(), 3);
                            }
                        } else if (y == 1 && dist < 1.0 && random.nextBoolean()) {
                            world.setBlock(p, ModBlocks.SULFUR_BLOCK.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }
}