package net.kingscraft.chaoscubed.entity.goals;

import net.kingscraft.chaoscubed.entity.SulfurCubeEntity;
import net.minecraft.world.entity.ai.control.MoveControl;

public class SulfurCubeMoveControl extends MoveControl {
    private float yRot;
    private int jumpDelay;
    private final SulfurCubeEntity cube;

    public SulfurCubeMoveControl(SulfurCubeEntity cube) {
        super(cube);
        this.cube = cube;
        this.yRot = cube.getYRot();
    }

    public void setDirection(float yRot) {
        this.yRot = yRot;
    }

    @Override
    public void tick() {
        this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
        this.mob.yHeadRot = this.mob.getYRot();
        this.mob.yBodyRot = this.mob.getYRot();

        // Operation check
        if (this.operation != MoveControl.Operation.MOVE_TO) {
            this.mob.setZza(0.0F);
        } else {
            this.operation = MoveControl.Operation.WAIT;
            if (this.mob.onGround()) {
                float currentSpeed = (float) this.speedModifier * this.cube.getSpeed();
                this.mob.setSpeed(currentSpeed);

                if (this.jumpDelay-- <= 0) {
                    this.jumpDelay = 10 + this.cube.getRandom().nextInt(20);
                    this.cube.zza = 1.0F;
                    this.cube.getJumpControl().jump();
                } else {
                    this.cube.xxa = 0.0F;
                    this.cube.zza = 0.0F;
                    this.mob.setSpeed(0.0F);
                }
            } else {
                this.mob.setSpeed((float) this.speedModifier * this.cube.getSpeed());
                this.cube.zza = 1.0F;
            }
        }
    }
}
