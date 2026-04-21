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

		// 's' represents the squish amount from the entity
		float s = state.squishAmount;

		// Anchor the cube to the ground (24 is the floor level in Blockbench)
		this.bone.y = 24.0F;

		// ─────────────────────────────────────────────────────────
		// THE SLIME MATH
		// ─────────────────────────────────────────────────────────

		// As it gets shorter (Y), it gets wider (X & Z) to keep its "volume"
		float yScale = 1.0F - s;
		float xzScale = 1.0F + s;

		// Apply to the Inner Cube (The Core)
		this.inner.yScale = yScale;
		this.inner.xScale = xzScale;
		this.inner.zScale = xzScale;

		// Apply to the Outer Cube (The Shell)
		// We multiply 's' by 0.8 to make the shell move a bit less than the core
		// This creates a "jelly" layered effect
		float shellS = s * 0.8F;
		this.outer.yScale = 1.0F - shellS;
		this.outer.xScale = 1.0F + shellS;
		this.outer.zScale = 1.0F + shellS;

		// Subtle idle "breathing" using a sine wave
		float breathe = Mth.cos(state.ageInTicks * 0.1F) * 0.01F;
		this.inner.yScale += breathe;
	}
}