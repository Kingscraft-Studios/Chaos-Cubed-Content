package net.kingscraft.chaoscubed.mixin;

import net.kingscraft.chaoscubed.friends.api.FriendsApi;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(OnlineOptionsScreen.class)
public abstract class OnlineScreenMixin extends OptionsSubScreen {

    public OnlineScreenMixin(Screen screen, Options options, Component title) {
        super(screen, options, title);
    }

    @Inject(method = "addOptions", at = @At("TAIL"))
    private void onAddOptions(CallbackInfo ci) {
        if (this.list == null) return;
        String uuid = this.minecraft.getUser().getProfileId().toString();

        LinearLayout group = LinearLayout.vertical().spacing(8);
        group.defaultCellSetting().alignHorizontallyCenter();

        StringWidget label = new StringWidget(Component.literal("Friends List Settings"), this.font);
        StringWidget allowLabel = new StringWidget(Component.literal("Allow Requests: §8..."), this.font);

        boolean[] current = {true};

        Button toggleBtn = Button.builder(
                Component.literal("§8..."),
                btn -> {
                    boolean next = !current[0];
                    current[0] = next;
                    CompletableFuture.supplyAsync(() -> FriendsApi.setAllowRequests(uuid, next))
                            .thenAccept(res -> {
                                if (res == null || !res.ok()) current[0] = !next;
                                boolean state = current[0];
                                this.minecraft.execute(() -> updateLabel(allowLabel, btn, state));
                            });
                }
        ).width(200).build();

        group.addChild(label, LayoutSettings::alignHorizontallyCenter);
        group.addChild(allowLabel, LayoutSettings::alignHorizontallyCenter);
        group.addChild(toggleBtn, LayoutSettings::alignHorizontallyCenter);
        this.layout.addToContents(group);
        this.addRenderableWidget(toggleBtn);

        CompletableFuture.supplyAsync(() -> FriendsApi.getAllowRequests(uuid))
                .thenAccept(res -> {
                    if (res != null) {
                        current[0] = res.allow();
                        this.minecraft.execute(() -> updateLabel(allowLabel, toggleBtn, current[0]));
                    }
                });

        this.updateLabel(allowLabel, toggleBtn, current[0]);
    }

    @Unique
    private void updateLabel(StringWidget label, Button btn, boolean allow) {
        label.setMessage(Component.literal("Allow Requests: " + (allow ? "§aOn" : "§cOff")));
        btn.setMessage(Component.literal(allow ? "Disable" : "Enable"));
    }
}