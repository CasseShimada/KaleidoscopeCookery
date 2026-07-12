package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonStructureResolver.class)
public abstract class PistonStructureResolverMixin {
    @Inject(method = "isSticky", at = @At("HEAD"), cancellable = true)
    private static void kaleidoscopeCookery$isSticky(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(ModBlocks.OIL_BLOCK)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canStickToEachOther", at = @At("HEAD"), cancellable = true)
    private static void kaleidoscopeCookery$canStickToEachOther(BlockState state, BlockState other,
                                                                CallbackInfoReturnable<Boolean> cir) {
        if ((state.is(ModBlocks.OIL_BLOCK) && other.is(Blocks.HONEY_BLOCK))
                || (state.is(Blocks.HONEY_BLOCK) && other.is(ModBlocks.OIL_BLOCK))) {
            cir.setReturnValue(false);
        }
    }
}
