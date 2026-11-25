package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TeaBannerBlockEntity;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Block item for the tea banner. Color and pattern are read back from the
 * stored block entity data and shown as tooltips. 26.x removed
 * BannerPatternItem, so the pattern display name comes from the stored pattern
 * item's hover name.
 */
public class TeaBannerItem extends BlockItem {
   public TeaBannerItem(Block block, Item.Properties properties) {
      super(block, properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
      CompoundTag tag = getBlockEntityTag(stack);
      DyeColor color = tag == null ? DyeColor.RED : TeaBannerBlockEntity.getColor(tag);
      ItemStack patternItem = tag == null ? ItemStack.EMPTY : TeaBannerBlockEntity.getPatternItem(tag);

      MutableComponent pattern = Component.translatable("tooltip.kaleidoscope_cookery.tea_banner.pattern.tea");
      if (!patternItem.isEmpty()) {
         pattern = patternItem.getHoverName().copy();
      }
      MutableComponent colorName = Component.translatable("color.minecraft." + color.getName());

      tooltip.accept(Component.translatable("tooltip.kaleidoscope_cookery.tea_banner.color", colorName).withStyle(ChatFormatting.GRAY));
      tooltip.accept(Component.translatable("tooltip.kaleidoscope_cookery.tea_banner.pattern", pattern).withStyle(ChatFormatting.GRAY));
   }

   private static CompoundTag getBlockEntityTag(ItemStack stack) {
      TypedEntityData<BlockEntityType<?>> data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
      return data == null ? null : data.copyTagWithoutId();
   }
}
