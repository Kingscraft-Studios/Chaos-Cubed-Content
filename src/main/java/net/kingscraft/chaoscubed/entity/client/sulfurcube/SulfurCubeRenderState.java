package net.kingscraft.chaoscubed.entity.client.sulfurcube;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.level.block.state.BlockState;

public class SulfurCubeRenderState extends LivingEntityRenderState {
    public float squishAmount;
    public BlockState absorbedBlockState;
    public boolean hasAbsorbedBlock;
}
