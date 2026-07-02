package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderSectionRegion.class)
public class RenderSectionRegionMixin {

    @Inject(method = "getBlockState", at = @At("RETURN"))
    private void onGetBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if (YugiClient.outlinedBlockTypes.isEmpty()) return;
        BlockState state = cir.getReturnValue();
        if (!YugiClient.outlinedBlocks.contains(state.getBlock())) {
            cir.setReturnValue(Blocks.AIR.defaultBlockState());
        }
    }
}
