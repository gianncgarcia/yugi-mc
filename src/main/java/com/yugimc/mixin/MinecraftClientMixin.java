package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {


    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void outlineEntities(Entity entity, CallbackInfoReturnable<Boolean> ci) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (YugiClient.outlineON) {
            if (entity.getType() == EntityType.PLAYER)
                ci.setReturnValue(true);

            if (entity.getType() == EntityType.WITCH)
                ci.setReturnValue(true);

            if (entity.getType() == EntityType.ITEM)
                ci.setReturnValue(true);

            if (entity.getType() == EntityType.ZOMBIE_VILLAGER)
                ci.setReturnValue(true);

            if (entity.getType() == EntityType.PILLAGER) {
                ci.setReturnValue(true);
            }

            if (entity.getType() == EntityType.RAVAGER) {
                ci.setReturnValue(true);
            }

            if (entity.getType() == EntityType.VINDICATOR) {
                ci.setReturnValue(true);
            }

            if (entity.getType() == EntityType.VEX) {
                ci.setReturnValue(true);
            }
        }

        if (YugiClient.outlineONN) {
            ci.setReturnValue(true);
        }

        if (!YugiClient.outlineONN){

        }
    }
}