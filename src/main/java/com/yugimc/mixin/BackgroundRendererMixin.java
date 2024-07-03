package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin
{
    // ANTI BLINDD
    @Inject(at = @At("HEAD"),
            method = "getFogModifier(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/BackgroundRenderer$StatusEffectFogModifier;",
            cancellable = true)
    private static <StatusEffectFogModifier> void onGetFogModifier(Entity entity, float tickDelta, CallbackInfoReturnable<StatusEffectFogModifier> ci)
    {
        if(YugiClient.abtoggleOn)
        {
            ci.setReturnValue(null);
        }
    }
}