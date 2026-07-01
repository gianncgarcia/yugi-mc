package com.yugimc.util;

import com.yugimc.YugiClient;
import net.minecraft.network.Connection;

public class PacketUtil {
    public static Connection connectionForFlush;

    public static void flushPackets() {
        if (connectionForFlush == null || YugiClient.packetQueue.isEmpty()) return;
        YugiClient.isFlushing = true;
        for (var packet : YugiClient.packetQueue) {
            connectionForFlush.send(packet);
        }
        YugiClient.packetQueue.clear();
        YugiClient.isFlushing = false;
    }
}
