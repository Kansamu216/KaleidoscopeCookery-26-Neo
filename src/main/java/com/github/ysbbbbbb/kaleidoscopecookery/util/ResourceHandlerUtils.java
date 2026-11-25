package com.github.ysbbbbbb.kaleidoscopecookery.util;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Bridges the NeoForge 26.3 {@code transfer} resource API with the
 * stack-oriented code used throughout the mod.
 */
public final class ResourceHandlerUtils {
   private ResourceHandlerUtils() {
   }

   /** Reads a slot of a handler as an {@link ItemStack}. */
   public static ItemStack stackAt(ResourceHandler<ItemResource> handler, int index) {
      return handler.getResource(index).toStack(handler.getAmountAsInt(index));
   }

   /** Overwrites a slot from an {@link ItemStack}. */
   public static void setStack(ItemStacksResourceHandler handler, int index, ItemStack stack) {
      if (stack.isEmpty()) {
         handler.set(index, ItemResource.EMPTY, 0);
      } else {
         handler.set(index, ItemResource.of(stack), stack.getCount());
      }
   }

   /** Extracts up to {@code amount} from a slot, optionally simulating. */
   public static ItemStack extract(ResourceHandler<ItemResource> handler, int index, int amount, boolean simulate) {
      ItemResource resource = handler.getResource(index);
      if (resource.isEmpty()) {
         return ItemStack.EMPTY;
      }
      try (Transaction tx = Transaction.openRoot()) {
         int moved = handler.extract(index, resource, amount, tx);
         if (!simulate) {
            tx.commit();
         }
         return moved <= 0 ? ItemStack.EMPTY : resource.toStack(moved);
      }
   }

   /** Materializes every slot as a list of stacks. */
   public static List<ItemStack> toStacks(ResourceHandler<ItemResource> handler) {
      NonNullList<ItemStack> list = NonNullList.withSize(handler.size(), ItemStack.EMPTY);
      for (int i = 0; i < handler.size(); i++) {
         list.set(i, stackAt(handler, i));
      }
      return list;
   }
}
