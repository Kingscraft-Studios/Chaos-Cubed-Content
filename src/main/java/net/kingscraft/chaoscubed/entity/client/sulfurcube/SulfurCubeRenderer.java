package net.kingscraft.chaoscubed.entity.client.sulfurcube;

import net.kingscraft.chaoscubed.ChaosCubed;
import net.kingscraft.chaoscubed.entity.SulfurCubeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;


public class SulfurCubeRenderer extends LivingEntityRenderer<SulfurCubeEntity, SulfurCubeRenderState, SulfurCubeModel> {

    public SulfurCubeRenderer(EntityRendererProvider.Context context) {
        super(context, new SulfurCubeModel(context.bakeLayer(SulfurCubeModel.LAYER)), 0.5f);
    }

    @Override
    public SulfurCubeRenderState createRenderState() {
        return new SulfurCubeRenderState();
    }

    @Override
    public void extractRenderState(SulfurCubeEntity entity, SulfurCubeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        state.isBaby = entity.isBaby();
        state.ageInTicks = (float) entity.tickCount + partialTick;
        state.squishAmount = Mth.lerp(partialTick, entity.prevSquish, entity.squish);
        state.absorbedBlockState = entity.getAbsorbedBlockState();
        state.hasAbsorbedBlock = entity.hasAbsorbedBlock();
    }

    @Override
    protected boolean shouldShowName(SulfurCubeEntity entity, double distance) {
        return false;
    }

    @Override
    public Identifier getTextureLocation(SulfurCubeRenderState state) {
        // SAFETY: If there is no block state, return the base sulfur cube texture immediately
        if (state.absorbedBlockState == null || !state.hasAbsorbedBlock) {
            return Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "textures/entity/sulfur_cube.png");
        }

