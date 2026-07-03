package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientBrandRetriever.class)
public abstract class ClientBrandRetrieverMixin { // Feature: Vanilla Spoof

    @Inject(at = @At("HEAD"), method = "getClientModName", cancellable = true)
    private static void getClientModName(CallbackInfoReturnable<String> callback) {
        if (YugiClient.vanillaSpoof) {
            callback.setReturnValue("vanilla");
        }
    }
}
