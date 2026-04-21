package net.kingscraft.chaoscubed.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SulfurGooProvider implements ParticleProvider<SimpleParticleType> {

    private final SpriteSet sprites;

    public SulfurGooProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }


    @Override
    public @Nullable Particle createParticle(@NonNull SimpleParticleType particleOptions, @NonNull ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, @NonNull RandomSource randomSource) {
        return new SulfurGooParticle(clientLevel, d, e, f, g, h, i, sprites);
    }
}