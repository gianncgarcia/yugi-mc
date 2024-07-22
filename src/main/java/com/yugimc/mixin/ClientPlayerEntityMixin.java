package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin
{
    // PORTAL GUI

    @Shadow
    @Final
    protected MinecraftClient client;
    private Screen tempCurrentScreen;

    @Inject(at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
            opcode = Opcodes.GETFIELD,
            ordinal = 0), method = "tickNausea(Z)V")
    private void beforeTickNausea(boolean fromPortalEffect, CallbackInfo ci)
    {
        if(!YugiClient.pgg)
            return;

        tempCurrentScreen = client.currentScreen;
        client.currentScreen = null;
    }

    @Inject(at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;nauseaIntensity:F",
            opcode = Opcodes.GETFIELD,
            ordinal = 1), method = "tickNausea(Z)V")
    private void afterUpdateNausea(CallbackInfo ci)
    {
        if(tempCurrentScreen == null)
            return;

        client.currentScreen = tempCurrentScreen;
        tempCurrentScreen = null;
    }

}