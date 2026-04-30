package net.kingscraft.chaoscubed.entity.goals;

import net.kingscraft.chaoscubed.entity.SulfurCubeEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SulfurCubeFloatGoal extends Goal {
    private final SulfurCubeEntity cube;

    public SulfurCubeFloatGoal(SulfurCubeEntity cube) {
        this.cube = cube;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.cube.getCubeProperties().isBuoyant() && (this.cube.isInWater() || this.cube.isInLava());
    }

    @Override
    public void tick() {
        if (this.cube.getRandom().nextFloat() < 0.8F) {
            this.cube.getJumpControl().jump();
        }
        // Use the MoveControl to steer away
        if (this.cube.getMoveControl() instanceof SulfurCubeMoveControl control) {
            control.setDirection(this.cube.getRandom().nextFloat() * 360.0F);
        }
    }
}
