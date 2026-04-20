package net.kingscraft.chaoscubed.blocks;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.kingscraft.chaoscubed.ChaosCubed;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static final Block SULFUR_BLOCK = registerBlock(
            "sulfur_block",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE)
                    .isValidSpawn((state, level, pos, type) -> true)
                    .setId(blockKey("sulfur_block")))
    );
    public static final Block CINNABAR_BLOCK = registerBlock(
            "cinnabar_block",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE)
                    .isValidSpawn((state, level, pos, type) -> true)
                    .setId(blockKey("cinnabar_block")))
    );
    public static final Block SULFUR_SLAB = registerBlock(
            "sulfur_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE_SLAB)
                    .setId(blockKey("sulfur_slab")))
    );
    public static final Block CINNABAR_SLAB = registerBlock(
            "cinnabar_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE_SLAB)
                    .setId(blockKey("cinnabar_slab")))
    );
    public static final Block SULFUR_STAIRS = registerBlock(
            "sulfur_stairs",
            new StairBlock(
                    SULFUR_BLOCK.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE_STAIRS)
                            .setId(blockKey("sulfur_stairs"))
            )
    );
    public static final Block CINNABAR_STAIRS = registerBlock(
            "cinnabar_stairs",
            new StairBlock(
                    CINNABAR_BLOCK.defaultBlockState(), // Fixed: was using Sulfur state
                    BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE_STAIRS)
                            .setId(blockKey("cinnabar_stairs"))
            )
    );
    public static final Block SULFUR_WALL = registerBlock(
            "sulfur_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE_WALL)
                    .setId(blockKey("sulfur_wall")))
    );
    public static final Block CINNABAR_WALL = registerBlock(
            "cinnabar_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE_WALL)
                    .setId(blockKey("cinnabar_wall")))
    );
    public static final Block CHISELED_SULFUR = registerBlock(
            "chiseled_sulfur",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_STONE_BRICKS)
                    .setId(blockKey("chiseled_sulfur")))
    );
    public static final Block CHISELED_CINNABAR = registerBlock(
            "chiseled_cinnabar",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_STONE_BRICKS)
                    .setId(blockKey("chiseled_cinnabar")))
    );
    public static final Block POLISHED_SULFUR = registerBlock(
            "polished_sulfur",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_DIORITE)
                    .setId(blockKey("polished_sulfur")))
    );
    public static final Block POLISHED_CINNABAR = registerBlock(
            "polished_cinnabar",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_DIORITE)
                    .setId(blockKey("polished_cinnabar")))
    );
    public static final Block POLISHED_SULFUR_SLAB = registerBlock(
            "polished_sulfur_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_DIORITE_SLAB)
                    .setId(blockKey("polished_sulfur_slab")))
    );
    public static final Block POLISHED_CINNABAR_SLAB = registerBlock(
            "polished_cinnabar_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_DIORITE_SLAB)
                    .setId(blockKey("polished_cinnabar_slab")))
    );
    public static final Block POLISHED_SULFUR_STAIRS = registerBlock(
            "polished_sulfur_stairs",
            new StairBlock(
                    POLISHED_SULFUR.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_DIORITE_STAIRS)
                            .setId(blockKey("polished_sulfur_stairs"))
            )
    );
    public static final Block POLISHED_CINNABAR_STAIRS = registerBlock(
            "polished_cinnabar_stairs",
            new StairBlock(
                    POLISHED_CINNABAR.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_DIORITE_STAIRS)
                            .setId(blockKey("polished_cinnabar_stairs"))
            )
    );
    public static final Block POLISHED_SULFUR_WALL = registerBlock(
            "polished_sulfur_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL)
                    .setId(blockKey("polished_sulfur_wall")))
    );
    public static final Block POLISHED_CINNABAR_WALL = registerBlock(
            "polished_cinnabar_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL)
                    .setId(blockKey("polished_cinnabar_wall")))
    );
    public static final Block SULFUR_BRICKS = registerBlock(
            "sulfur_bricks",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)
                    .setId(blockKey("sulfur_bricks")))
    );
    public static final Block CINNABAR_BRICKS = registerBlock(
            "cinnabar_bricks",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)
                    .setId(blockKey("cinnabar_bricks")))
    );
    public static final Block SULFUR_BRICK_SLAB = registerBlock(
            "sulfur_brick_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_SLAB)
                    .setId(blockKey("sulfur_brick_slab")))
    );
    public static final Block CINNABAR_BRICK_SLAB = registerBlock(
            "cinnabar_brick_slab",
            new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_SLAB)
                    .setId(blockKey("cinnabar_brick_slab")))
    );
    public static final Block SULFUR_BRICK_STAIRS = registerBlock(
            "sulfur_brick_stairs",
            new StairBlock(
                    SULFUR_BRICKS.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_STAIRS)
                            .setId(blockKey("sulfur_brick_stairs"))
            )
    );
    public static final Block CINNABAR_BRICK_STAIRS = registerBlock(
            "cinnabar_brick_stairs", // Fixed typo: was "cinnabar_cinnabar_stairs"
            new StairBlock(
                    CINNABAR_BRICKS.defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_STAIRS)
                            .setId(blockKey("cinnabar_brick_stairs"))
            )
    );
    public static final Block SULFUR_BRICK_WALL = registerBlock(
            "sulfur_brick_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL)
                    .setId(blockKey("sulfur_brick_wall")))
    );
    public static final Block CINNABAR_BRICK_WALL = registerBlock(
            "cinnabar_brick_wall",
            new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_WALL)
                    .setId(blockKey("cinnabar_brick_wall")))
    );

    private static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ChaosCubed.MODID, name));
    }

    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ChaosCubed.MODID, name));
    }

    private static Block registerBlock(String name, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, blockKey(name), block);
        Registry.register(BuiltInRegistries.ITEM, itemKey(name), new BlockItem(block, new Item.Properties().setId(itemKey(name))));
        return block;
    }

    public static void registerModBlocks() {
        ChaosCubed.LOGGER.info("Registering blocks for {}", ChaosCubed.MODID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
            entries.accept(SULFUR_BLOCK);
            entries.accept(SULFUR_SLAB);
            entries.accept(SULFUR_STAIRS);
            entries.accept(SULFUR_WALL);
            entries.accept(CHISELED_SULFUR);
            entries.accept(POLISHED_SULFUR);
            entries.accept(POLISHED_SULFUR_SLAB);
            entries.accept(POLISHED_SULFUR_STAIRS);
            entries.accept(POLISHED_SULFUR_WALL);
            entries.accept(SULFUR_BRICKS);
            entries.accept(SULFUR_BRICK_SLAB);
            entries.accept(SULFUR_BRICK_STAIRS);
            entries.accept(SULFUR_BRICK_WALL);
            entries.accept(CINNABAR_BLOCK);
            entries.accept(CINNABAR_SLAB);
            entries.accept(CINNABAR_STAIRS);
            entries.accept(CINNABAR_WALL);
            entries.accept(CHISELED_CINNABAR);
            entries.accept(POLISHED_CINNABAR);
            entries.accept(POLISHED_CINNABAR_SLAB);
            entries.accept(POLISHED_CINNABAR_STAIRS);
            entries.accept(POLISHED_CINNABAR_WALL);
            entries.accept(CINNABAR_BRICKS);
            entries.accept(CINNABAR_BRICK_SLAB);
            entries.accept(CINNABAR_BRICK_STAIRS);
            entries.accept(CINNABAR_BRICK_WALL);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            entries.accept(SULFUR_BLOCK);
            entries.accept(CINNABAR_BLOCK);
        });
    }
}