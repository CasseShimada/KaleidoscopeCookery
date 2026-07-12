package com.github.ysbbbbbb.kaleidoscopecookery.client.render.item;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.StrawHatModel;
import com.github.ysbbbbbb.kaleidoscopecookery.item.StrawHatItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class StrawHatArmorRenderer implements ArmorRenderer {
    private static final Identifier NORMAL = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/models/armor/straw_hat.png");
    private static final Identifier FLOWER = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/models/armor/straw_hat_flower.png");
    private StrawHatModel cachedModel = null;

    @Override
    public void render(PoseStack matrices, SubmitNodeCollector collector, ItemStack stack, HumanoidRenderState state,
                       EquipmentSlot slot, int light, HumanoidModel<HumanoidRenderState> contextModel) {
        if (cachedModel == null) {
            cachedModel = new StrawHatModel(Minecraft.getInstance().getEntityModels().bakeLayer(StrawHatModel.LAYER_LOCATION));
        }
        Identifier texture = getArmorTexture(stack);
        ArmorRenderer.submitTransformCopyingModel(contextModel, state, cachedModel, state, true,
                collector, matrices, cachedModel.renderType(texture), light, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
    }

    public Identifier getArmorTexture(ItemStack stack) {
        if (stack.getItem() instanceof StrawHatItem hatItem && hatItem.hasFlower()) {
            return FLOWER;
        }
        return NORMAL;
    }
}
