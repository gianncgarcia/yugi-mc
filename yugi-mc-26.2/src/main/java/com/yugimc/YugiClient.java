package com.yugimc;

import com.yugimc.util.ModCommandRegister;
import com.yugimc.util.PacketUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;

public enum YugiClient { // Feature: Core / All Features
    cliente;

    // Configuration Flags
    public static boolean vanillaSpoof = true;
    public static boolean noHurtCam = true;
    public static boolean antiBlind = true;
    public static boolean zoomEnabled = false;
    public static boolean portalGuiEnabled = true;
    public static boolean entityOutline = false;
    public static boolean entityOutlineAll = false;
    public static boolean nametagSneak = true;
    public static boolean noMiningCooldown = true;
    public static boolean fakeLagEnabled = false;
    public static boolean isFlushing = false;
    public static long fakelagDelay = 500;
    public static long lastFlushTime = 0;
    public static boolean fakelagRandomize = false;
    public static final ArrayList<Packet<?>> packetQueue = new ArrayList<>();
    public static boolean pickupMagnet = false;
    public static boolean fastPickup = true;
    public static boolean hudVisible = false;
    public static long lastHudToggleTime = 0;
    public static java.util.HashMap<String, Integer> outlinedEntities = new java.util.HashMap<>();
    public static java.util.HashMap<String, Integer> outlinedPlayers = new java.util.HashMap<>();
    public static final java.util.Set<Identifier> outlinedBlockTypes = new java.util.HashSet<>();
    public static final java.util.Set<net.minecraft.world.level.block.Block> outlinedBlocks = new java.util.HashSet<>();
    public static final java.util.Set<net.minecraft.core.BlockPos> cachedBlockPositions = java.util.concurrent.ConcurrentHashMap.newKeySet();
    public static long lastBlockScanTime = 0;
    public static int blockScanRange = 48;
    public static final java.util.Map<Identifier, Integer> MOB_COLORS = new java.util.HashMap<>();
    public static final java.util.Map<String, Integer> COLOR_NAMES = new java.util.HashMap<>();

