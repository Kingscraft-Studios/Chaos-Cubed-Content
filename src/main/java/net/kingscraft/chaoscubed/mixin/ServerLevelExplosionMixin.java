package net.kingscraft.chaoscubed.mixin;

import net.kingscraft.chaoscubed.entity.SulfurCubeEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelExplosionMixin {

    @Inject(
            method = "explode",
            at = @At("HEAD")
    )
    private void onExplosion(
            Entity entity,
            DamageSource damageSource,
            ExplosionDamageCalculator calculator,
            double x,
            double y,
            double z,
            float power,
            boolean fire,
            Level.ExplosionInteraction interaction,
            ParticleOptions p1,
            ParticleOptions p2,
            WeightedList<ExplosionParticleInfo> list,
            Holder<SoundEvent> sound,
            CallbackInfo ci
    ) {

        ServerLevel level = (ServerLevel)(Object)this;

        double radius = 4.0D;

        var cubes = level.getEntities(
                entity,
                new AABB(x, y, z, x, y, z).inflate(radius),
                e -> e instanceof SulfurCubeEntity
        );

        for (Entity e : cubes) {
            SulfurCubeEntity cube = (SulfurCubeEntity) e;

            cube.onExplosionNearby(power, x, y, z);
        }
    }
}