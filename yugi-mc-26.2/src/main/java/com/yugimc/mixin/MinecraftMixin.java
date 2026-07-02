package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void onShouldEntityAppearGlowing(Entity entity, CallbackInfoReturnable<Boolean> ci) {
        if (YugiClient.entityOutlineAll || YugiClient.entityOutline) {
            ci.setReturnValue(true);
            return;
        }

        if (!YugiClient.outlinedEntities.isEmpty()
                && !(entity instanceof Player)
                && (YugiClient.outlinedEntities.containsKey(entity.getName().getString())
                    || YugiClient.outlinedEntities.containsKey(EntityType.getKey(entity.getType()).getPath()))) {
            ci.setReturnValue(true);
        }

        if (!YugiClient.outlinedPlayers.isEmpty()
                && entity instanceof Player
                && YugiClient.outlinedPlayers.containsKey(entity.getName().getString())) {
            ci.setReturnValue(true);
        }
    }
}
