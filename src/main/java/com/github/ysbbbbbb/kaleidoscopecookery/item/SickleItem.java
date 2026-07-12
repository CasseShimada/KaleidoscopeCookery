package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.SickleHarvestEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SickleItem extends CookeryTooltipItem {
    private static final ToolMaterial SICKLE_MATERIAL = ToolMaterial.STONE;
    private static final float ATTACK_DAMAGE = 3.0F;
    private static final float ATTACK_SPEED = -2.4F;
    private final ThreadLocal<BlockPos> activeHarvestPos = new ThreadLocal<>();

    public SickleItem() {
        this(new Item.Properties());
    }

    public SickleItem(Item.Properties properties) {
        this(SICKLE_MATERIAL, ATTACK_DAMAGE, ATTACK_SPEED, properties);
    }

    public SickleItem(ToolMaterial material, Item.Properties properties) {
        this(material, ATTACK_DAMAGE, ATTACK_SPEED, properties);
    }

    public SickleItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties properties) {
        super(material.applySwordProperties(properties, attackDamage, attackSpeed));
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (pos.equals(activeHarvestPos.get())) {
            return true;
        }
        return super.mineBlock(stack, level, state, pos, entity);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        // 生成挥动音效和粒子
        Player player = context.getPlayer();
        if (player == null) {
            return super.useOn(context);
        }
        if (context.getHand() != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos();
        int breakCount = 0;
        // 搜索方块的 5x5x2 范围内的可收割作物、草丛、灌木等并收割
        ItemStack stack = context.getItemInHand();
        for (int x = -2; x <= 2; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -2; z <= 2; z++) {
                    if (harvest(pos, x, y, z, level, player, stack)) {
                        breakCount++;
                    }
                }
            }
        }

        serverLevel.playSound(null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(),
                1.0F, 1.0F);
        if (!player.hasInfiniteMaterials() && breakCount > 0) {
            stack.hurtAndBreak(breakCount, player, EquipmentSlot.MAINHAND);
        }
        player.getCooldowns().addCooldown(stack, 10);
        return InteractionResult.SUCCESS;
    }

    private boolean harvest(BlockPos pos, int x, int y, int z, Level level, Player player, ItemStack stack) {
        BlockPos newPos = pos.offset(x, y, z);
        if (!level.mayInteract(player, newPos)) {
            return false;
        }
        BlockState blockState = level.getBlockState(newPos);
        if (blockState.isAir()) {
            return false;
        }
        if (blockState.is(TagMod.SICKLE_HARVEST_BLACKLIST)) {
            return false;
        }

        BlockPos previousHarvestPos = activeHarvestPos.get();
        activeHarvestPos.set(newPos);
        try {
            return harvestBlock(newPos, level, player, stack, blockState);
        } finally {
            if (previousHarvestPos == null) {
                activeHarvestPos.remove();
            } else {
                activeHarvestPos.set(previousHarvestPos);
            }
        }
    }

    private boolean harvestBlock(BlockPos newPos, Level level, Player player, ItemStack stack, BlockState blockState) {
        Block block = blockState.getBlock();

        SickleHarvestEvent event = new SickleHarvestEvent(player, stack, newPos, blockState);
        ModEvents.SICKLE_HARVEST.invoker().onSickleHarvest(event);
        if (event.isCanceled()) {
            return event.isCostDurability();
        }

        // 成熟作物走原版破坏流程，以保留 Fabric 破坏事件、权限和工具掉落上下文。
        if (block instanceof CropBlock cropBlock && player instanceof ServerPlayer serverPlayer) {
            RiceCropBlock riceCropBlock = block instanceof RiceCropBlock rice ? rice : null;
            if (riceCropBlock != null) {
                newPos = newPos.below(blockState.getValue(RiceCropBlock.LOCATION));
                activeHarvestPos.set(newPos);
            }
            if (!level.mayInteract(player, newPos)) {
                return false;
            }

            blockState = level.getBlockState(newPos);
            if (!blockState.is(block) || !cropBlock.isMaxAge(blockState)) {
                return false;
            }

            if (riceCropBlock != null) {
                if (blockState.getValue(RiceCropBlock.LOCATION) != RiceCropBlock.DOWN) {
                    return false;
                }
                BlockState middleRiceState = level.getBlockState(newPos.above(RiceCropBlock.MIDDLE));
                BlockState upperRiceState = level.getBlockState(newPos.above(RiceCropBlock.UP));
                if (!serverPlayer.gameMode.destroyBlock(newPos)
                        || level.getBlockState(newPos).equals(blockState)) {
                    return false;
                }
                riceCropBlock.replantAfterHarvestIfUnchanged(level, newPos, blockState, middleRiceState, upperRiceState);
                return true;
            }

            if (!serverPlayer.gameMode.destroyBlock(newPos)
                    || level.getBlockState(newPos).equals(blockState)) {
                return false;
            }
            BlockState replacementState = blockState.getFluidState().createLegacyBlock();
            if (level.getBlockState(newPos).equals(replacementState)) {
                BlockState stateForAge = cropBlock.getStateForAge(0);
                BooleanProperty waterlogged = BlockStateProperties.WATERLOGGED;
                if (stateForAge.hasProperty(waterlogged)) {
                    stateForAge = stateForAge.setValue(waterlogged, blockState.getValue(waterlogged));
                }
                level.setBlock(newPos, stateForAge, Block.UPDATE_ALL);
            }
            return true;
        }

        // 如果是植被，直接破坏
        if (block instanceof VegetationBlock && player instanceof ServerPlayer serverPlayer) {
            return serverPlayer.gameMode.destroyBlock(newPos)
                    && !level.getBlockState(newPos).equals(blockState);
        }
        return false;
    }

    @Override
    protected void appendCookeryTooltip(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.kaleidoscope_cookery.sickle").withStyle(ChatFormatting.GRAY));
    }
}
