package com.yugimc.mixin;

import com.yugimc.YugiClient;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCopy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderSectionRegion.class)
public class RenderSectionRegionMixin {

    @Redirect(method = "getBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/SectionCopy;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState redirectGetBlockState(SectionCopy copy, BlockPos pos) {
        BlockState state = copy.getBlockState(pos);
        if (YugiClient.outlinedBlockTypes.isEmpty()) return state;
        if (YugiClient.outlinedBlocks.contains(state.getBlock())) {
            return state;
        }
        return Blocks.AIR.defaultBlockState();
    }
}
