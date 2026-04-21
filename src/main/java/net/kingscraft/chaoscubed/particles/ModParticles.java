package net.kingscraft.chaoscubed.particles;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.kingscraft.chaoscubed.ChaosCubed;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;

public class ModParticles {
    public static final SimpleParticleType SULFUR_GOO = FabricParticleTypes.simple();

    public static void registerParticles() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE,
                Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_cube_goo"),
                SULFUR_GOO);
    }
}