    static {
        var cn = COLOR_NAMES;
        cn.put("black", 0x000000);
        cn.put("dark_blue", 0x0000AA);
        cn.put("dark_green", 0x00AA00);
        cn.put("dark_aqua", 0x00AAAA);
        cn.put("dark_red", 0xAA0000);
        cn.put("dark_purple", 0xAA00AA);
        cn.put("gold", 0xFFAA00);
        cn.put("gray", 0xAAAAAA);
        cn.put("dark_gray", 0x555555);
        cn.put("blue", 0x5555FF);
        cn.put("green", 0x55FF55);
        cn.put("aqua", 0x55FFFF);
        cn.put("red", 0xFF5555);
        cn.put("light_purple", 0xFF55FF);
        cn.put("yellow", 0xFFFF55);
        cn.put("white", 0xFFFFFF);
        cn.put("pink", 0xFFAEB9);

        var c = MOB_COLORS;
        c.put(Identifier.withDefaultNamespace("slime"), 0x7ECB7E);
        c.put(Identifier.withDefaultNamespace("creeper"), 0x3B7A3B);
        c.put(Identifier.withDefaultNamespace("pig"), 0xF0A8A8);
        c.put(Identifier.withDefaultNamespace("cow"), 0x8B4513);
        c.put(Identifier.withDefaultNamespace("sheep"), 0xE8E8E8);
        c.put(Identifier.withDefaultNamespace("chicken"), 0xF5F0E0);
        c.put(Identifier.withDefaultNamespace("spider"), 0x2C2C2C);
        c.put(Identifier.withDefaultNamespace("cave_spider"), 0x1E5A5A);
        c.put(Identifier.withDefaultNamespace("zombie"), 0x3B7A3B);
        c.put(Identifier.withDefaultNamespace("skeleton"), 0xC0C0C0);
        c.put(Identifier.withDefaultNamespace("wither_skeleton"), 0x1A1A1A);
        c.put(Identifier.withDefaultNamespace("enderman"), 0x1A1A1A);
        c.put(Identifier.withDefaultNamespace("witch"), 0x5C3A5C);
        c.put(Identifier.withDefaultNamespace("warden"), 0x2A4A6A);
        c.put(Identifier.withDefaultNamespace("blaze"), 0xFFD700);
        c.put(Identifier.withDefaultNamespace("ghast"), 0xF5F5F5);
        c.put(Identifier.withDefaultNamespace("magma_cube"), 0x8B2500);
        c.put(Identifier.withDefaultNamespace("squid"), 0x2B2B5A);
        c.put(Identifier.withDefaultNamespace("dolphin"), 0x4A7A9A);
        c.put(Identifier.withDefaultNamespace("axolotl"), 0xFFA0B0);
        c.put(Identifier.withDefaultNamespace("goat"), 0x8B8378);
        c.put(Identifier.withDefaultNamespace("fox"), 0xD4763A);
        c.put(Identifier.withDefaultNamespace("wolf"), 0xC0C0C0);
        c.put(Identifier.withDefaultNamespace("cat"), 0xF5DEB3);
        c.put(Identifier.withDefaultNamespace("bee"), 0xD4A000);
        c.put(Identifier.withDefaultNamespace("iron_golem"), 0x8B8B8B);
        c.put(Identifier.withDefaultNamespace("snow_golem"), 0xF0F0F0);
        c.put(Identifier.withDefaultNamespace("villager"), 0x8B5A2B);
        c.put(Identifier.withDefaultNamespace("evoker"), 0x5C5C5C);
        c.put(Identifier.withDefaultNamespace("vindicator"), 0x6B5B4B);
        c.put(Identifier.withDefaultNamespace("pillager"), 0x5C5C5C);
        c.put(Identifier.withDefaultNamespace("ravager"), 0x6B5B4B);
        c.put(Identifier.withDefaultNamespace("vex"), 0x8C8C8C);
        c.put(Identifier.withDefaultNamespace("phantom"), 0x2B5C8A);
        c.put(Identifier.withDefaultNamespace("pufferfish"), 0xD4C44A);
        c.put(Identifier.withDefaultNamespace("salmon"), 0x8B6363);
        c.put(Identifier.withDefaultNamespace("cod"), 0x8B7355);
        c.put(Identifier.withDefaultNamespace("turtle"), 0x2B5C2B);
        c.put(Identifier.withDefaultNamespace("panda"), 0xE8E8E8);
        c.put(Identifier.withDefaultNamespace("polar_bear"), 0xF5F5F5);
        c.put(Identifier.withDefaultNamespace("llama"), 0x8B7355);
        c.put(Identifier.withDefaultNamespace("horse"), 0x8B5A2B);
        c.put(Identifier.withDefaultNamespace("bat"), 0x2B2B2B);
        c.put(Identifier.withDefaultNamespace("ocelot"), 0xD4A060);
        c.put(Identifier.withDefaultNamespace("parrot"), 0x4AB44A);
        c.put(Identifier.withDefaultNamespace("mooshroom"), 0x8B2500);
        c.put(Identifier.withDefaultNamespace("rabbit"), 0x8B7355);
        c.put(Identifier.withDefaultNamespace("guardian"), 0x4AA4D4);
        c.put(Identifier.withDefaultNamespace("elder_guardian"), 0x5C5C5C);
        c.put(Identifier.withDefaultNamespace("shulker"), 0x8B5AB5);
        c.put(Identifier.withDefaultNamespace("silverfish"), 0x6B6B6B);
        c.put(Identifier.withDefaultNamespace("endermite"), 0x4A2B4A);
        c.put(Identifier.withDefaultNamespace("allay"), 0x8AB5D4);
        c.put(Identifier.withDefaultNamespace("frog"), 0x4AB44A);
        c.put(Identifier.withDefaultNamespace("tadpole"), 0x2B2B2B);
        c.put(Identifier.withDefaultNamespace("camel"), 0xD4A060);
        c.put(Identifier.withDefaultNamespace("sniffer"), 0x4A7A4A);
        c.put(Identifier.withDefaultNamespace("breeze"), 0x8BC4E8);
        c.put(Identifier.withDefaultNamespace("zombified_piglin"), 0xFFC0CB);
        c.put(Identifier.withDefaultNamespace("zombie_villager"), 0x3B6B3B);
        c.put(Identifier.withDefaultNamespace("donkey"), 0x7A6B5A);
        c.put(Identifier.withDefaultNamespace("mule"), 0x7A6B5A);
        c.put(Identifier.withDefaultNamespace("skeleton_horse"), 0x6B5A4B);
        c.put(Identifier.withDefaultNamespace("zombie_horse"), 0x3B6B3B);
        c.put(Identifier.withDefaultNamespace("illusioner"), 0x5C5C5C);
        c.put(Identifier.withDefaultNamespace("trader_llama"), 0x8B7355);
        c.put(Identifier.withDefaultNamespace("wandering_trader"), 0x5C4A7A);
    }

    // Constants
    public static final String MOD_ID = "yugimc";
    public static final double zoomMultiplier = 0.23;
    public static final Logger LOGGER = LoggerFactory.getLogger("yugimc");

    // Custom Key Category
    public static final KeyMapping.Category YUGI_CATEGORY = KeyMapping.Category.register(Identifier.withDefaultNamespace("yugi-plus"));

