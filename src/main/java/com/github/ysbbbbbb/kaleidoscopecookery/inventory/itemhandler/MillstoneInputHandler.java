package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

/**
 * Automation input for the millstone. It accepts an item only when the
 * millstone can match a recipe for that item and keeps the block entity input
 * synchronized with the transfer capability.
 */
public final class MillstoneInputHandler extends ItemStacksResourceHandler {
   private final MillstoneBlockEntity millstone;

   public MillstoneInputHandler(MillstoneBlockEntity millstone) {
      super(1);
      this.millstone = millstone;
      if (!millstone.getInput().isEmpty()) {
         this.set(0, ItemResource.of(millstone.getInput().getItem()), millstone.getInput().getCount());
      }
   }

   @Override
   public boolean isValid(int slot, ItemResource resource) {
      return this.millstone.isInputValid(resource.getItem());
   }

   @Override
   protected int getCapacity(int slot, ItemResource resource) {
      return 64;
   }

   @Override
   protected void onContentsChanged(int slot, ItemStack previousContents) {
      this.millstone.setInputFromCapability(this.getResource(slot).toStack(this.getAmountAsInt(slot)));
   }
}
