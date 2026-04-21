package net.kingscraft.chaoscubed.entity;

import net.kingscraft.chaoscubed.particles.ModParticles;
import net.kingscraft.chaoscubed.sounds.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public class SulfurCubeEntity extends PathfinderMob {

    public SulfurCubeEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
        // Fix: Use the custom move control
        this.moveControl = new SulfurCubeMoveControl(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SulfurCubeFloatGoal(this));
        // Priority 1: Look at the player and PAUSE
        this.goalSelector.addGoal(1, new SulfurCubeLookAndPauseGoal(this, Player.class, 5.0F));
        this.goalSelector.addGoal(2, new SulfurCubeRandomDirectionGoal(this));
        this.goalSelector.addGoal(3, new SulfurCubeKeepOnHoppingGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.SULFUR_CUBE_DEATH;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.SULFUR_CUBE_HURT;
    }

    @Override
    public void jumpFromGround() {
        super.jumpFromGround();
        this.playSound(ModSounds.SULFUR_CUBE_JUMP, 1.0F, 1.0F + (this.random.nextFloat() * 0.1F));
    }

    public float targetSquish;
    public float prevSquish;
    public float squish;

    @Override
    public void aiStep() {
        super.aiStep(); // Moved to the top to ensure physics are processed

        this.prevSquish = this.squish;

        // 1. SPRING PHYSICS
        this.squish += (this.targetSquish - this.squish) * 0.5F;
        this.targetSquish *= 0.6F;

        // 2. THE LANDING TRIGGER
        if (this.onGround()) {
            if (this.prevSquish < 0.0F) {
                float impact = (float)Math.abs(this.getDeltaMovement().y);
                this.targetSquish = impact * 2.0F;

                this.spawnSqualParticles();
                this.playSound(ModSounds.SULFUR_CUBE_SQUISH, 1.0F, 1.0F);
            }
        } else {
            // 3. THE JUMP STRETCH (Visual cue only)
            this.targetSquish = -0.15F;
        }
    }

    private void spawnSqualParticles() {
        // Slimes spawn particles proportional to their size
        int count = this.isBaby() ? 3 : 10;

        for (int i = 0; i < count; ++i) {
            // Randomize position around the base of the cube
            float angle = this.random.nextFloat() * ((float)Math.PI * 2F);
            float distance = this.random.nextFloat() * 0.6F + 0.4F;
            double xOff = (double)(Mth.sin(angle) * distance);
            double zOff = (double)(Mth.cos(angle) * distance);

            // SNEEZE is the best "Sulfur" particle (Yellow/Green cloud)
            this.level().addParticle(
                    ModParticles.SULFUR_GOO,
                    this.getX() + xOff,
                    this.getY() + 0.1D, // Slightly above ground level
                    this.getZ() + zOff,
                    0.0D, 0.0D, 0.0D
            );
        }
    }

    static class SulfurCubeLookAndPauseGoal extends Goal {
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
            // Simplified search for the nearest player
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

    // --- CUSTOM CONTROLS ---

    static class SulfurCubeMoveControl extends MoveControl {
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
                    // FIX: Use the 'speedModifier' directly since we are inside the subclass
                    float currentSpeed = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    this.mob.setSpeed(currentSpeed);

                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = 10 + this.cube.getRandom().nextInt(20);
                        this.cube.getJumpControl().jump();
                    } else {
                        this.cube.xxa = 0.0F;
                        this.cube.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
            }
        }
    }

    // --- CUSTOM GOALS ---

    static class SulfurCubeFloatGoal extends Goal {
        private final SulfurCubeEntity cube;

        public SulfurCubeFloatGoal(SulfurCubeEntity cube) {
            this.cube = cube;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.cube.isInWater() || this.cube.isInLava();
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

    static class SulfurCubeKeepOnHoppingGoal extends Goal {
        private final SulfurCubeEntity cube;

        public SulfurCubeKeepOnHoppingGoal(SulfurCubeEntity cube) {
            this.cube = cube;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            // FIX: Access speed through the proper MoveControl method
            this.cube.getMoveControl().setWantedPosition(this.cube.getX(), this.cube.getY(), this.cube.getZ(), 1.0D);
        }
    }

    static class SulfurCubeRandomDirectionGoal extends Goal {
        private final SulfurCubeEntity cube;
        private int nextChosenTick;

        public SulfurCubeRandomDirectionGoal(SulfurCubeEntity cube) {
            this.cube = cube;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
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
}