package net.kingscraft.chaoscubed.entity.client.sulfurcube;

import net.kingscraft.chaoscubed.ChaosCubed;
import net.kingscraft.chaoscubed.client.ChaosCubedClient;
import net.kingscraft.chaoscubed.entity.SulfurCubeEntity;
import net.kingscraft.chaoscubed.entity.properties.CubeBlockProperties;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;


public class SulfurCubeRenderer extends LivingEntityRenderer<SulfurCubeEntity, SulfurCubeRenderState, SulfurCubeModel> {

    private static final Set<Block> REGISTERED_TEXTURES = new HashSet<>();
    private static boolean initialized = false;

    public SulfurCubeRenderer(EntityRendererProvider.Context context) {
        super(context, new SulfurCubeModel(context.bakeLayer(SulfurCubeModel.LAYER)), 0.5f);
    }

    private static void ensureInitialized() {

        if (initialized) {
            return;
        }

        initialized = true;

        ChaosCubedClient.LOGGER.info("Initializing SulfurCube texture registry...");

        // Concrete Powder
        registerTextureTag(BlockTags.DIRT, Blocks.MYCELIUM, Blocks.MOSS_BLOCK, Blocks.PALE_MOSS_BLOCK);  // Add Grass Block
        registerTexture(Blocks.PACKED_MUD);
        registerTexture(Blocks.COAL_BLOCK);
        registerTexture(Blocks.CLAY);
        registerTexture(Blocks.BONE_BLOCK);

        registerTexture(Blocks.HONEY_BLOCK);
        registerTexture(Blocks.HONEYCOMB_BLOCK);

        registerTexture(Blocks.IRON_BLOCK);
        registerTexture(Blocks.GOLD_BLOCK);
        registerTexture(Blocks.RAW_COPPER_BLOCK);
        registerTexture(Blocks.RAW_GOLD_BLOCK);
        registerTexture(Blocks.RAW_IRON_BLOCK);
        registerTexture(Blocks.GOLD_BLOCK);
        registerTexture(Blocks.NETHER_GOLD_ORE);
        registerTexture(Blocks.IRON_ORE);
        registerTexture(Blocks.COPPER_ORE);
        registerTexture(Blocks.NETHERITE_BLOCK);
        registerTexture(Blocks.ANCIENT_DEBRIS);
        registerTextureTag(BlockTags.COPPER);
        // Copper Bulb, Cut Copper, Chiseled Copper

        registerTextureTag(BlockTags.WOOL);

        registerTexture(Blocks.TNT);

        ChaosCubedClient.LOGGER.info("Finished SulfurCube texture registry!");
        ChaosCubedClient.LOGGER.info("Registered Texture Count: " + REGISTERED_TEXTURES.size());
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
        ensureInitialized();

        // fallback if not registered
        if (!REGISTERED_TEXTURES.contains(block)) {
            ChaosCubedClient.LOGGER.warn("Texture Doesnt Exist for Block: {} Using Default Texture!", block);
            return Identifier.fromNamespaceAndPath(
                    ChaosCubed.MODID,
                    "textures/entity/sulfur_cube.png"
            );
        }

        // auto-generate from registry name
        Identifier id = BuiltInRegistries.BLOCK.getKey(block);

        String path = "textures/entity/sulfur_cube_" + id.getPath() + ".png";
        return Identifier.fromNamespaceAndPath(ChaosCubed.MODID, path);
    }

    public static void registerTexture(Block block) {
        REGISTERED_TEXTURES.add(block);
        ChaosCubedClient.LOGGER.info("Added Block: {}", block);
    }

    public static void registerTextureTag(
            TagKey<Block> tag,
            Block... excludedBlocks) {

        Set<Block> excluded = Set.of(excludedBlocks);

        for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) {

            Block block = holder.value();
            // Skip excluded blocks
            if (excluded.contains(block)) {
                continue;
            }

            REGISTERED_TEXTURES.add(block);

            ChaosCubedClient.LOGGER.info("SUCCESSFULLY REGISTERED: {}", block);
        }
    }
}
