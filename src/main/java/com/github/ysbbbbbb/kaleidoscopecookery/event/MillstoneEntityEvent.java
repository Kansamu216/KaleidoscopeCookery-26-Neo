package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Millstone entity event handler.
 * When a living entity that is currently bound to a millstone is attacked, it is
 * unbound from the millstone and the millstone rotation is snapshotted, matching
 * the official 1.20.1 behavior.
 * <p>
 * 26.1.2 note: the old Forge {@code LivingAttackEvent} no longer exists; it is
 * replaced by NeoForge's {@link LivingIncomingDamageEvent}, fired from
 * {@code LivingEntity.hurt} before damage is processed.
 * <p>
 * The millstone is a nine-part block but only its CENTER part carries the block
 * entity, so scanning the nearby volume for any millstone block entity and
 * matching its bound entity is sufficient (the center is always within the
 * searched range around the entity).
 */
@EventBusSubscriber(modid = "kaleidoscope_cookery")
public class MillstoneEntityEvent {
    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide()) {
            return;
        }
        BlockPos origin = entity.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-5, -2, -5), origin.offset(5, 2, 5))) {
            if (level.getBlockEntity(pos) instanceof MillstoneBlockEntity millstone
                    && millstone.isBoundTo(entity)) {
                millstone.unbindEntity();
                millstone.setCacheRot(millstone.getRotation(level, 0.0F));
                millstone.refresh();
                return;
            }
        }
    }
}
