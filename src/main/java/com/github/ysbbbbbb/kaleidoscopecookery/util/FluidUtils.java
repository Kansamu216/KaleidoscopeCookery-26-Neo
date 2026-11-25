package com.github.ysbbbbbb.kaleidoscopecookery.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.minecraft.world.level.material.Fluid;

/**
 * Fluid transfer helpers using the NeoForge 26.3 {@code transfer} API.
 */
public final class FluidUtils {
   private FluidUtils() {
   }

   /** Drains fluid from the held container into the target handler. */
   public static boolean emptyItem(LivingEntity user, ItemStack bucket, ResourceHandler<FluidResource> handler, int amount) {
      ItemStack copy = bucket.copyWithCount(1);
      ResourceHandler<FluidResource> itemHandler = itemFluidHandler(copy);
      if (itemHandler == null) {
         return false;
      }
      try (Transaction tx = Transaction.openRoot()) {
         FluidResource resource = firstFluid(itemHandler);
         if (resource.isEmpty()) {
            return false;
         }
         int moved = itemHandler.extract(resource, amount, tx);
         if (moved <= 0) {
            return false;
         }
         int accepted = handler.insert(resource, moved, tx);
         if (accepted < moved) {
            return false;
         }
         tx.commit();
         afterFluidMove(user, bucket, copy, resource, SoundActions.BUCKET_EMPTY);
         return true;
      }
   }

   /** Fills the held container from the target handler. */
   public static boolean fillItem(LivingEntity user, ItemStack bucket, ResourceHandler<FluidResource> handler, int amount) {
      ItemStack copy = bucket.copyWithCount(1);
      ResourceHandler<FluidResource> itemHandler = itemFluidHandler(copy);
      if (itemHandler == null) {
         return false;
      }
      try (Transaction tx = Transaction.openRoot()) {
         FluidResource resource = firstFluid(handler);
         if (resource.isEmpty()) {
            return false;
         }
         int drained = handler.extract(resource, amount, tx);
         if (drained <= 0) {
            return false;
         }
         int accepted = itemHandler.insert(resource, drained, tx);
         if (accepted < drained) {
            return false;
         }
         tx.commit();
         afterFluidMove(user, bucket, copy, resource, SoundActions.BUCKET_FILL);
         return true;
      }
   }

   public static boolean hasFluid(ItemStack stack) {
      ResourceHandler<FluidResource> handler = itemFluidHandler(stack);
      return handler != null && !firstFluid(handler).isEmpty();
   }

   public static ResourceHandler<FluidResource> itemFluidHandler(ItemStack stack) {
      if (stack.isEmpty()) {
         return null;
      }
      return ItemAccess.forStack(stack).oneByOne().getCapability(Capabilities.Fluid.ITEM);
   }

   public static FluidResource firstFluid(ResourceHandler<FluidResource> handler) {
      for (int i = 0; i < handler.size(); i++) {
         FluidResource resource = handler.getResource(i);
         if (!resource.isEmpty() && handler.getAmountAsLong(i) > 0) {
            return resource;
         }
      }
      return FluidResource.EMPTY;
   }

   /** First non-empty fluid stack in a handler, or empty. */
   public static net.neoforged.neoforge.fluids.FluidStack firstStack(ResourceHandler<FluidResource> handler) {
      for (int i = 0; i < handler.size(); i++) {
         FluidResource r = handler.getResource(i);
         if (!r.isEmpty() && handler.getAmountAsLong(i) > 0) {
            return r.toStack((int) handler.getAmountAsLong(i));
         }
      }
      return net.neoforged.neoforge.fluids.FluidStack.EMPTY;
   }

   private static void afterFluidMove(LivingEntity user, ItemStack bucket, ItemStack copy, FluidResource resource, net.neoforged.neoforge.common.SoundAction soundAction) {
      ItemStack result = copy.getCraftingRemainder() != null ? copy.getCraftingRemainder().create() : ItemStack.EMPTY;
      if (!(user instanceof Player player && player.isCreative())) {
         bucket.shrink(1);
      }
      if (!result.isEmpty()) {
         ItemUtils.getItemToLivingEntity(user, result);
      }
      Fluid fluid = resource.value();
      SoundEvent sound = fluid.getFluidType().getSound((net.minecraft.world.entity.Entity) null, soundAction);
      if (sound != null) {
         user.playSound(sound);
      }
   }
}
