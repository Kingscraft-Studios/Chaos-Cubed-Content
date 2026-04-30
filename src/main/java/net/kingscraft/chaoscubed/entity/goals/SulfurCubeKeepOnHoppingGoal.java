package net.kingscraft.chaoscubed.entity.goals;

import net.kingscraft.chaoscubed.entity.SulfurCubeEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SulfurCubeKeepOnHoppingGoal extends Goal {
    private final SulfurCubeEntity cube;

    public SulfurCubeKeepOnHoppingGoal(SulfurCubeEntity cube) {
        this.cube = cube;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !this.cube.hasAbsorbedBlock();
    }

    @Override
    public void tick() {
        // FIX: Access speed through the proper MoveControl method
        this.cube.getMoveControl().setWantedPosition(this.cube.getX(), this.cube.getY(), this.cube.getZ(), 1.0D);
    }
}