    // Minecraft Client Instance
    public static Minecraft client;

    public void initialize() { // Feature: Core / All Features
        client = Minecraft.getInstance();

        var keyEntityOutline = KeyMappingHelper.registerKeyMapping(new KeyMapping("Entity Outline",
                GLFW.GLFW_KEY_UNKNOWN,
                YUGI_CATEGORY));
        var keyNametagSneak = KeyMappingHelper.registerKeyMapping(new KeyMapping("Nametag Sneak",
                GLFW.GLFW_KEY_UNKNOWN,
                YUGI_CATEGORY));
        var keyVanillaSpoof = KeyMappingHelper.registerKeyMapping(new KeyMapping("Vanilla Spoof",
                GLFW.GLFW_KEY_UNKNOWN,
                YUGI_CATEGORY));
        var keyAntiBlind = KeyMappingHelper.registerKeyMapping(new KeyMapping("Anti Blind",
                GLFW.GLFW_KEY_UNKNOWN,
                YUGI_CATEGORY));
        var keyZoom = KeyMappingHelper.registerKeyMapping(new KeyMapping("Zoom",
                GLFW.GLFW_KEY_UNKNOWN,
                YUGI_CATEGORY));
        var keyHurtShake = KeyMappingHelper.registerKeyMapping(new KeyMapping("Hurt Shake",
                GLFW.GLFW_KEY_UNKNOWN,
                YUGI_CATEGORY));
        var keyPortalGui = KeyMappingHelper.registerKeyMapping(new KeyMapping("Portal Gui",
                GLFW.GLFW_KEY_UNKNOWN,
                YUGI_CATEGORY));
        var keyNoMiningCooldown = KeyMappingHelper.registerKeyMapping(new KeyMapping("No Mining Cooldown",
                GLFW.GLFW_KEY_UNKNOWN,
                YUGI_CATEGORY));
        var keyFakeLag = KeyMappingHelper.registerKeyMapping(new KeyMapping("FakeLag",
                GLFW.GLFW_KEY_UNKNOWN,
                YUGI_CATEGORY));
        var keyPickupMagnet = KeyMappingHelper.registerKeyMapping(new KeyMapping("Pickup Magnet",
                GLFW.GLFW_KEY_UNKNOWN,
                YUGI_CATEGORY));
        var keyFastPickup = KeyMappingHelper.registerKeyMapping(new KeyMapping("Fast Pickup",
                GLFW.GLFW_KEY_UNKNOWN,
                YUGI_CATEGORY));

        // Feature: Keybind Handlers
        ClientTickEvents.END_CLIENT_TICK.register(tickClient -> {
            if (client.player == null) return;

            if (keyEntityOutline.consumeClick()) {
                entityOutline = !entityOutline;
                client.player.sendSystemMessage(Component.literal(
                    entityOutline ? "§a[Yugi+] §rEnabled Entity Outlining" : "§a[Yugi+] §rDisabled Entity Outlining"));
            }

            if (keyNametagSneak.consumeClick()) {
                nametagSneak = !nametagSneak;
                client.player.sendSystemMessage(Component.literal(
                    nametagSneak ? "§a[Yugi+] §rNametag Sneak Visibility On" : "§a[Yugi+] §rNametag Sneak Visibility Off"));
            }

            if (keyVanillaSpoof.consumeClick()) {
                vanillaSpoof = !vanillaSpoof;
                client.player.sendSystemMessage(Component.literal(
                    vanillaSpoof ? "§a[Yugi+] §rEnabled Vanilla Spoofing" : "§a[Yugi+] §rDisabled Vanilla Spoofing"));
            }

            if (keyAntiBlind.consumeClick()) {
                antiBlind = !antiBlind;
                client.player.sendSystemMessage(Component.literal(
                    antiBlind ? "§a[Yugi+] §rEnabled Anti-Blind" : "§a[Yugi+] §rDisabled Anti-Blind"));
            }

            if (keyZoom.consumeClick()) {
                zoomEnabled = !zoomEnabled;
                client.player.sendSystemMessage(Component.literal(
                    zoomEnabled ? "§a[Yugi+] §rZooming" : "§a[Yugi+] §rUnzoomed"));
            }

            if (keyHurtShake.consumeClick()) {
                noHurtCam = !noHurtCam;
                client.player.sendSystemMessage(Component.literal(
                    noHurtCam ? "§a[Yugi+] §rEnabled NoHurtCam" : "§a[Yugi+] §rDisabled NoHurtCam"));
            }

            if (keyPortalGui.consumeClick()) {
                portalGuiEnabled = !portalGuiEnabled;
                client.player.sendSystemMessage(Component.literal(
                    portalGuiEnabled ? "§a[Yugi+] §rEnabled Portal Gui" : "§a[Yugi+] §rDisabled Portal Gui"));
            }

            if (keyNoMiningCooldown.consumeClick()) {
                noMiningCooldown = !noMiningCooldown;
                client.player.sendSystemMessage(Component.literal(
                    noMiningCooldown ? "§a[Yugi+] §rEnabled No Mining Cooldown" : "§a[Yugi+] §rDisabled No Mining Cooldown"));
            }

            if (keyFakeLag.consumeClick()) {
                fakeLagEnabled = !fakeLagEnabled;
                if (!fakeLagEnabled) {
                    PacketUtil.flushPackets();
                } else {
                    lastFlushTime = System.currentTimeMillis();
                }
                client.player.sendSystemMessage(Component.literal(
                    fakeLagEnabled ? "§a[Yugi+] §rFakeLag On" : "§a[Yugi+] §rFakeLag Off"));
            }

            if (keyPickupMagnet.consumeClick()) {
                pickupMagnet = !pickupMagnet;
                client.player.sendSystemMessage(Component.literal(
                    pickupMagnet ? "§a[Yugi+] §rPickup Magnet Enabled" : "§a[Yugi+] §rPickup Magnet Disabled"));
            }

            if (keyFastPickup.consumeClick()) {
                fastPickup = !fastPickup;
                client.player.sendSystemMessage(Component.literal(
                    fastPickup ? "§a[Yugi+] §rFast Pickup Enabled" : "§a[Yugi+] §rFast Pickup Disabled"));
            }

            if (GLFW.glfwGetKey(client.getWindow().handle(), GLFW.GLFW_KEY_V) == GLFW.GLFW_PRESS
                    && GLFW.glfwGetKey(client.getWindow().handle(), GLFW.GLFW_KEY_B) == GLFW.GLFW_PRESS) {
                long now = System.currentTimeMillis();
                if (now - lastHudToggleTime > 500) {
                    lastHudToggleTime = now;
                    hudVisible = !hudVisible;
                    client.player.sendSystemMessage(Component.literal(
                        hudVisible ? "§a[Yugi+] §rHUD Visible" : "§a[Yugi+] §rHUD Hidden"));
                }
            }

            // Feature: Block Outline (Scan)
            if (!outlinedBlockTypes.isEmpty()) {
                long now = System.currentTimeMillis();
                if (now - lastBlockScanTime > 1000) {
                    lastBlockScanTime = now;
                    cachedBlockPositions.clear();
                    var level = client.level;
                    if (level != null) {
                        var playerPos = client.player.blockPosition();
                        int px = playerPos.getX();
                        int pz = playerPos.getZ();
                        int range = blockScanRange;
                        var mutable = new net.minecraft.core.BlockPos.MutableBlockPos();
                        for (int x = px - range; x <= px + range; x += 16) {
                            for (int z = pz - range; z <= pz + range; z += 16) {
                                if (!level.hasChunk(x >> 4, z >> 4)) continue;
                                int cx = Math.min(x + 15, px + range);
                                int cz = Math.min(z + 15, pz + range);
                                for (int bx = x; bx <= cx; bx++) {
                                    for (int bz = z; bz <= cz; bz++) {
                                        int minY = Math.max(level.getMinY(), playerPos.getY() - 16);
                                        int maxY = Math.min(level.getMaxY(), playerPos.getY() + 16);
                                        for (int by = minY; by <= maxY; by++) {
                                            var state = level.getBlockState(mutable.set(bx, by, bz));
                                            if (outlinedBlocks.contains(state.getBlock())) {
                                                cachedBlockPositions.add(mutable.immutable());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Feature: FakeLag (Tick)
            if (fakeLagEnabled) {
                long now = System.currentTimeMillis();
                if (now - lastFlushTime >= fakelagDelay) {
                    PacketUtil.flushPackets();
                    if (fakelagRandomize) {
                        lastFlushTime = now + (long)((Math.random() - 0.5) * fakelagDelay * 0.8);
                    } else {
                        lastFlushTime = now;
                    }
                }
            }
        });

        ModCommandRegister.registerCommands();

        // Feature: State Reset
        ClientPlayConnectionEvents.DISCONNECT.register(((listener, client1) -> {
            outlinedEntities.clear();
            outlinedPlayers.clear();
            outlinedBlockTypes.clear();
            outlinedBlocks.clear();
            cachedBlockPositions.clear();
            entityOutlineAll = false;
            fakeLagEnabled = false;
            fakelagDelay = 100;
            fakelagRandomize = false;
            packetQueue.clear();
            isFlushing = false;
        }));
    }
}
