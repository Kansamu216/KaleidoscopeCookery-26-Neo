package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenShelfBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

/**
 * Single-slot storage for the kitchen shelf, holding up to {@link #CAPACITY}
 * items of one type. Serialization is handled by the block entity itself.
 */
public class KitchenShelfItemHandler extends ItemStacksResourceHandler {
   public static final int CAPACITY = 256;
   private final KitchenShelfBlockEntity blockEntity;

   public KitchenShelfItemHandler(KitchenShelfBlockEntity blockEntity) {
      super(1);
      this.blockEntity = blockEntity;
   }

   @Override
   protected int getCapacity(int slot, ItemResource resource) {
      return CAPACITY;
   }

   @Override
   public boolean isValid(int slot, ItemResource resource) {
      return true;
   }

   @Override
   protected void onContentsChanged(int slot, ItemStack previousContents) {
      this.blockEntity.refresh();
   }
}
