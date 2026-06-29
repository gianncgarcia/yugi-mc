package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "extract", at = @At("RETURN"))
    private void onExtract(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        if (YugiClient.noHurtCam) {
            ((GameRenderer)(Object)this).gameRenderState().optionsRenderState.damageTiltStrength = 0.0;
        }

        if (YugiClient.portalGuiEnabled) {
            ((GameRenderer)(Object)this).gameRenderState().optionsRenderState.screenEffectScale = 0.0f;
        }
    }
}
