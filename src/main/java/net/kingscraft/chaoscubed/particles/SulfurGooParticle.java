package net.kingscraft.chaoscubed.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;

public class SulfurGooParticle extends SingleQuadParticle {

    private final SpriteSet sprites;

    protected SulfurGooParticle(
            ClientLevel level,
            double x, double y, double z,
            double dx, double dy, double dz,
            SpriteSet sprites
    ) {
        // Start with the first sprite in your JSON list (index 0)
        super(level, x, y, z, sprites.get(0, 1));

        this.sprites = sprites;

        // This picks the correct sprite based on age (0) vs lifetime
        this.setSpriteFromAge(sprites);

        this.xd = dx;
        this.yd = dy;
        this.zd = dz;

        this.gravity = 0.2f;
        this.friction = 0.98f;

        // Lifetime determines how fast it cycles through the 4 PNGs
        // If lifetime is 20, it will spend 5 ticks on each PNG slice
        this.lifetime = 20 + this.random.nextInt(10);
    }

    @Override
    public void tick() {
        super.tick();

        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;

        this.yd -= 0.04 * this.gravity;

        // This is the "Magic": it looks at your age (0, 1, 2...)
        // and picks the matching sprite from your 4-entry JSON list automatically.
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }
}