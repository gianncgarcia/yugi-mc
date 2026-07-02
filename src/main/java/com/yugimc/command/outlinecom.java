package com.yugimc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.yugimc.YugiClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public class outlinecom {



    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(ClientCommandManager.literal("outlineon").executes(ctx -> {
            YugiClient.outlineONN = true;
            YugiClient.client.player.sendMessage(Text.of("§a[Yugi+] §rAll Entity Outline"), false);
            return 1;
        }));
    }

}