        Block block = state.absorbedBlockState.getBlock();
        return getTextureForBlock(block);
    }

    @Override
    public @Nullable RenderType getRenderType(SulfurCubeRenderState state, boolean bl, boolean bl2, boolean bl3) {
        return RenderTypes.entityTranslucent(this.getTextureLocation(state), true);
    }

    public Identifier getTextureForBlock(Block block) {
        String texturePath = switch (block) {
            // --- REGULAR ---
            case Block b when b == Blocks.DIRT -> "textures/entity/sulfur_cube_dirt.png";
            case Block b when b == Blocks.STONE -> "textures/entity/sulfur_cube_stone.png";
            case Block b when b == Blocks.COBBLESTONE -> "textures/entity/sulfur_cube_cobblestone.png";
            case Block b when b == Blocks.MOSSY_COBBLESTONE -> "textures/entity/sulfur_cube_mossy_cobblestone.png";
            case Block b when b == Blocks.SMOOTH_STONE -> "textures/entity/sulfur_cube_smooth_stone.png";
            case Block b when b == Blocks.STONE_BRICKS -> "textures/entity/sulfur_cube_stone_bricks.png";
            case Block b when b == Blocks.CHISELED_STONE_BRICKS -> "textures/entity/sulfur_cube_chiseled_stone_bricks.png";
            case Block b when b == Blocks.CRACKED_STONE_BRICKS -> "textures/entity/sulfur_cube_cracked_stone_bricks.png";
            case Block b when b == Blocks.MOSSY_STONE_BRICKS -> "textures/entity/sulfur_cube_mossy_stone_bricks.png";

            // --- STICKY ---
            case Block b when b == Blocks.HONEY_BLOCK -> "textures/entity/sulfur_cube_honey_block.png";
            case Block b when b == Blocks.HONEYCOMB_BLOCK -> "textures/entity/sulfur_cube_honeycomb_block.png";

            // --- HEAVY (SLOW_FLAT) ---
            case Block b when b == Blocks.QUARTZ_BLOCK -> "textures/entity/sulfur_cube_quartz_block.png";
            case Block b when b == Blocks.QUARTZ_BRICKS -> "textures/entity/sulfur_cube_quartz_bricks.png";
            case Block b when b == Blocks.CHISELED_QUARTZ_BLOCK -> "textures/entity/sulfur_cube_chiseled_quartz_block.png";
            case Block b when b == Blocks.QUARTZ_PILLAR -> "textures/entity/sulfur_cube_quartz_pillar.png";
            case Block b when b == Blocks.SMOOTH_QUARTZ -> "textures/entity/sulfur_cube_smooth_quartz.png";
            case Block b when b == Blocks.COPPER_BLOCK -> "textures/entity/sulfur_cube_copper_block.png";
            case Block b when b == Blocks.IRON_BLOCK -> "textures/entity/sulfur_cube_iron_block.png";
            case Block b when b == Blocks.GOLD_BLOCK -> "textures/entity/sulfur_cube_gold_block.png";
            case Block b when b == Blocks.AMETHYST_BLOCK -> "textures/entity/sulfur_cube_amethyst_block.png";
            case Block b when b == Blocks.COAL_BLOCK -> "textures/entity/sulfur_cube_coal_block.png";
            case Block b when b == Blocks.DIAMOND_BLOCK -> "textures/entity/sulfur_cube_diamond_block.png";
            case Block b when b == Blocks.EMERALD_BLOCK -> "textures/entity/sulfur_cube_emerald_block.png";
            case Block b when b == Blocks.LAPIS_BLOCK -> "textures/entity/sulfur_cube_lapis_block.png";
            case Block b when b == Blocks.NETHERITE_BLOCK -> "textures/entity/sulfur_cube_netherite_block.png";
            case Block b when b == Blocks.REDSTONE_BLOCK -> "textures/entity/sulfur_cube_redstone_block.png";

            // --- LIGHT (WOODS) ---
            // Oak
            case Block b when b == Blocks.OAK_LOG -> "textures/entity/sulfur_cube_oak_log.png";
            case Block b when b == Blocks.STRIPPED_OAK_LOG -> "textures/entity/sulfur_cube_stripped_oak_log.png";
            case Block b when b == Blocks.OAK_WOOD -> "textures/entity/sulfur_cube_oak_wood.png";
            case Block b when b == Blocks.STRIPPED_OAK_WOOD -> "textures/entity/sulfur_cube_stripped_oak_wood.png";
            case Block b when b == Blocks.OAK_PLANKS -> "textures/entity/sulfur_cube_oak_planks.png";
            // Spruce
            case Block b when b == Blocks.SPRUCE_LOG -> "textures/entity/sulfur_cube_spruce_log.png";
            case Block b when b == Blocks.STRIPPED_SPRUCE_LOG -> "textures/entity/sulfur_cube_stripped_spruce_log.png";
            case Block b when b == Blocks.SPRUCE_WOOD -> "textures/entity/sulfur_cube_spruce_wood.png";
            case Block b when b == Blocks.STRIPPED_SPRUCE_WOOD -> "textures/entity/sulfur_cube_stripped_spruce_wood.png";
            case Block b when b == Blocks.SPRUCE_PLANKS -> "textures/entity/sulfur_cube_spruce_planks.png";
            // Birch
            case Block b when b == Blocks.BIRCH_LOG -> "textures/entity/sulfur_cube_birch_log.png";
            case Block b when b == Blocks.STRIPPED_BIRCH_LOG -> "textures/entity/sulfur_cube_stripped_birch_log.png";
            case Block b when b == Blocks.BIRCH_WOOD -> "textures/entity/sulfur_cube_birch_wood.png";
            case Block b when b == Blocks.STRIPPED_BIRCH_WOOD -> "textures/entity/sulfur_cube_stripped_birch_wood.png";
            case Block b when b == Blocks.BIRCH_PLANKS -> "textures/entity/sulfur_cube_birch_planks.png";
            // Jungle
            case Block b when b == Blocks.JUNGLE_LOG -> "textures/entity/sulfur_cube_jungle_log.png";
            case Block b when b == Blocks.STRIPPED_JUNGLE_LOG -> "textures/entity/sulfur_cube_stripped_jungle_log.png";
            case Block b when b == Blocks.JUNGLE_WOOD -> "textures/entity/sulfur_cube_jungle_wood.png";
            case Block b when b == Blocks.STRIPPED_JUNGLE_WOOD -> "textures/entity/sulfur_cube_stripped_jungle_wood.png";
            case Block b when b == Blocks.JUNGLE_PLANKS -> "textures/entity/sulfur_cube_jungle_planks.png";
            // Acacia
            case Block b when b == Blocks.ACACIA_LOG -> "textures/entity/sulfur_cube_acacia_log.png";
            case Block b when b == Blocks.STRIPPED_ACACIA_LOG -> "textures/entity/sulfur_cube_stripped_acacia_log.png";
            case Block b when b == Blocks.ACACIA_WOOD -> "textures/entity/sulfur_cube_acacia_wood.png";
            case Block b when b == Blocks.STRIPPED_ACACIA_WOOD -> "textures/entity/sulfur_cube_stripped_acacia_wood.png";
            case Block b when b == Blocks.ACACIA_PLANKS -> "textures/entity/sulfur_cube_acacia_planks.png";
            // Dark Oak
            case Block b when b == Blocks.DARK_OAK_LOG -> "textures/entity/sulfur_cube_dark_oak_log.png";
            case Block b when b == Blocks.STRIPPED_DARK_OAK_LOG -> "textures/entity/sulfur_cube_stripped_dark_oak_log.png";
            case Block b when b == Blocks.DARK_OAK_WOOD -> "textures/entity/sulfur_cube_dark_oak_wood.png";
            case Block b when b == Blocks.STRIPPED_DARK_OAK_WOOD -> "textures/entity/sulfUR_cube_stripped_dark_oak_wood.png";
            case Block b when b == Blocks.DARK_OAK_PLANKS -> "textures/entity/sulfur_cube_dark_oak_planks.png";
            // Mangrove
            case Block b when b == Blocks.MANGROVE_LOG -> "textures/entity/sulfur_cube_mangrove_log.png";
            case Block b when b == Blocks.STRIPPED_MANGROVE_LOG -> "textures/entity/sulfur_cube_stripped_mangrove_log.png";
            case Block b when b == Blocks.MANGROVE_WOOD -> "textures/entity/sulfur_cube_mangrove_wood.png";
            case Block b when b == Blocks.STRIPPED_MANGROVE_WOOD -> "textures/entity/sulfur_cube_stripped_mangrove_wood.png";
            case Block b when b == Blocks.MANGROVE_PLANKS -> "textures/entity/sulfur_cube_mangrove_planks.png";
            // Cherry
            case Block b when b == Blocks.CHERRY_LOG -> "textures/entity/sulfur_cube_cherry_log.png";
            case Block b when b == Blocks.STRIPPED_CHERRY_LOG -> "textures/entity/sulfur_cube_stripped_cherry_log.png";
            case Block b when b == Blocks.CHERRY_WOOD -> "textures/entity/sulfur_cube_cherry_wood.png";
            case Block b when b == Blocks.STRIPPED_CHERRY_WOOD -> "textures/entity/sulfur_cube_stripped_cherry_wood.png";
            case Block b when b == Blocks.CHERRY_PLANKS -> "textures/entity/sulfur_cube_cherry_planks.png";
            // Pale Oak
            case Block b when b == Blocks.PALE_OAK_LOG -> "textures/entity/sulfur_cube_pale_oak_log.png";
            case Block b when b == Blocks.STRIPPED_PALE_OAK_LOG -> "textures/entity/sulfur_cube_stripped_pale_oak_log.png";
            case Block b when b == Blocks.PALE_OAK_WOOD -> "textures/entity/sulfur_cube_pale_oak_wood.png";
            case Block b when b == Blocks.STRIPPED_PALE_OAK_WOOD -> "textures/entity/sulfur_cube_stripped_pale_oak_wood.png";
            case Block b when b == Blocks.PALE_OAK_PLANKS -> "textures/entity/sulfur_cube_pale_oak_planks.png";
            // Bamboo
            case Block b when b == Blocks.BAMBOO_BLOCK -> "textures/entity/sulfur_cube_bamboo_block.png";
            case Block b when b == Blocks.STRIPPED_BAMBOO_BLOCK -> "textures/entity/sulfur_cube_stripped_bamboo_block.png";
            case Block b when b == Blocks.BAMBOO_MOSAIC -> "textures/entity/sulfur_cube_bamboo_mosaic.png";
            // Crimson
            case Block b when b == Blocks.CRIMSON_STEM -> "textures/entity/sulfur_cube_crimson_stem.png";
            case Block b when b == Blocks.STRIPPED_CRIMSON_STEM -> "textures/entity/sulfur_cube_stripped_crimson_stem.png";
            case Block b when b == Blocks.CRIMSON_HYPHAE -> "textures/entity/sulfur_cube_crimson_hyphae.png";
            case Block b when b == Blocks.STRIPPED_CRIMSON_HYPHAE -> "textures/entity/sulfur_cube_stripped_crimson_hyphae.png";
            case Block b when b == Blocks.CRIMSON_PLANKS -> "textures/entity/sulfur_cube_crimson_planks.png";
            // Warped
            case Block b when b == Blocks.WARPED_STEM -> "textures/entity/sulfur_cube_warped_stem.png";
            case Block b when b == Blocks.STRIPPED_WARPED_STEM -> "textures/entity/sulfur_cube_stripped_warped_stem.png";
            case Block b when b == Blocks.WARPED_HYPHAE -> "textures/entity/sulfur_cube_warped_hyphae.png";
            case Block b when b == Blocks.STRIPPED_WARPED_HYPHAE -> "textures/entity/sulfur_cube_stripped_warped_hyphae.png";
            case Block b when b == Blocks.WARPED_PLANKS -> "textures/entity/sulfur_cube_warped_planks.png";

            // Wool
            case Block b when b == Blocks.WHITE_WOOL -> "textures/entity/sulfur_cube_white_wool.png";
            case Block b when b == Blocks.LIGHT_GRAY_WOOL -> "textures/entity/sulfur_cube_light_gray_wool.png";
            case Block b when b == Blocks.GRAY_WOOL -> "textures/entity/sulfur_cube_gray_wool.png";
            case Block b when b == Blocks.BLACK_WOOL -> "textures/entity/sulfur_cube_black_wool.png";
            case Block b when b == Blocks.BROWN_WOOL -> "textures/entity/sulfur_cube_brown_wool.png";
            case Block b when b == Blocks.RED_WOOL -> "textures/entity/sulfur_cube_red_wool.png";
            case Block b when b == Blocks.ORANGE_WOOL -> "textures/entity/sulfur_cube_orange_wool.png";
            case Block b when b == Blocks.YELLOW_WOOL -> "textures/entity/sulfur_cube_yellow_wool.png";
            case Block b when b == Blocks.LIME_WOOL -> "textures/entity/sulfur_cube_lime_wool.png";
            case Block b when b == Blocks.GREEN_WOOL -> "textures/entity/sulfur_cube_green_wool.png";
            case Block b when b == Blocks.CYAN_WOOL -> "textures/entity/sulfur_cube_cyan_wool.png";
            case Block b when b == Blocks.LIGHT_BLUE_WOOL -> "textures/entity/sulfur_cube_light_blue_wool.png";
            case Block b when b == Blocks.BLUE_WOOL -> "textures/entity/sulfur_cube_blue_wool.png";
            case Block b when b == Blocks.PURPLE_WOOL -> "textures/entity/sulfur_cube_purple_wool.png";
            case Block b when b == Blocks.MAGENTA_WOOL -> "textures/entity/sulfur_cube_magenta_wool.png";
            case Block b when b == Blocks.PINK_WOOL -> "textures/entity/sulfur_cube_pink_wool.png";

            default -> "textures/entity/sulfur_cube_default.png";
        };

        return Identifier.fromNamespaceAndPath(ChaosCubed.MODID, texturePath);
    }
}
