package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin { // Feature: Nametag Sneak

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void onExtractRenderState(Entity entity, EntityRenderState renderState, float tickDelta, CallbackInfo ci) {
        if (YugiClient.nametagSneak) {
            renderState.isDiscrete = false;
            renderState.lightCoords = 15728880;
        }
    }

    @ModifyArg(method = "extractNameTags(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V",
               at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;extractNameTags(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FDD)V"),
               index = 3)
    private double modifyNameTagDistance(double distance) {
        return YugiClient.nametagSneak ? Double.MAX_VALUE : distance;
    }
}
