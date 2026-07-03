package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin { // Feature: No Mining Cooldown

    @ModifyConstant(method = "continueDestroyBlock", constant = @Constant(intValue = 5))
    private int onContinueDestroyBlock(int value) {
        return YugiClient.noMiningCooldown ? 0 : value;
    }

    @ModifyConstant(method = "startDestroyBlock", constant = @Constant(intValue = 5))
    private int onStartDestroyBlock(int value) {
        return YugiClient.noMiningCooldown ? 0 : value;
    }
}
