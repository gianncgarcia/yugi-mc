package com.yugimc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.resources.Identifier;

public class YugiMCCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(ClientCommands.literal("yugimc")
            .then(ClientCommands.literal("outline")
                .then(ClientCommands.literal("on").executes(ctx -> {
                    YugiClient.entityOutlineAll = true;
                    YugiClient.outlinedEntities.clear();
                    YugiClient.outlinedPlayers.clear();
                    ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rAll Entity Outline Enabled"));
                    return 1;
                }))
                .then(ClientCommands.literal("off").executes(ctx -> {
                    YugiClient.entityOutlineAll = false;
                    YugiClient.outlinedEntities.clear();
                    YugiClient.outlinedPlayers.clear();
                    YugiClient.outlinedBlockTypes.clear();
                    YugiClient.outlinedBlocks.clear();
                    YugiClient.cachedBlockPositions.clear();
                    ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rAll Entity Outline Disabled"));
                    return 1;
                }))
                .then(ClientCommands.literal("player")
                    .then(ClientCommands.literal("clear").executes(ctx -> {
                        YugiClient.outlinedPlayers.clear();
                        YugiClient.entityOutlineAll = false;
                        ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rAll player outlines cleared"));
                        return 1;
                    }))
                    .then(ClientCommands.argument("playername", StringArgumentType.word())
                        .suggests(YugiMCCommand::suggestPlayerNames)
                        .executes(ctx -> {
                            String playerName = StringArgumentType.getString(ctx, "playername");
                            if (YugiClient.outlinedPlayers.containsKey(playerName)) {
                                YugiClient.outlinedPlayers.remove(playerName);
                                ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rOutline removed from " + playerName));
                            } else {
                                YugiClient.outlinedPlayers.put(playerName, -1);
                                YugiClient.entityOutlineAll = false;
                                ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rOutlined " + playerName));
                            }
                            return 1;
                        })
                        .then(ClientCommands.argument("color", StringArgumentType.word())
                            .suggests(YugiMCCommand::suggestColors)
                            .executes(ctx -> {
                                String playerName = StringArgumentType.getString(ctx, "playername");
                                String colorName = StringArgumentType.getString(ctx, "color");
                                Integer color = parseColor(colorName);
                                if (color == null) {
                                    ctx.getSource().sendFeedback(Component.literal("§cUnknown color: " + colorName));
                                    return 0;
                                }
                                if (YugiClient.outlinedPlayers.containsKey(playerName)) {
                                    YugiClient.outlinedPlayers.remove(playerName);
                                }
                                YugiClient.outlinedPlayers.put(playerName, color);
                                YugiClient.entityOutlineAll = false;
                                ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rOutlined " + playerName + " (" + colorName + ")"));
                                return 1;
                            })
                        )
                    )
                )
                .then(ClientCommands.literal("entity")
                    .then(ClientCommands.literal("clear").executes(ctx -> {
                        YugiClient.outlinedEntities.clear();
                        YugiClient.entityOutlineAll = false;
                        ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rAll entity outlines cleared"));
                        return 1;
                    }))
                    .then(ClientCommands.argument("entityname", StringArgumentType.word())
                        .suggests(YugiMCCommand::suggestEntityNames)
                        .executes(ctx -> {
                            String entityName = StringArgumentType.getString(ctx, "entityname");
                            if (YugiClient.outlinedEntities.containsKey(entityName)) {
                                YugiClient.outlinedEntities.remove(entityName);
                                ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rOutline removed from " + entityName));
                            } else {
                                YugiClient.outlinedEntities.put(entityName, -1);
                                YugiClient.entityOutlineAll = false;
                                ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rOutlined " + entityName));
                            }
                            return 1;
                        })
                        .then(ClientCommands.argument("color", StringArgumentType.word())
                            .suggests(YugiMCCommand::suggestColors)
                            .executes(ctx -> {
                                String entityName = StringArgumentType.getString(ctx, "entityname");
                                String colorName = StringArgumentType.getString(ctx, "color");
                                Integer color = parseColor(colorName);
                                if (color == null) {
                                    ctx.getSource().sendFeedback(Component.literal("§cUnknown color: " + colorName));
                                    return 0;
                                }
                                if (YugiClient.outlinedEntities.containsKey(entityName)) {
                                    YugiClient.outlinedEntities.remove(entityName);
                                }
                                YugiClient.outlinedEntities.put(entityName, color);
                                YugiClient.entityOutlineAll = false;
                                ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rOutlined " + entityName + " (" + colorName + ")"));
                                return 1;
                            })
                        )
                    )
                )
                .then(ClientCommands.literal("block")
                    .then(ClientCommands.literal("clear").executes(ctx -> {
                        YugiClient.entityOutlineAll = false;
                        YugiClient.outlinedEntities.clear();
                        YugiClient.outlinedPlayers.clear();
                        YugiClient.outlinedBlockTypes.clear();
                        YugiClient.outlinedBlocks.clear();
                        YugiClient.cachedBlockPositions.clear();
                        ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rAll outlines cleared"));
                        return 1;
                    }))
                    .then(ClientCommands.argument("blockname", StringArgumentType.word())
                        .suggests(YugiMCCommand::suggestBlockNames)
                        .executes(ctx -> {
                            String blockName = StringArgumentType.getString(ctx, "blockname");
                            var id = Identifier.withDefaultNamespace(blockName);
                            if (YugiClient.outlinedBlockTypes.contains(id)) {
                                YugiClient.outlinedBlockTypes.remove(id);
                                YugiClient.outlinedBlocks.clear();
                                for (var oid : YugiClient.outlinedBlockTypes) {
                                    var b = BuiltInRegistries.BLOCK.getValue(oid);
                                    if (b != null) YugiClient.outlinedBlocks.add(b);
                                }
                                YugiClient.cachedBlockPositions.clear();
                                ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rBlock outline removed for " + blockName));
                            } else {
                                var block = BuiltInRegistries.BLOCK.getValue(id);
                                if (block == null) {
                                    ctx.getSource().sendFeedback(Component.literal("§cUnknown block: " + blockName));
                                    return 0;
                                }
                                YugiClient.outlinedBlockTypes.add(id);
                                YugiClient.outlinedBlocks.add(block);
                                YugiClient.cachedBlockPositions.clear();
                                ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rOutlining " + blockName));
                            }
                            return 1;
                        })
                    )
                )
                .then(ClientCommands.literal("list")
                    .executes(ctx -> {
                        ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rUse /yugimc outline on, /yugimc outline off, /yugimc outline player <name>, /yugimc outline entity <name>, or /yugimc outline block <name>"));
                        return 1;
                    })
                )
            )
            .then(ClientCommands.literal("fakelag")
                .then(ClientCommands.argument("delay", IntegerArgumentType.integer(0, 5000))
                    .executes(ctx -> {
                        int delay = IntegerArgumentType.getInteger(ctx, "delay");
                        YugiClient.fakelagDelay = delay;
                        ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rFakeLag delay set to " + delay + "ms"));
                        return 1;
                    })
                )
                .then(ClientCommands.literal("randomize").executes(ctx -> {
                    YugiClient.fakelagRandomize = !YugiClient.fakelagRandomize;
                    ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rFakeLag randomization " +
                        (YugiClient.fakelagRandomize ? "§aOn" : "§cOff")));
                    return 1;
                }))
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rCurrent FakeLag delay: " + YugiClient.fakelagDelay + "ms (Randomization: " +
                        (YugiClient.fakelagRandomize ? "§aOn" : "§cOff") + "§r)"));
                    ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rUsage: /yugimc fakelag <delay(0-5000)>"));
                    ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §rUsage: /yugimc fakelag randomize"));
                    return 1;
                })
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
                String address = host + ":" + port;
                var hover = Component.literal("§7Click to copy");
                ctx.getSource().sendFeedback(Component.literal("§a[Yugi+] §r")
                        .append(Component.literal(address)
                                .withStyle(Style.EMPTY
                                        .withClickEvent(new ClickEvent.CopyToClipboard(address))
                                        .withHoverEvent(new HoverEvent.ShowText(hover)))));
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return InetAddress.getByName(host).getHostAddress();
                    } catch (Exception e) {
                        return null;
                    }
                }).thenAcceptAsync(resolved -> {
                    if (resolved != null) {
                        var src = ctx.getSource();
                        String resolvedAddress = resolved + ":" + port;
                        src.sendFeedback(Component.literal("§a[Yugi+] §r")
                                .append(Component.literal(resolvedAddress)
                                                .withStyle(Style.EMPTY
                                                        .withClickEvent(new ClickEvent.CopyToClipboard(resolvedAddress))
                                                        .withHoverEvent(new HoverEvent.ShowText(hover)))));
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
                if (!(entity instanceof Player)) {
                    names.add(entity.getName().getString());
                    names.add(EntityType.getKey(entity.getType()).getPath());
                }
            }
            return SharedSuggestionProvider.suggest(names, builder);
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestBlockNames(CommandContext<FabricClientCommandSource> ctx, SuggestionsBuilder builder) {
        List<String> names = new ArrayList<>();
        for (var id : BuiltInRegistries.BLOCK.keySet()) {
            String path = id.getPath();
            if (!names.contains(path)) {
                names.add(path);
            }
        }
        return SharedSuggestionProvider.suggest(names, builder);
    }

    private static Integer parseColor(String input) {
        Integer named = YugiClient.COLOR_NAMES.get(input.toLowerCase());
        if (named != null) return named;
        if (input.startsWith("#")) {
            try {
                return Integer.parseInt(input.substring(1), 16);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private static CompletableFuture<Suggestions> suggestColors(CommandContext<FabricClientCommandSource> ctx, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(YugiClient.COLOR_NAMES.keySet(), builder);
    }
}