package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

/**
 * Automation view of the steamer's eight cooking slots.
 *
 * <p>The handler wraps the block entity's own {@code NonNullList}, so items inserted by a
 * hopper (or by {@code /item replace block}) are the same stacks the cooking tick uses.
 * Upstream 1.20.1 never exposed the steamer to automation; exposing it also makes the
 * content HUD verifiable without hand-placing items.
 */
public final class SteamerItemHandler extends ItemStacksResourceHandler {
   private final SteamerBlockEntity steamer;

   public SteamerItemHandler(SteamerBlockEntity steamer) {
      super(steamer.getItems());
      this.steamer = steamer;
   }

   @Override
   protected int getCapacity(int slot, ItemResource resource) {
      return 64;
   }

   @Override
   public boolean isValid(int slot, ItemResource resource) {
      return true;
   }

   @Override
   protected void onContentsChanged(int slot, ItemStack previousContents) {
      this.steamer.setChanged();
   }
}
