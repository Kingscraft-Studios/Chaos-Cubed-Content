package net.kingscraft.chaoscubed.entity.goals;

import net.kingscraft.chaoscubed.entity.SulfurCubeEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SulfurCubeRandomDirectionGoal extends Goal {
    private final SulfurCubeEntity cube;
    private int nextChosenTick;

    public SulfurCubeRandomDirectionGoal(SulfurCubeEntity cube) {
        this.cube = cube;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return !this.cube.hasAbsorbedBlock();
    }

    @Override
    public void tick() {
        if (--this.nextChosenTick <= 0) {
            this.nextChosenTick = 40 + this.cube.getRandom().nextInt(60);
            float newRot = (float)this.cube.getRandom().nextInt(360);

            if (this.cube.getMoveControl() instanceof SulfurCubeMoveControl control) {
                control.setDirection(newRot);
            }
        }
    }
}
