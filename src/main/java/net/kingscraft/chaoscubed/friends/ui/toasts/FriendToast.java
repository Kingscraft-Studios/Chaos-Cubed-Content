package net.kingscraft.chaoscubed.friends.ui.toasts;

import net.kingscraft.chaoscubed.client.ChaosCubedClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FriendToast implements Toast {
    private static final Identifier BACKGROUND_SPRITE = Identifier.fromNamespaceAndPath(ChaosCubedClient.MODID, "toast/friend");
    private final Component message;
    private final long duration;
    @Nullable private final PlayerSkin skin;

    private Visibility visibility = Visibility.SHOW;
    private int cachedWidth = -1;

    public FriendToast(Component message, long durationMillis) {
        this.message = message;
        this.duration = durationMillis;
        this.skin = null;
    }

    public FriendToast(PlayerSkin skin, Component message, long durationMillis) {
        this.skin = skin;
        this.message = message;
        this.duration = durationMillis;
    }

    @Override
    public int width() {
        if (this.cachedWidth == -1) {
            int textWidth = Minecraft.getInstance().font.width(this.message);
            this.cachedWidth = Math.max(160, Math.min(240, textWidth + 38));
        }
        return this.cachedWidth;
    }

    @Override
    public @NonNull Visibility getWantedVisibility() {
        return this.visibility;
    }

    @Override
    public void update(@NonNull ToastManager toastManager, long timeSinceLastRender) {
        this.visibility = timeSinceLastRender >= this.duration ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public void render(GuiGraphics guiGraphics, @NonNull Font font, long timeSinceLastRender) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());

        int textX = 30;
        if (this.skin != null) {
            PlayerFaceRenderer.draw(guiGraphics, this.skin, 8, 8, 16);
        } else {
            textX = 18;
        }

        guiGraphics.drawString(font, this.message, textX, 12, 0xFFFFFFFF, false);
    }
}