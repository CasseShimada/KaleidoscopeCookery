package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.ActionEventCallback;
import com.github.ysbbbbbb.kaleidoscopecookery.event.SpecialRecipeItemEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.recipe.MillstoneSpecialFinishEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.recipe.MillstoneSpecialRecipeEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.ServerEntityLoadEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect.FlatulenceServerEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect.PreservationEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect.SatiatedShieldEvent;
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
        SatiatedShieldEvent.register();
        FlatulenceServerEvent.register();
        PreservationEvent.register();
        ServerEntityLoadEvent.register();
        MillstoneSpecialRecipeEvent.register();
        MillstoneSpecialFinishEvent.register();
        SpecialRecipeItemEvent.register();
    }
}
