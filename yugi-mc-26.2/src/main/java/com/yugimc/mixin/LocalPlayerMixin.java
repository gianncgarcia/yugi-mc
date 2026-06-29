package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "handlePortalTransitionEffect", at = @At("HEAD"), cancellable = true)
    private void onHandlePortalTransitionEffect(boolean bl, CallbackInfo ci) {
        if (YugiClient.portalGuiEnabled) {
            ci.cancel();
        }
    }
}
