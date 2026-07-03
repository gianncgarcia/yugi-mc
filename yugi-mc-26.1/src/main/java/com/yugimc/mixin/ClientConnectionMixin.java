package com.yugimc.mixin;

import com.yugimc.YugiClient;
import com.yugimc.util.PacketUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ClientConnectionMixin { // Feature: FakeLag

    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, CallbackInfo ci) {
        if (!YugiClient.fakeLagEnabled || YugiClient.isFlushing) return;
        if (packet instanceof ServerboundKeepAlivePacket) return;
        PacketUtil.connectionForFlush = (Connection)(Object)this;
        YugiClient.packetQueue.add(packet);
        ci.cancel();
    }
}
