package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.AbstractTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin
{
    // ANTI-BLIND FULL
    @Inject(at = @At("HEAD"),
            method = "hasBlindnessOrDarkness(Lnet/minecraft/client/render/Camera;)Z",
            cancellable = true)
    private void onHasBlindnessOrDarknessEffect(Camera camera, CallbackInfoReturnable<Boolean> ci)  {
        if(YugiClient.abtoggleOn) {
            ci.setReturnValue(false);
        }
    }

    @Inject(method = "renderEntity", at = @At("HEAD"))
    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (YugiClient.outlineON && vertexConsumers instanceof OutlineVertexConsumerProvider) {

            OutlineVertexConsumerProvider outlineVertexConsumers = (OutlineVertexConsumerProvider) vertexConsumers;


            if (entity.getType() == EntityType.SLIME) {
                outlineVertexConsumers.setColor(0, 255, 0, 255);
            }

            if (entity.getType() == EntityType.PILLAGER) {
                outlineVertexConsumers.setColor(128, 128, 128, 255);
            }

            if (entity.getType() == EntityType.RAVAGER) {
                outlineVertexConsumers.setColor(150, 128, 128, 255);
            }

            if (entity.getType() == EntityType.VINDICATOR) {
                outlineVertexConsumers.setColor(128, 128, 128, 255);
            }

            if (entity.getType() == EntityType.VEX) {
                outlineVertexConsumers.setColor(128, 128, 128, 255);
            }

            if (entity.getType() == EntityType.CREEPER) {
                outlineVertexConsumers.setColor(0, 255, 0, 255);
            }

            if (entity.getType() == EntityType.ZOMBIFIED_PIGLIN) {
                outlineVertexConsumers.setColor(255, 192, 203, 255);
            }

            if (entity.getType() == EntityType.ZOMBIE_VILLAGER) {
                outlineVertexConsumers.setColor(0, 240, 0, 255);
            }


            if (entity.getType() == EntityType.WITCH) {
                outlineVertexConsumers.setColor(128, 0, 128, 255);
            }

            if (entity.getType() == EntityType.WITHER_SKELETON) {
                outlineVertexConsumers.setColor(0, 0, 0, 255);
            }

            if (entity.getType() == EntityType.SPIDER) {
                outlineVertexConsumers.setColor(28, 25, 24, 255);
            }

            if (entity.getType() == EntityType.BLAZE) {
                outlineVertexConsumers.setColor(255, 255, 0, 255);
            }

            if (entity.getType() == EntityType.PLAYER) {
                PlayerEntity player = (PlayerEntity) entity;
                AbstractTeam team = player.getScoreboardTeam();
                var ifmurderer = player.getMainHandStack().getItem() == Items.DIAMOND_SWORD;
                var ifdetective = player.getMainHandStack().getItem() == Items.BOW;
                if (!ifmurderer)
                    assert false;
                else
                    outlineVertexConsumers.setColor(255,0, 0, 255);
                if (!ifdetective)
                    assert false;
                else
                    outlineVertexConsumers.setColor(0, 0, 255, 255);
                if (team != null && team.getColor().getColorValue() != null) {
                    int hexColor = team.getColor().getColorValue();
                    int blue = hexColor % 256;
                    int green = (hexColor / 256) % 256;
                    int red = (hexColor / 65536) % 256;
                    outlineVertexConsumers.setColor(red, green, blue, 255);
                    //titi
                }
            }
        }
    }
}