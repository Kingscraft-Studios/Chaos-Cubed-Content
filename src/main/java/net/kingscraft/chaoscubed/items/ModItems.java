package net.kingscraft.chaoscubed.items;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.kingscraft.chaoscubed.ChaosCubed;
import net.kingscraft.chaoscubed.entity.ModEntities;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;

public class ModItems {

    public static final Item SULFUR_SPAWN_EGG = registerItem("sulfur_cube_spawn_egg",
            new SpawnEggItem(
                    new Item.Properties()
                            .setId(itemKey("sulfur_cube_spawn_egg"))
                            .component(DataComponents.ENTITY_DATA,
                                    TypedEntityData.of(ModEntities.SULFUR_CUBE, new net.minecraft.nbt.CompoundTag()))
            )
    );

    // This method now correctly takes an ITEM
    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(ChaosCubed.MODID, name), item);
    }

    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ChaosCubed.MODID, name));
    }

    public static void registerModItems() {
        ChaosCubed.LOGGER.info("Registering items for {}", ChaosCubed.MODID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
            entries.accept(SULFUR_SPAWN_EGG);
        });
    }
}
