package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LightmapTextureManager.class)
public class LightTextureManagerMixin
{
    //FULL BRIGHT
    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"), index = 2)
    private int onSetColor(int color) {
        if (YugiClient.abtoggleOn) {
            return 0xFFFFFFFF;
        }
        return color;
    }
}