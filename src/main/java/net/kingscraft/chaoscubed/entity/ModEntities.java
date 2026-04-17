package net.kingscraft.chaoscubed.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.kingscraft.chaoscubed.ChaosCubed;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final ResourceKey<EntityType<?>> SULFUR_CUBE_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "sulfur_cube"));

    public static final EntityType<SulfurCubeEntity> SULFUR_CUBE =
            Registry.register(
                    BuiltInRegistries.ENTITY_TYPE,
                    SULFUR_CUBE_KEY,
                    EntityType.Builder.of(SulfurCubeEntity::new, MobCategory.CREATURE)
                            .sized(1.0F, 1.0F)
                            .build(SULFUR_CUBE_KEY)
            );

    public static void registerModEntities() {
        FabricDefaultAttributeRegistry.register(SULFUR_CUBE, SulfurCubeEntity.createAttributes());
        ChaosCubed.LOGGER.info("Registered entities for {}", ChaosCubed.MODID);
    }
}
