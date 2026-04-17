package net.kingscraft.chaoscubed.entity.client.sulfurcube;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.kingscraft.chaoscubed.entity.client.sulfurcube.SulfurCubeRenderState;

public class SulfurCubeModel extends EntityModel<SulfurCubeRenderState> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(
                    Identifier.fromNamespaceAndPath("chaos_cubed", "sulfur_cube"),
                    "main"
            );

    private final ModelPart cube;

    public SulfurCubeModel(ModelPart root) {
        super(root);
        this.cube = root.getChild("cube");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "cube",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        // Change the Y origin from -8 to -16
                        // This makes the box sit ON TOP of the pivot point
                        .addBox(-8, -16, -8, 16, 16, 16),
                PartPose.offset(0, 24, 0)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(SulfurCubeRenderState state) {
        resetPose();
        float age = state.ageInTicks;

        float bounce = Mth.sin(age * 0.4F) * 0.25F;

        // squash/stretch illusion
        float squash = Mth.clamp(bounce, -0.15F, 0.15F);

        cube.y = 24.0F + bounce;

        cube.xScale = 1.0F + (-squash);
        cube.zScale = 1.0F + (-squash);
        cube.yScale = 1.0F + squash;
    }
}
