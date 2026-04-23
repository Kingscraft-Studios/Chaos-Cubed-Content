package net.kingscraft.chaoscubed.entity.client.sulfurcube;

import net.kingscraft.chaoscubed.ChaosCubed;
import net.kingscraft.chaoscubed.entity.SulfurCubeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;


public class SulfurCubeRenderer extends LivingEntityRenderer<SulfurCubeEntity, SulfurCubeRenderState, SulfurCubeModel> {

    public SulfurCubeRenderer(EntityRendererProvider.Context context) {
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
        state.squishAmount = Mth.lerp(partialTick, entity.prevSquish, entity.squish);
        state.absorbedBlockState = entity.getAbsorbedBlockState();
        state.hasAbsorbedBlock = entity.hasAbsorbedBlock();
    }

    @Override
    protected boolean shouldShowName(SulfurCubeEntity entity, double distance) {
        return false;
    }

    @Override
    public Identifier getTextureLocation(SulfurCubeRenderState state) {
        if (state.hasAbsorbedBlock && state.absorbedBlockState != null) {
            Block block = state.absorbedBlockState.getBlock();
            if (block == Blocks.DIRT) {
                return Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "textures/entity/sulfur_cube_dirt.png");
            }
        }
        return Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "textures/entity/sulfur_cube.png");
    }

    @Override
    public @Nullable RenderType getRenderType(SulfurCubeRenderState state, boolean bl, boolean bl2, boolean bl3) {
        return RenderTypes.entityTranslucent(this.getTextureLocation(state), true);
    }
}
