package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin { // Feature: No Hurt Cam / Portal GUI

    // Feature: No Hurt Cam
    @Inject(method = "extract", at = @At("RETURN"))
    private void onExtract(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        // Feature: No Hurt Cam
        if (YugiClient.noHurtCam) {
            ((GameRenderer)(Object)this).gameRenderState().optionsRenderState.damageTiltStrength = 0.0;
        }

        // Feature: Portal GUI
        if (YugiClient.portalGuiEnabled) {
            ((GameRenderer)(Object)this).gameRenderState().optionsRenderState.screenEffectScale = 0.0f;
        }
    }
}
