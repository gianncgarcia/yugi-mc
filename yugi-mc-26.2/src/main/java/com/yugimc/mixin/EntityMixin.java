package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void onGetTeamColor(CallbackInfoReturnable<Integer> ci) {
        Entity self = (Entity) (Object) this;

        boolean outlined = YugiClient.entityOutlineAll
                || YugiClient.entityOutline
                || (!(self instanceof Player) && YugiClient.outlinedEntities.contains(self.getName().getString()))
                || (self instanceof Player && YugiClient.outlinedPlayers.contains(self.getName().getString()));

        if (!outlined) return;

        if (self instanceof Player player) {
            var team = player.getTeam();
            if (team != null) {
                var colorOpt = team.getColor();
                if (colorOpt.isPresent()) {
                    ci.setReturnValue(colorOpt.get().rgb());
                    return;
                }
            }
        }

        Identifier id = EntityType.getKey(self.getType());
        Integer custom = YugiClient.MOB_COLORS.get(id);
        int color = custom != null ? custom : colorFromName(id.getPath());
        ci.setReturnValue(color);
    }

    private static int colorFromName(String name) {
        int hash = Math.abs(name.hashCode());
        float hue = (hash % 360) / 360.0f;
        float sat = 0.65f + ((hash >> 8) & 0x1F) / 100.0f;
        float light = 0.4f + ((hash >> 13) & 0x1F) / 100.0f;
        return hslToRgb(hue, Math.min(sat, 0.85f), Math.min(light, 0.7f));
    }

    private static int hslToRgb(float h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h * 6) % 2 - 1));
        float m = l - c / 2;
        float r, g, b;
        int sextant = (int) (h * 6);
        switch (sextant) {
            case 0 -> { r = c; g = x; b = 0; }
            case 1 -> { r = x; g = c; b = 0; }
            case 2 -> { r = 0; g = c; b = x; }
            case 3 -> { r = 0; g = x; b = c; }
            case 4 -> { r = x; g = 0; b = c; }
            case 5 -> { r = c; g = 0; b = x; }
            default -> { r = 0; g = 0; b = 0; }
        }
        return ((int) ((r + m) * 255) << 16) | ((int) ((g + m) * 255) << 8) | (int) ((b + m) * 255);
    }
}
