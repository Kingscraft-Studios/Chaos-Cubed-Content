package net.kingscraft.chaoscubed.mixin;

import net.kingscraft.chaoscubed.friends.ui.FriendsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.RenderPipelines;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Unique
    private static final Identifier FRIENDS_ICON = Identifier.fromNamespaceAndPath("chaos_cubed", "textures/gui/friends/friends.png");

    protected TitleScreenMixin(Component title) { super(title); }

    @Inject(at = @At("TAIL"), method = "init")
    private void addFriendsButton(CallbackInfo ci) {
        int realmsY = this.height / 4 + 48 + 24 + 24;
        // Placing it to the right of the Accessibility button usually found at width/2 + 104
        this.addRenderableWidget(new FriendsIconButton(this.width / 2 + 104, realmsY));
    }

    @Unique
    private static class FriendsIconButton extends Button {
        public FriendsIconButton(int x, int y) {
            super(x, y, 20, 20, Component.empty(),
                    button -> Minecraft.getInstance().setScreen(new FriendsScreen()),
                    Button.DEFAULT_NARRATION);

            this.setTooltip(Tooltip.create(Component.literal("Friends List")));
        }

        @Override
        protected void renderContents(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            // 1. Draw the background (this method uses this.getX() internally, so it stays put)
            this.renderDefaultSprite(guiGraphics);

            // 2. Prepare the alpha/color
            int alpha = (int)(this.alpha * 255.0F) << 24;
            int colorTint = 0xFFFFFF | alpha;

            // 3. MOVE the drawing "brush" to the button's position
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(this.getX(), this.getY());

            // 4. Draw the icon at (2, 2) RELATIVE to the button's new (0, 0)
            guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    FRIENDS_ICON,
                    2, 2,
                    0.0F, 0.0F,
                    16, 16,
                    16, 16,
                    colorTint
            );

            // 5. Always pop the matrix so you don't mess up the rest of the screen's rendering!
            guiGraphics.pose().popMatrix();
        }
    }
}