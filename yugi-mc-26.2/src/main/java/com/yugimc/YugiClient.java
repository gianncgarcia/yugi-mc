package com.yugimc;

import com.yugimc.util.ModCommandRegister;
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

import java.util.HashMap;
import java.util.Map;

public enum YugiClient {
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
    public static java.util.HashMap<String, Integer> outlinedEntities = new java.util.HashMap<>();
    public static java.util.HashMap<String, Integer> outlinedPlayers = new java.util.HashMap<>();
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

    public void initialize() {
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

        ClientTickEvents.END_CLIENT_TICK.register(tickClient -> {
            if (client.player == null) return;

            if (keyEntityOutline.consumeClick()) {
                entityOutline = !entityOutline;
                client.player.sendSystemMessage(Component.literal(
                    entityOutline ? "§9[Yugi+] §rEnabled Entity Outlining" : "§9[Yugi+] §rDisabled Entity Outlining"));
            }

            if (keyNametagSneak.consumeClick()) {
                nametagSneak = !nametagSneak;
                client.player.sendSystemMessage(Component.literal(
                    nametagSneak ? "§9[Yugi+] §rNametag Sneak Visibility On" : "§9[Yugi+] §rNametag Sneak Visibility Off"));
            }

            if (keyVanillaSpoof.consumeClick()) {
                vanillaSpoof = !vanillaSpoof;
                client.player.sendSystemMessage(Component.literal(
                    vanillaSpoof ? "§9[Yugi+] §rEnabled Vanilla Spoofing" : "§9[Yugi+] §rDisabled Vanilla Spoofing"));
            }

            if (keyAntiBlind.consumeClick()) {
                antiBlind = !antiBlind;
                client.player.sendSystemMessage(Component.literal(
                    antiBlind ? "§9[Yugi+] §rEnabled Anti-Blind" : "§9[Yugi+] §rDisabled Anti-Blind"));
            }

            if (keyZoom.consumeClick()) {
                zoomEnabled = !zoomEnabled;
                client.player.sendSystemMessage(Component.literal(
                    zoomEnabled ? "§9[Yugi+] §rZooming" : "§9[Yugi+] §rUnzoomed"));
            }

            if (keyHurtShake.consumeClick()) {
                noHurtCam = !noHurtCam;
                client.player.sendSystemMessage(Component.literal(
                    noHurtCam ? "§9[Yugi+] §rEnabled NoHurtCam" : "§9[Yugi+] §rDisabled NoHurtCam"));
            }

            if (keyPortalGui.consumeClick()) {
                portalGuiEnabled = !portalGuiEnabled;
                client.player.sendSystemMessage(Component.literal(
                    portalGuiEnabled ? "§9[Yugi+] §rEnabled Portal Gui" : "§9[Yugi+] §rDisabled Portal Gui"));
            }

            if (keyNoMiningCooldown.consumeClick()) {
                noMiningCooldown = !noMiningCooldown;
                client.player.sendSystemMessage(Component.literal(
                    noMiningCooldown ? "§9[Yugi+] §rEnabled No Mining Cooldown" : "§9[Yugi+] §rDisabled No Mining Cooldown"));
            }
        });

        ModCommandRegister.registerCommands();

        ClientPlayConnectionEvents.DISCONNECT.register(((listener, client1) -> {
            outlinedEntities.clear();
            outlinedPlayers.clear();
            entityOutlineAll = false;
        }));
    }
}
