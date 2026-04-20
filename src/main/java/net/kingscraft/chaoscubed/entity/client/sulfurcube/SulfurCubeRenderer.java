package net.kingscraft.chaoscubed.entity.client.sulfurcube;

import net.kingscraft.chaoscubed.ChaosCubed;
import net.kingscraft.chaoscubed.entity.SulfurCubeEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.Identifier;


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

        state.isBaby = entity.isBaby();
        state.ageInTicks = (float) entity.tickCount + partialTick;

        // ─────────────────────────────
        //  PHYSICS INFO
        // ─────────────────────────────
        double vy = entity.getDeltaMovement().y;

        boolean currentGround = entity.onGround();
        boolean previousGround = state.isOnGround;

        // velocity-based logic
        state.fallSpeed = (float) vy;
        state.isFalling = vy < -0.08;

        //  prediction (THIS removes "late feeling")
        state.willLandSoon = state.isFalling && currentGround;

        // actual landing detection
        state.justLanded = !previousGround && currentGround;

        state.wasOnGround = previousGround;
        state.isOnGround = currentGround;
    }

    @Override
    public Identifier getTextureLocation(SulfurCubeRenderState state) {
        return Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "textures/entity/sulfur_cube.png");
    }

    @Override
    protected boolean shouldShowName(SulfurCubeEntity entity, double distance) {
        return false;
    }
}
