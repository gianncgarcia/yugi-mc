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

        for (BlockPos pos : YugiClient.cachedBlockPositions) {
            Gizmos.cuboid(pos, 0.05f, GizmoStyle.fill(0x15FFDD00));
            Gizmos.cuboid(pos, GizmoStyle.strokeAndFill(0xFFFFDD00, 2.5f, 0x40FFDD00));
        }
    }
}
