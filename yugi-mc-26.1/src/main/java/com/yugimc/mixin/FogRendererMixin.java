package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Inject(method = "setupFog", at = @At("RETURN"), cancellable = true)
    private void onSetupFog(Camera camera, int i, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<FogData> ci) {
        if (YugiClient.antiBlind) {
            FogData fogData = ci.getReturnValue();
            fogData.renderDistanceStart = fogData.renderDistanceEnd * 10;
            ci.setReturnValue(fogData);
        }
    }
}
