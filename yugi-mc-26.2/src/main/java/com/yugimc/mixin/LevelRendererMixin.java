package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.Gizmos.TemporaryCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "collectPerFrameRenderThreadGizmos", at = @At("RETURN"))
    private void onCollectGizmos(CallbackInfoReturnable<TemporaryCollection> cir) {
        if (YugiClient.cachedBlockPositions.isEmpty()) return;

        int color = 0xFF55FF55;
        for (BlockPos pos : YugiClient.cachedBlockPositions) {
            Gizmos.cuboid(pos, GizmoStyle.strokeAndFill(color, 2.0f, 0x30FF55FF));
        }
    }
}
