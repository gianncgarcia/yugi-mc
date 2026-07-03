package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin { // Feature: Anti Blind

    @Inject(method = "getEffectBlendFactor", at = @At("HEAD"), cancellable = true)
    private void onGetEffectBlendFactor(Holder<MobEffect> registryEntry, float delta, CallbackInfoReturnable<Float> cir) {
        if (registryEntry.is(MobEffects.DARKNESS) && YugiClient.antiBlind) {
            cir.setReturnValue(0.0f);
        }
    }
}