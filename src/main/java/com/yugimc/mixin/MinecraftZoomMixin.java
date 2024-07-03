package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class MinecraftZoomMixin {

    // MINECRAFT ZOOM FOV

    @Inject(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", at = @At("RETURN"), cancellable = true)
    public void getZoomLevel(CallbackInfoReturnable<Double> callbackInfo) {
        if(YugiClient.zoomOn) {
            double fov = callbackInfo.getReturnValue();
            callbackInfo.setReturnValue(fov * YugiClient.zoomLevel);
        }

        ///YugiMc.manageSmoothCamera();
    }
}