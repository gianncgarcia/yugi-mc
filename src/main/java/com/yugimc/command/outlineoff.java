package com.yugimc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.yugimc.YugiClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public class outlineoff {

    public static MinecraftClient client;


    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(ClientCommandManager.literal("outlineoff").executes(ctx -> {
            YugiClient.outlineONN = false;
            YugiClient.client.player.sendMessage(Text.of("§9[Yugi+] §rNo Entity Outline"), false);
            return 1;
        }));
    }
}