package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin { // Feature: Fast Pickup / Pickup Magnet

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!self.level().isClientSide()) return;

        // Feature: Fast Pickup
        if (YugiClient.fastPickup) {
            self.setPickUpDelay(0);
        }

        // Feature: Pickup Magnet
        if (!YugiClient.pickupMagnet) return;

        Player nearest = null;
        double nearestDist = 100.0;
        for (Player player : self.level().players()) {
            if (player.isSpectator()) continue;
            double dist = self.distanceToSqr(player);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = player;
            }
        }

        if (nearest != null && nearestDist > 1.0 && nearestDist < 2.25) {
            Vec3 motion = nearest.position().subtract(self.position()).normalize().scale(0.4);
            self.setDeltaMovement(motion);
        }
    }
}
