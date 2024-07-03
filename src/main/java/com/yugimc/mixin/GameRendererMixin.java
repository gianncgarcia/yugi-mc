package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	// HURT SHAKE
	@Inject(at = @At("HEAD"), method = "tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V", cancellable = true)
	public void bobViewWhenHurt(MatrixStack matrixStack_1, float float_1, CallbackInfo ci) {
		if (YugiClient.hstoggledOn) {
			ci.cancel();
		}
	}
}