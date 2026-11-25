package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration;
import net.minecraft.world.level.storage.ValueInput;

import net.minecraft.world.level.storage.ValueOutput;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ResourceHandlerUtils;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class FruitBasketBlockEntity extends BaseBlockEntity {
   public static final String ITEMS = "BasketItems";
   private final ItemStacksResourceHandler items = new ItemStacksResourceHandler(8);

   public FruitBasketBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super(ModBlocks.FRUIT_BASKET_BE.get(), pPos, pBlockState);
   }

   public void putOn(ItemStack stack) {
      if (stack.getItem().canFitInsideContainerItems()) {
         ItemStack reminder = ItemUtil.insertItemReturnRemaining(this.items, stack.copy(), false, null);
         if (stack.getCount() != reminder.getCount()) {
            stack.shrink(stack.getCount() - reminder.getCount());
            if (this.level != null) {
               this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS);
            }

            this.refresh();
         }
      }
   }

   public void takeOut(Player player) {
      for (int i = 0; i < this.items.size(); i++) {
         ItemStack stack = ResourceHandlerUtils.stackAt(this.items, i);
         if (!stack.isEmpty()) {
            ItemStack extractItem = ResourceHandlerUtils.extract(this.items, i, this.items.getCapacityAsInt(i, this.items.getResource(i)), false);
            player.getInventory().add(extractItem);
            if (this.level != null) {
               this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS);
            }

            this.refresh();
            return;
         }
      }
   }

   protected void saveAdditional(ValueOutput tag) {
      super.saveAdditional(tag);
      tag.putChild("BasketItems", this.items);
   }

   protected void loadAdditional(ValueInput tag) {
      super.loadAdditional(tag);
tag.readChild("BasketItems",       this.items);
   }

   public ItemStacksResourceHandler getItems() {
      return this.items;
   }

   public void setItems(ItemStacksResourceHandler items, RegistryAccess access) {
      for (int slot = 0; slot < this.items.size(); slot++) {
         ItemStack stack = slot < items.size() ? ResourceHandlerUtils.stackAt(items, slot) : ItemStack.EMPTY;
         ResourceHandlerUtils.setStack(this.items, slot, stack);
      }
      this.refresh();
   }
}
