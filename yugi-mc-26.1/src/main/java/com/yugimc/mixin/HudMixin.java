package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(Gui.class)
public class HudMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void onExtractRenderState(GuiGraphicsExtractor extractor, DeltaTracker delta, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) return;
        if (!YugiClient.hudVisible) return;

        Gui self = (Gui) (Object) this;
        Font font = self.getFont();
        var lines = new ArrayList<String>();

        if (YugiClient.vanillaSpoof) lines.add("§7VanillaSpoof: §aOn");
        if (YugiClient.noHurtCam) lines.add("§7NoHurtCam: §aOn");
        if (YugiClient.antiBlind) lines.add("§7AntiBlind: §aOn");
        if (YugiClient.zoomEnabled) lines.add("§7Zoom: §aOn");
        if (YugiClient.portalGuiEnabled) lines.add("§7PortalGui: §aOn");
        if (YugiClient.entityOutlineAll || YugiClient.entityOutline) lines.add("§7EntityOutline: §aOn");
        if (YugiClient.nametagSneak) lines.add("§7NametagSneak: §aOn");
        if (YugiClient.noMiningCooldown) lines.add("§7NoMineCD: §aOn");
        if (YugiClient.fakeLagEnabled) lines.add("§7FakeLag: §aOn §7/ §f" + YugiClient.fakelagDelay + "ms");
        if (YugiClient.fastPickup) lines.add("§7FastPickup: §aOn");
        if (YugiClient.pickupMagnet) lines.add("§7PickupMagnet: §aOn");

        int lineCount = 1 + lines.size();
        int totalHeight = lineCount * 10 + 4;
        int x = 4;
        int y = (extractor.guiHeight() - totalHeight) / 2;

        extractor.text(font, "§a[Yugi+]", x, y, -1, true);
        y += 12;

        for (var line : lines) {
            extractor.text(font, line, x, y, -1, true);
            y += 10;
        }
    }
}
