package com.github.ysbbbbbb.kaleidoscopecookery.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;

public class StrawHatItem extends ModArmorItem {
    private final boolean hasFlower;

    public StrawHatItem(boolean hasFlower, Item.Properties properties) {
        super(ArmorMaterials.LEATHER, ArmorType.HELMET, properties.stacksTo(1));
        this.hasFlower = hasFlower;
    }

    public boolean hasFlower() {
        return hasFlower;
    }
}
