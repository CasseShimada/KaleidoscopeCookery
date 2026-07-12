package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.ActionEventCallback;
import com.github.ysbbbbbb.kaleidoscopecookery.event.SpecialRecipeItemEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.interaction.CaterpillarChickenFeedEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.interaction.WetFieldHoeUseEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.recipe.MillstoneSpecialFinishEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.recipe.MillstoneSpecialRecipeEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.AddVillageStructuresEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.SickleHarvestNetherWartEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.ServerEntityLoadEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.TrashCanHideEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect.FarmerArmorEffectEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect.FlatulenceServerEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect.HinderEffectEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect.InstantSmeltingEffectEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect.PreservationEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect.SatiatedShieldEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect.VitalityEffectEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.loot.ExtraLootTableDrop;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

// 所有的自定义事件
public final class ModEvents {
    private ModEvents() {
    }

    public static final Event<ActionEventCallback.MillstoneFinish> MILLSTONE_FINISH =
            EventFactory.createArrayBacked(ActionEventCallback.MillstoneFinish.class, call -> action -> {
                for (ActionEventCallback.MillstoneFinish listener : call) {
                    listener.onMillstoneFinish(action);
                }
            });

    public static final Event<ActionEventCallback.MillstoneTakeItem> MILLSTONE_TAKE_ITEM =
            EventFactory.createArrayBacked(ActionEventCallback.MillstoneTakeItem.class, call -> action -> {
                for (ActionEventCallback.MillstoneTakeItem listener : call) {
                    listener.onMillstoneTakeItem(action);
                }
            });

    public static final Event<ActionEventCallback.CheckSpecialItem> CHECK_SPECIAL_ITEM =
            EventFactory.createArrayBacked(ActionEventCallback.CheckSpecialItem.class, call -> action -> {
                for (ActionEventCallback.CheckSpecialItem listener : call) {
                    listener.onCheckItemEvent(action);
                }
            });

    public static final Event<ActionEventCallback.DeductSpecialItem> DEDUCT_SPECIAL_ITEM =
            EventFactory.createArrayBacked(ActionEventCallback.DeductSpecialItem.class, call -> action -> {
                for (ActionEventCallback.DeductSpecialItem listener : call) {
                    listener.onDeductItemEvent(action);
                }
            });

    public static final Event<ActionEventCallback.LivingEntityHurt> LIVING_ENTITY_HURT =
            EventFactory.createArrayBacked(ActionEventCallback.LivingEntityHurt.class, call -> action -> {
                for (ActionEventCallback.LivingEntityHurt listener : call) {
                    listener.onLivingEntityHurt(action);
                }
            });

    public static final Event<ActionEventCallback.SickleHarvest> SICKLE_HARVEST =
            EventFactory.createArrayBacked(ActionEventCallback.SickleHarvest.class, call -> event -> {
                for (ActionEventCallback.SickleHarvest listener : call) {
                    listener.onSickleHarvest(event);
                }
            });

    public static void init() {
        registerEffectEvents();
        registerServerLifecycleEvents();
        registerInteractionEvents();
        registerLootEvents();
        registerRecipeEvents();
    }

    private static void registerEffectEvents() {
        SatiatedShieldEvent.register();
        FlatulenceServerEvent.register();
        PreservationEvent.register();
        FarmerArmorEffectEvent.register();
        InstantSmeltingEffectEvent.register();
        HinderEffectEvent.register();
        VitalityEffectEvent.register();
    }

    private static void registerServerLifecycleEvents() {
        AddVillageStructuresEvent.register();
        ServerEntityLoadEvent.register();
        TrashCanHideEvent.register();
    }

    private static void registerInteractionEvents() {
        WetFieldHoeUseEvent.register();
        CaterpillarChickenFeedEvent.register();
        SickleHarvestNetherWartEvent.register();
    }

    private static void registerLootEvents() {
        ExtraLootTableDrop.register();
    }

    private static void registerRecipeEvents() {
        MillstoneSpecialRecipeEvent.register();
        MillstoneSpecialFinishEvent.register();
        SpecialRecipeItemEvent.register();
    }
}
