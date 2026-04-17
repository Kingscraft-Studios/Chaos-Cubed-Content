package net.kingscraft.chaoscubed.entity.client.sulfurcube;

import net.kingscraft.chaoscubed.ChaosCubed;
import net.kingscraft.chaoscubed.entity.SulfurCubeEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.Identifier;
import net.kingscraft.chaoscubed.entity.client.sulfurcube.SulfurCubeRenderState;


public class SulfurCubeRenderer extends LivingEntityRenderer<SulfurCubeEntity, SulfurCubeRenderState, SulfurCubeModel> {

    public SulfurCubeRenderer(EntityRendererProvider.Context context) {
        // 1.21.11 Constructor: Context, Model, Shadow Size
        super(context, new SulfurCubeModel(context.bakeLayer(SulfurCubeModel.LAYER)), 0.5f);
    }

    @Override
    public SulfurCubeRenderState createRenderState() {
        return new SulfurCubeRenderState();
    }

    @Override
    public void extractRenderState(SulfurCubeEntity entity, SulfurCubeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        // Use variables already in the LivingEntityRenderState you shared
        state.isBaby = entity.isBaby();

        // Custom variables for your bounce animation
        state.ageInTicks = (float)entity.tickCount + partialTick;
        state.isOnGround = entity.onGround();
    }

    @Override
    public Identifier getTextureLocation(SulfurCubeRenderState state) {
        return Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "textures/entity/sulfur_cube.png");
    }
}
