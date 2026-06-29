package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraZoomMixin {

    @Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
    private void onCalculateFov(float partialTicks, CallbackInfoReturnable<Float> ci) {
        if (YugiClient.zoomEnabled) {
            ci.setReturnValue(ci.getReturnValue() * (float) YugiClient.zoomMultiplier);
        }
    }
}
