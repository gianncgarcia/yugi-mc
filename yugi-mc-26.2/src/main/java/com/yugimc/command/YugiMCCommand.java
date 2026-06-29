package com.yugimc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.yugimc.YugiClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class YugiMCCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(ClientCommands.literal("yugimc")
            .then(ClientCommands.literal("outline")
                .then(ClientCommands.literal("on").executes(ctx -> {
                    YugiClient.entityOutlineAll = true;
                    YugiClient.outlinedEntities.clear();
                    ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rAll Entity Outline Enabled"));
                    return 1;
                }))
                .then(ClientCommands.literal("off").executes(ctx -> {
                    YugiClient.entityOutlineAll = false;
                    YugiClient.outlinedEntities.clear();
                    ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rAll Entity Outline Disabled"));
                    return 1;
                }))
                .then(ClientCommands.literal("player")
                    .then(ClientCommands.argument("playername", StringArgumentType.greedyString())
                        .suggests(YugiMCCommand::suggestPlayerNames)
                        .executes(ctx -> {
                            String playerName = StringArgumentType.getString(ctx, "playername");
                            if (YugiClient.outlinedEntities.contains(playerName)) {
                                YugiClient.outlinedEntities.remove(playerName);
                                ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rOutline removed from " + playerName));
                            } else {
                                YugiClient.outlinedEntities.add(playerName);
                                YugiClient.entityOutlineAll = false;
                                ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rOutlined " + playerName));
                            }
                            return 1;
                        })
                    )
                )
                .then(ClientCommands.literal("entity")
                    .then(ClientCommands.argument("entityname", StringArgumentType.greedyString())
                        .suggests(YugiMCCommand::suggestEntityNames)
                        .executes(ctx -> {
                            String entityName = StringArgumentType.getString(ctx, "entityname");
                            if (YugiClient.outlinedEntities.contains(entityName)) {
                                YugiClient.outlinedEntities.remove(entityName);
                                ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rOutline removed from " + entityName));
                            } else {
                                YugiClient.outlinedEntities.add(entityName);
                                YugiClient.entityOutlineAll = false;
                                ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rOutlined " + entityName));
                            }
                            return 1;
                        })
                    )
                )
                .then(ClientCommands.literal("list")
                    .executes(ctx -> {
                        ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rUse /yugimc outline on, /yugimc outline off, /yugimc outline player <name>, or /yugimc outline entity <name>"));
                        return 1;
                    })
                )
            )
            .then(ClientCommands.literal("ip").executes(ctx -> {
                var serverData = Minecraft.getInstance().getCurrentServer();
                if (serverData == null || serverData.ip == null) {
                    ctx.getSource().sendFeedback(Component.literal("§cNot connected to a server."));
                    return 0;
                }
                String[] parts = serverData.ip.split(":");
                String host = parts[0];
                String port = parts.length > 1 ? parts[1] : "25565";
                ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rHost: ")
                        .append(Component.literal(host).withStyle(Style.EMPTY
                                .withUnderlined(true)
                                .withClickEvent(new ClickEvent.CopyToClipboard(host)))));
                ctx.getSource().sendFeedback(Component.literal("§9[Yugi+] §rPort: ")
                        .append(Component.literal(port).withStyle(Style.EMPTY
                                .withUnderlined(true)
                                .withClickEvent(new ClickEvent.CopyToClipboard(port)))));
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return InetAddress.getByName(host).getHostAddress();
                    } catch (Exception e) {
                        return null;
                    }
                }).thenAcceptAsync(resolved -> {
                    if (resolved != null) {
                        var src = ctx.getSource();
                        src.sendFeedback(Component.literal("§9[Yugi+] §rIP: ")
                                .append(Component.literal(resolved).withStyle(Style.EMPTY
                                        .withUnderlined(true)
                                        .withClickEvent(new ClickEvent.CopyToClipboard(resolved)))));
                    }
                }, Minecraft.getInstance());
                return 1;
            }))
        );
    }

    private static CompletableFuture<Suggestions> suggestPlayerNames(CommandContext<FabricClientCommandSource> ctx, SuggestionsBuilder builder) {
        var level = YugiClient.client.level;
        if (level != null) {
            List<String> names = new ArrayList<>();
            for (var entity : level.entitiesForRendering()) {
                if (entity instanceof Player) {
                    names.add(entity.getName().getString());
                }
            }
            return SharedSuggestionProvider.suggest(names, builder);
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestEntityNames(CommandContext<FabricClientCommandSource> ctx, SuggestionsBuilder builder) {
        var level = YugiClient.client.level;
        if (level != null) {
            List<String> names = new ArrayList<>();
            for (var entity : level.entitiesForRendering()) {
                names.add(entity.getName().getString());
            }
            return SharedSuggestionProvider.suggest(names, builder);
        }
        return builder.buildFuture();
    }
}