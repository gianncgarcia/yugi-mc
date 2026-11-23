package com.yugimc;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public enum YugiClient {
    cliente;

    // Configuration Flags
    public static boolean brandspoof = true;
    public static String brandspoofText = "vanilla";
    public static boolean hstoggledOn = true; // No Hurtcam / shake
    public static boolean abtoggleOn = true; // Anti-blind for warden blind
    public static boolean zoomOn = false;
    public static boolean pgg = true; // Portal Gui
    public static boolean outliningEntities; // Outline entity requirement
    public static boolean outlineON = false; // Outline entity on/off flag
    public static boolean outlineONN = false;
    public static boolean NTSneak = true;

    // Constants
    public static final String MOD_ID = "yugimc";
    public static final double zoomLevel = 0.23;
    public static final Logger LOGGER = LoggerFactory.getLogger("yugimc");
    private static final Gson GSON = new Gson();

    // Minecraft Client Instance
    public static MinecraftClient client;

    public void initialize()
    {


        client = MinecraftClient.getInstance();

        KeyBinding eoo = KeyBindingHelper.registerKeyBinding(new KeyBinding("Entity Outline",
                GLFW.GLFW_KEY_UNKNOWN,
                "Yugi+"));
        KeyBinding NametagSneak = KeyBindingHelper.registerKeyBinding(new KeyBinding("Nametag Sneak",
                GLFW.GLFW_KEY_UNKNOWN,
                "Yugi+"));
        KeyBinding k = KeyBindingHelper.registerKeyBinding(new KeyBinding("Hurt Shake",
                GLFW.GLFW_KEY_UNKNOWN,
                "Yugi+"));
        KeyBinding sf = KeyBindingHelper.registerKeyBinding(new KeyBinding("Brand Spoof",
                GLFW.GLFW_KEY_UNKNOWN,
                "Yugi+"));
        KeyBinding kk = KeyBindingHelper.registerKeyBinding(new KeyBinding("Anti Blind",
                GLFW.GLFW_KEY_UNKNOWN,
                "Yugi+"));
        KeyBinding kg = KeyBindingHelper.registerKeyBinding(new KeyBinding("Zoom",
                GLFW.GLFW_KEY_UNKNOWN,
                "Yugi+"));
        KeyBinding pg = KeyBindingHelper.registerKeyBinding(new KeyBinding("Portal Gui",
                GLFW.GLFW_KEY_UNKNOWN,
                "Yugi+"));
        KeyBinding CONFIG_BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.entity-outliner.selector",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_SEMICOLON,
                "Yugi+"));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Check if the player has the "Darkness" effect
            if (client.player != null) {
                StatusEffectInstance darknessEffect = client.player.getStatusEffect(StatusEffects.DARKNESS);
                if (YugiClient.abtoggleOn && darknessEffect != null) {
                    // Remove the "Darkness" effect
                    client.player.removeStatusEffect(StatusEffects.DARKNESS);
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            //Entity Outline
            if (eoo.wasPressed()) {
                if (outlineON) {
                    outlineON = false;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rDisabled Entity Outlining"), false);
                }
                else {
                    outlineON = true;
                    outliningEntities = !outliningEntities;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rEnabled Entity Outlining"), false);
                }
            }
            //Nametag Visible when Sneaking
            if (NametagSneak.wasPressed()) {
                if (NTSneak) {
                    NTSneak = false;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rNametag Sneak Visibility On"), false);
                }
                else {
                    NTSneak = true;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rNametag Sneak Visibility On"), false);
                }
            }
            //Entity Outline GUI
            if (CONFIG_BIND.wasPressed()) {
                //client.setScreen(new EntitySelector(null));
            }
            //Brand Spoof
            if (sf.wasPressed()) {
                if (brandspoof) {
                    brandspoof = false;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rDisabled Brand Spoofing"), false);

                }
                else  {
                    brandspoof = true;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rEnabled Brand Spoofing"), false);
                }
            }
            // Hurtcam shake
            if (k.wasPressed()) {
                if (hstoggledOn) {
                    hstoggledOn = false;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rDisabled NoHurtCam"), false);
                }
                else {
                    hstoggledOn = true;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rEnabled NoHurtCam"), false);
                }
            }
            // Anti-Blind
            if (kk.wasPressed()) {
                if (abtoggleOn) {
                    abtoggleOn = false;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rDisabled Anti-Blind"), false);
                }
                else {
                    abtoggleOn = true;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rEnabled Anti-Blind"), false);
                }

            }
            // Zoom
            if (kg.wasPressed()) {
                if (zoomOn) {
                    zoomOn = false;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rUnzoomed"), false);
                }
                else {
                    zoomOn = true;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rZooming"), false);
                }
            }
            // Portal Enable Gui
            if (pg.wasPressed()) {
                if (pgg) {
                    pgg = false;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rDisabled Portal Gui"), false);
                }
                else {
                    pgg = true;
                    client.player.sendMessage(Text.of("§a[Yugi+] §rEnabled Portal Gui"), false);
                }
            }
        });
    }
}