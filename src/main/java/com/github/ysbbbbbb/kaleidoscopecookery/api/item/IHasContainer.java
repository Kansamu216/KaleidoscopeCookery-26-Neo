package com.github.ysbbbbbb.kaleidoscopecookery.api.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemUtil;

public interface IHasContainer {
   Item getContainerItem();

   default ItemStack returnContainerToEntity(ItemStack stack, Level level, LivingEntity entity) {
      ItemStack carried = this.getContainerItem().getDefaultInstance();
      if (stack.isEmpty()) {
         return carried;
      } else {
         if (entity instanceof Player player) {
            player.getInventory().add(carried);
         } else {
            ItemEntity itemEntity = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), carried);
            level.addFreshEntity(itemEntity);
         }

         return stack;
      }
   }
}
