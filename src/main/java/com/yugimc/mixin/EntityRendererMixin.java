package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    private Entity currentEntity;


    // renders nametag while sneaking
    @Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaky()Z"))
    private boolean redirectIsSneaky(Entity entity) {
        if (YugiClient.NTSneak) {
            return false;
        }
        return entity.isSneaky();
    }
}
