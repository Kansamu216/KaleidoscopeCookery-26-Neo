package com.github.ysbbbbbb.kaleidoscopecookery.util;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.UseRemainder;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.minecraft.world.entity.player.Inventory;
import org.apache.commons.lang3.tuple.Pair;

public class ItemUtils {
   public static void getItemToLivingEntity(LivingEntity entity, ItemStack stack) {
      getItemToLivingEntity(entity, stack, -1);
   }

   public static void getItemToLivingEntity(LivingEntity entity, ItemStack stack, int preferredSlot) {
      if (!stack.isEmpty()) {
         if (entity.getMainHandItem().isEmpty()) {
            RandomSource random = entity.level().getRandom();
            entity.setItemInHand(InteractionHand.MAIN_HAND, stack);
            entity.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         } else if (entity instanceof Player player) {
            if (preferredSlot >= 0 && preferredSlot < Inventory.INVENTORY_SIZE) {
               ItemStack existing = player.getInventory().getItem(preferredSlot);
               if (existing.isEmpty()) {
                  player.getInventory().setItem(preferredSlot, stack);
                  return;
               }
               if (ItemStack.isSameItemSameComponents(existing, stack) && existing.getCount() < existing.getMaxStackSize()) {
                  int moved = Math.min(stack.getCount(), existing.getMaxStackSize() - existing.getCount());
                  existing.grow(moved);
                  stack.shrink(moved);
                  if (stack.isEmpty()) {
                     return;
                  }
               }
            }
            if (!player.getInventory().add(stack)) {
               player.drop(stack, false, net.minecraft.util.Prediction.PREDICTED);
            }
         } else if (entity.level() instanceof ServerLevel serverLevel) {
            ItemEntity dropItem = entity.spawnAtLocation(serverLevel, stack);
            if (dropItem != null) {
               dropItem.setPickUpDelay(0);
            }
         }
      }
   }

   public static Pair<Integer, ItemStack> getLastStack(ResourceHandler<ItemResource> itemHandler) {
      for (int i = itemHandler.size(); i > 0; i--) {
         int index = i - 1;
         ItemStack stack = itemHandler.getResource(index).toStack(itemHandler.getAmountAsInt(index));
         if (!stack.isEmpty()) {
            return Pair.of(index, stack);
         }
      }

      return Pair.of(0, ItemStack.EMPTY);
   }

   public static Item getContainerItem(ItemStack stack) {
      if (stack.isEmpty()) {
         return Items.AIR;
      } else {
         UseRemainder useRemainder = stack.get(DataComponents.USE_REMAINDER);
         if (useRemainder != null) {
            return useRemainder.convertInto().create().getItem();
         } else {
            Item item = stack.getItem();
            ItemStackTemplate remainingItem = stack.getCraftingRemainder();
            if (remainingItem != null) {
               return remainingItem.create().getItem();
            } else if (item instanceof IHasContainer hasContainer) {
               return hasContainer.getContainerItem();
            } else if (stack.is(TagMod.BOWL_CONTAINER)) {
               return Items.BOWL;
            } else if (stack.is(TagMod.GLASS_BOTTLE_CONTAINER)) {
               return Items.GLASS_BOTTLE;
            } else if (stack.is(TagMod.BUCKET_CONTAINER)) {
               return Items.BUCKET;
            } else {
               return stack.is(Items.POTION) ? Items.GLASS_BOTTLE : Items.AIR;
            }
         }
      }
   }
}
