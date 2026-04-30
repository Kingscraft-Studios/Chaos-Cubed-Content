package net.kingscraft.chaoscubed.entity.goals;

import net.kingscraft.chaoscubed.entity.SulfurCubeEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class SulfurCubeLookAndPauseGoal extends Goal {
    private final SulfurCubeEntity cube;
    // Changed from Mob to Player
    private final Class<Player> lookTargetType;
    private final float lookDistance;
    private int lookTime;
    private Player target;

    public SulfurCubeLookAndPauseGoal(SulfurCubeEntity cube, Class<Player> lookTargetType, float lookDistance) {
        this.cube = cube;
        this.lookTargetType = lookTargetType;
        this.lookDistance = lookDistance;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.cube.hasAbsorbedBlock()) {
            return false;
        }

        this.target = this.cube.level().getNearestPlayer(this.cube, this.lookDistance);
        return this.target != null;
    }

    @Override
    public void start() {
        this.lookTime = 40 + this.cube.getRandom().nextInt(40);
    }

    @Override
    public boolean canContinueToUse() {
        // Keep looking as long as the player is close and time hasn't run out
        return this.target != null && this.target.isAlive() && this.cube.distanceToSqr(this.target) <= (double)(this.lookDistance * this.lookDistance) && this.lookTime > 0;
    }

    @Override
    public void tick() {
        this.lookTime--;
        if (this.target != null) {
            this.cube.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        }

        // Kill all movement velocity while watching the player
        this.cube.setZza(0.0F);
        this.cube.setXxa(0.0F);
        this.cube.setSpeed(0.0F);

        // Sync the MoveControl direction so it doesn't "snap" back to a weird angle
        if (this.cube.getMoveControl() instanceof SulfurCubeMoveControl control) {
            control.setDirection(this.cube.getYRot());
        }
    }

    @Override
    public void stop() {
        this.target = null;
    }
}
