package net.kingscraft.chaoscubed.entity.client.sulfurcube;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class SulfurCubeRenderState extends LivingEntityRenderState {
    public boolean wasOnGround;
    public boolean justLanded;
    public boolean isOnGround;
    public boolean isFalling;
    public boolean willLandSoon;
    public float fallSpeed;
}