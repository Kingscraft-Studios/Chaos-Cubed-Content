package net.kingscraft.chaoscubed.worldgen.structure;

import net.kingscraft.chaoscubed.blocks.ModBlocks;
import net.kingscraft.chaoscubed.worldgen.WorldGeneration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class SulfurPoolPiece extends TemplateStructurePiece {

    // Constructor 1: Used by SulfurPoolStructure during initial generation
    public SulfurPoolPiece(StructureTemplateManager manager, Identifier template, BlockPos pos) {
        super(WorldGeneration.SULFUR_POOL_PIECE, 0, manager, template, template.toString(), makeSettings(), pos);
    }

    // Constructor 2: Used by the Registry when loading the piece from a saved world
    public SulfurPoolPiece(StructureTemplateManager manager, CompoundTag tag) {
        super(WorldGeneration.SULFUR_POOL_PIECE, tag, manager, (identifier) -> makeSettings());
    }

    private static StructurePlaceSettings makeSettings() {
        return new StructurePlaceSettings()
                .setRotation(Rotation.NONE)
                .setIgnoreEntities(true);
    }

    @Override
    protected void handleDataMarker(String name, BlockPos pos, ServerLevelAccessor world, RandomSource random, BoundingBox box) {
    }

    @Override
    public void postProcess(WorldGenLevel world, StructureManager structureManager, net.minecraft.world.level.chunk.ChunkGenerator generator, RandomSource random, BoundingBox box, net.minecraft.world.level.ChunkPos chunkPos, BlockPos pos) {
        // 1. Place the NBT pool (the 5x3x5 structure)
        super.postProcess(world, structureManager, generator, random, box, chunkPos, pos);

        // 2. Identify the center of the 5x5 pool
        int centerX = this.templatePosition.getX() + 2;
        int centerZ = this.templatePosition.getZ() + 2;
        int poolBottomY = this.templatePosition.getY();

        // 3. Drill the SOLID Tube
        // We removed the 'if (xOffset == 0 && zOffset == 0)' check.
        // Now every block in the 3x3 square is a Sulfur Block.
        for (int y = poolBottomY - 1; y > -32; y--) {
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    BlockPos targetPos = new BlockPos(centerX + xOffset, y, centerZ + zOffset);

                    // Ensure we stay inside the box we expanded in the constructor
                    if (box.isInside(targetPos)) {
                        // ALL blocks in the tube are now Sulfur
                        world.setBlock(targetPos, ModBlocks.SULFUR_BLOCK.defaultBlockState(), 2);
                    }
                }
            }
        }
    }
}