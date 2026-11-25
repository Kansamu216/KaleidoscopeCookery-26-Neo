package com.github.ysbbbbbb.kaleidoscopecookery.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/** Small bridges between the transfer API handlers and plain ItemStacks. */
public final class ResourceHandlerUtils {
   private ResourceHandlerUtils() {
   }

   public static ItemStack stackAt(ResourceHandler<ItemResource> handler, int index) {
      return handler.getResource(index).toStack(handler.getAmountAsInt(index));
   }

   public static void setStack(ItemStacksResourceHandler handler, int index, ItemStack stack) {
      if (stack.isEmpty()) {
         handler.set(index, ItemResource.EMPTY, 0);
      } else {
         handler.set(index, ItemResource.of(stack), stack.getCount());
      }
   }

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

   public static List<ItemStack> toStacks(ResourceHandler<ItemResource> handler) {
      List<ItemStack> list = new ArrayList<>();
      for (int i = 0; i < handler.size(); i++) {
         list.add(stackAt(handler, i));
      }
      return list;
   }
}
