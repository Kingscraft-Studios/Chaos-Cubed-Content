package net.kingscraft.chaoscubed.entity;

import net.kingscraft.chaoscubed.entity.goals.*;
import net.kingscraft.chaoscubed.entity.properties.CubeBlockProperties;
import net.kingscraft.chaoscubed.entity.properties.SulfurCubePropertiesRegistry;
import net.kingscraft.chaoscubed.particles.ModParticles;
import net.kingscraft.chaoscubed.sounds.ModSounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SulfurCubeEntity extends PathfinderMob {
    private static final EntityDataAccessor<BlockState> DATA_ABSORBED_BLOCK =
            SynchedEntityData.defineId(SulfurCubeEntity.class, EntityDataSerializers.BLOCK_STATE);
    private static final double MIN_BOUNCE_IMPACT = 0.08D;
    private static final double MIN_BOUNCE_POWER = 0.28D;
    private static final double MAX_BOUNCE_POWER = 1.2D;
    private static final double STICKY_CONTACT_DISTANCE = 0.03D;
    private static final double SETTLE_HORIZONTAL_SPEED = 0.01D;
    private static final double STICKY_SLIDE_HORIZONTAL = 0.995D;
    private static final double STICKY_WALL_VERTICAL = 0.99D;
    private int stickyDetachTicks;

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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ABSORBED_BLOCK, Blocks.AIR.defaultBlockState());
    }

    @Override
    public void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);

        if (!this.hasAbsorbedBlock()) {
            return;
        }

        Identifier blockId = BuiltInRegistries.BLOCK.getKey(this.getAbsorbedBlockState().getBlock());
        if (blockId != null) {
            tag.putString("AbsorbedBlock", blockId.toString());
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);

        Identifier blockId = tag.getString("AbsorbedBlock").map(Identifier::tryParse).orElse(null);
        if (blockId == null) {
            this.setAbsorbedBlockState(Blocks.AIR.defaultBlockState());
            return;
        }

        Block block = BuiltInRegistries.BLOCK.getOptional(blockId).orElse(Blocks.AIR);
        this.setAbsorbedBlockState(block.defaultBlockState());
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
    public boolean isPersistenceRequired() {
        return this.hasAbsorbedBlock() || super.isPersistenceRequired();
    }

    public float targetSquish;
    public float prevSquish;
    public float squish;

    @Override
    public void aiStep() {
        // 1. Capture the velocity BEFORE super.aiStep() potentially clears it on ground impact
        Vec3 preStepVelocity = this.getDeltaMovement();

        super.aiStep();

        this.prevSquish = this.squish;

        // 2. SPRING PHYSICS (Visual Animation)
        this.squish += (this.targetSquish - this.squish) * 0.5F;
        this.targetSquish *= 0.6F;

        if (this.hasAbsorbedBlock()) {
            CubeBlockProperties props = this.getCubeProperties();
            if (this.onGround() && preStepVelocity.y < -MIN_BOUNCE_IMPACT) {
                if (props.isSticky()) {
                    this.handleLandingSquish(preStepVelocity);
                } else {
                    this.handleLandingBounce(preStepVelocity, props);
                }
            }

            if (props.isSticky()) {
                this.setNoGravity(true);
                this.applyStickyMovement(props);
            } else {
                this.setNoGravity(false);
            }

            // 4. JUMP STRETCH (Visual cue only)
            if (!this.onGround() && !props.isSticky()) {
                this.targetSquish = -0.15F;
            }
        } else {
            this.setNoGravity(false);
            if (this.onGround() && preStepVelocity.y < -MIN_BOUNCE_IMPACT) {
                this.handleLandingSquish(preStepVelocity);
            } else if (!this.onGround()) {
                this.targetSquish = -0.15F;
            }
        }
    }

    public BlockState getAbsorbedBlockState() {
        return this.entityData.get(DATA_ABSORBED_BLOCK);
    }

    public boolean hasAbsorbedBlock() {
        return !this.getAbsorbedBlockState().isAir();
    }

    public CubeBlockProperties getCubeProperties() {
        if (this.hasAbsorbedBlock()) {
            return SulfurCubePropertiesRegistry.get(this.getAbsorbedBlockState().getBlock());
        }
        return CubeBlockProperties.REGULAR;
    }

    public boolean isStickyArchetype() {
        return this.getCubeProperties().isSticky();
    }

    private void setAbsorbedBlockState(BlockState state) {
        this.entityData.set(DATA_ABSORBED_BLOCK, state);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof BlockItem blockItem) {
            BlockState state = blockItem.getBlock().defaultBlockState();
            if (!state.isAir()) {
                if (!this.level().isClientSide()) {
                    this.setAbsorbedBlockState(state);
                    this.playSound(ModSounds.SULFUR_CUBE_ABSORB, 1.0F, 0.95F + this.random.nextFloat() * 0.1F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }

                return InteractionResult.SUCCESS;
            }
        }

        if (stack.getItem() instanceof ShearsItem) {
            if (this.level() instanceof ServerLevel serverLevel) {
                BlockState stored = this.getAbsorbedBlockState();

                if (stored != null && !stored.isAir()) {

                    // Drop block
                    this.spawnAtLocation(serverLevel, new ItemStack(stored.getBlock()));

                    // Clear stored block
                    this.setAbsorbedBlockState(Blocks.AIR.defaultBlockState());

                    // Damage shears
                    stack.hurtAndBreak(1, player, hand);

                    this.playSound(ModSounds.SULFUR_CUBE_EJECT, 1.0F, 1.2F);
                } else {
                    return InteractionResult.PASS; // don’t waste durability
                }
            }

            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private void handleLandingBounce(Vec3 impactVelocity, CubeBlockProperties props) {
        double impact = -impactVelocity.y;
        double retention = Mth.clamp(0.55D + props.bounceMod() * 0.14D, 0.35D, 0.9D);
        double bouncePower = Mth.clamp(impact * retention, 0.0D, MAX_BOUNCE_POWER);

        this.handleLandingSquish(impactVelocity);

        if (bouncePower <= MIN_BOUNCE_POWER) {
            this.setDeltaMovement(
                    this.settleAxis(impactVelocity.x),
                    0.0D,
                    this.settleAxis(impactVelocity.z)
            );
            return;
        }

        double horizontalCarry = Mth.clamp((1.0D / props.dragMod()) + 0.08D, 0.85D, 1.08D);
        this.setDeltaMovement(
                impactVelocity.x * horizontalCarry,
                bouncePower,
                impactVelocity.z * horizontalCarry
        );
    }

    private void handleLandingSquish(Vec3 impactVelocity) {
        double impact = -impactVelocity.y;
        this.targetSquish = (float) Math.min(1.0D, impact * 1.6D);
        this.spawnSqualParticles();
        this.playLandingSound(impact);
    }

    private void applyStickyMovement(CubeBlockProperties props) {
        boolean touchingCeiling = this.isTouchingStickySurface(0.0D, STICKY_CONTACT_DISTANCE, 0.0D);
        boolean touchingWall = this.horizontalCollision
                || this.isTouchingStickySurface(STICKY_CONTACT_DISTANCE, 0.0D, 0.0D)
                || this.isTouchingStickySurface(-STICKY_CONTACT_DISTANCE, 0.0D, 0.0D)
                || this.isTouchingStickySurface(0.0D, 0.0D, STICKY_CONTACT_DISTANCE)
                || this.isTouchingStickySurface(0.0D, 0.0D, -STICKY_CONTACT_DISTANCE);

        boolean attached = this.onGround() || touchingWall || touchingCeiling;
        if (attached) {
            this.stickyDetachTicks = 0;
        } else if (++this.stickyDetachTicks > 20) {
            this.setNoGravity(false);
            return;
        }

        if (!attached) {
            return;
        }

        this.fallDistance = 0.0F;
        Vec3 velocity = this.getDeltaMovement();
        double contactDamping = props.dragMod() < 0.6F ? STICKY_SLIDE_HORIZONTAL : Mth.clamp(0.98D / props.dragMod(), 0.85D, 1.0D);
        double x = velocity.x * contactDamping;
        double y = velocity.y;
        double z = velocity.z * contactDamping;

        if (this.onGround()) {
            y = 0.0D;
            x *= 0.95D;
            z *= 0.95D;
        }

        if (touchingWall) {
            y *= STICKY_WALL_VERTICAL;
            x *= 1.01D;
            z *= 1.01D;
        }

        if (touchingCeiling) {
            y = 0.0D;
        }

        this.setDeltaMovement(this.settleAxis(x), this.settleAxis(y), this.settleAxis(z));
    }

    private double settleAxis(double value) {
        return Math.abs(value) < SETTLE_HORIZONTAL_SPEED ? 0.0D : value;
    }

    private boolean isTouchingStickySurface(double x, double y, double z) {
        return !this.level().noCollision(this, this.getBoundingBox().move(x, y, z));
    }

    private void playLandingSound(double impact) {
        float volume = Mth.clamp((float) impact * 1.4F, 0.35F, 1.0F);
        float pitch = 0.9F + this.random.nextFloat() * 0.2F;
        this.playSound(ModSounds.SULFUR_CUBE_SQUISH, volume, pitch);
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


            double x = this.getX() + xOff;
            double y = this.getY() + 0.1D;
            double z = this.getZ() + zOff;

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ModParticles.SULFUR_GOO, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            } else {
                this.level().addParticle(ModParticles.SULFUR_GOO, x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.hasAbsorbedBlock()) {
            CubeBlockProperties props = this.getCubeProperties();

            // 1. BUOYANCY: If the archetype doesn't float, apply extra downward force in liquids
            if (!props.isBuoyant() && (this.isInWater() || this.isInLava())) {
                Vec3 velocity = this.getDeltaMovement();
                // Sinking force: helps it drop to the bottom like the Heavy archetype
                this.setDeltaMovement(velocity.x, velocity.y - 0.05D, velocity.z);
            }

            // 2. DRAG / FRICTION
            // Standard Minecraft air/ground friction is ~0.98.
            // We use dragMod to increase or decrease this resistance.
            if (!props.isSticky() && props.dragMod() != 1.0f) {
                Vec3 vel = this.getDeltaMovement();
                double horizontalDrag = Mth.clamp(0.98D / props.dragMod(), 0.65D, 1.02D);
                this.setDeltaMovement(vel.x * horizontalDrag, vel.y, vel.z * horizontalDrag);
            }
        }
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (this.hasAbsorbedBlock()) {
            CubeBlockProperties props = this.getCubeProperties();

            // Apply archetype bounciness to the initial push
            double finalStrength = strength * props.knockbackMod();
            super.knockback(finalStrength, x, z);

            // Vertical Pop: High bounce blocks jump higher when hit
            if (!props.isSticky() && props.bounceMod() > 1.0f) {
                Vec3 vel = this.getDeltaMovement();
                this.setDeltaMovement(vel.x, Math.min(0.9D, 0.35D * props.bounceMod()), vel.z);
            }
        } else {
            super.knockback(strength, x, z);
        }
    }

    @Override
    public void jumpFromGround() {
        float jumpPower = 0.42F;

        if (this.hasAbsorbedBlock()) {
            CubeBlockProperties props = this.getCubeProperties();
            if (props.isSticky()) {
                return;
            }
            jumpPower *= Mth.clamp(props.bounceMod(), 0.15F, 1.9F);
        }

        if (jumpPower <= 0.05F) {
            return;
        }

        Vec3 velocity = this.getDeltaMovement();
        this.setDeltaMovement(velocity.x, jumpPower, velocity.z);

        this.playSound(ModSounds.SULFUR_CUBE_JUMP, 1.0F, 1.0F + (this.random.nextFloat() * 0.1F));
    }

    @Override
    public float getSpeed() {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.getCubeProperties().speedMod();
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        if (this.hasAbsorbedBlock()) {
            CubeBlockProperties props = this.getCubeProperties();
            boolean bypassProtection = damageSource.is(DamageTypes.FELL_OUT_OF_WORLD)
                    || damageSource.is(DamageTypes.GENERIC_KILL);

            if (bypassProtection) {
                return super.hurtServer(level, damageSource, amount);
            }

            float hitScale = props.hitResponseScale(amount);
            if (damageSource.getDirectEntity() instanceof LivingEntity attacker) {
                this.knockback(0.4F * hitScale, attacker.getX() - this.getX(), attacker.getZ() - this.getZ());
            }

            this.playSound(ModSounds.SULFUR_CUBE_HURT, 0.5F, 1.2F);
            if (props.isSticky()) {
                Vec3 velocity = this.getDeltaMovement();
                this.setDeltaMovement(velocity.x * hitScale, velocity.y, velocity.z * hitScale);
            } else {
                Vec3 velocity = this.getDeltaMovement();
                double boostedBounce = Math.min(0.9D, velocity.y + (0.08D * hitScale));
                this.setDeltaMovement(velocity.x * hitScale, boostedBounce, velocity.z * hitScale);
            }
            this.spawnSqualParticles();
            return false;
        }
        return super.hurtServer(level, damageSource, amount);
    }
}
