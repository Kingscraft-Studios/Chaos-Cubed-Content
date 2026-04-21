package net.kingscraft.chaoscubed.entity.client.sulfurcube;

import net.kingscraft.chaoscubed.ChaosCubed;
import net.kingscraft.chaoscubed.entity.SulfurCubeEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;


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

        // Keep these basic ones
        state.isBaby = entity.isBaby();

        // 1.21.11 uses ageInTicks from the base class,
        // but if you need it specifically for your model math:
        state.ageInTicks = (float) entity.tickCount + partialTick;

        // REPLACE all the old physics code with this one line:
        // This smoothly blends the squish from the last tick to the current tick
        state.squishAmount = Mth.lerp(partialTick, entity.prevSquish, entity.squish);
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
