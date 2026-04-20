package net.kingscraft.chaoscubed.entity.client.sulfurcube;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class SulfurCubeModel extends EntityModel<SulfurCubeRenderState> {

	public static final ModelLayerLocation LAYER =
			new ModelLayerLocation(
					Identifier.fromNamespaceAndPath("chaos_cubed", "sulfur_cube"),
					"main"
			);

	private final ModelPart bone;
	private final ModelPart inner;
	private final ModelPart outer;

	public SulfurCubeModel(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
		this.inner = this.bone.getChild("inner");
		this.outer = this.bone.getChild("outer");
	}
	public static LayerDefinition createBodyLayer() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition bone = modelPartData.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition inner = bone.addOrReplaceChild("inner", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition outer = bone.addOrReplaceChild("outer", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -17.0F, -9.0F, 18.0F, 18.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		return LayerDefinition.create(modelData, 128, 128);
	}

	@Override
	public void setupAnim(SulfurCubeRenderState state) {
		this.resetPose();

		float age = state.ageInTicks;

		// small idle motion (NOT fake bounce, just life breathing)
		float idle = Mth.sin(age * 0.15F) * 0.03F;

		float squash = 0.0F;

		// landing impact
		if (state.justLanded) {
			squash = 0.12F;
		}

		//  airborne stretch
		if (!state.isOnGround) {
			squash = -0.05F;
		}

		//  safety clamp (CRITICAL FIX for popping)
		float safeSquash = Mth.clamp(squash, -0.08F, 0.08F);

		//  anchor movement (stable + idle life)
		this.bone.y = 24.0F + idle;

		//  RESET
		this.inner.xScale = 1.0F;
		this.inner.yScale = 1.0F;
		this.inner.zScale = 1.0F;

		this.outer.xScale = 1.0F;
		this.outer.yScale = 1.0F;
		this.outer.zScale = 1.0F;

		// INNER (controlled volume-safe deformation)
		this.inner.xScale = 1.0F - safeSquash;
		this.inner.yScale = 1.0F + safeSquash * 1.2F; // reduced from 2.0
		this.inner.zScale = 1.0F - safeSquash;

		// OUTER (soft containment shell)
		this.outer.xScale = 1.0F - safeSquash * 0.5F;
		this.outer.yScale = 1.0F + safeSquash * 0.6F;
		this.outer.zScale = 1.0F - safeSquash * 0.5F;

		// OPTIONAL: slight damping on ground
		if (state.isOnGround && !state.justLanded) {
			this.inner.xScale *= 0.995F;
			this.inner.zScale *= 0.995F;
			this.outer.xScale *= 0.998F;
			this.outer.zScale *= 0.998F;
		}
	}
}