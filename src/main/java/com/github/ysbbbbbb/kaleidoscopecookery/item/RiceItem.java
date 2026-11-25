package com.github.ysbbbbbb.kaleidoscopecookery.item;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRegistrationProperties;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.block.Block;

public class RiceItem extends BlockItem {
   public RiceItem() {
      super((Block)ModBlocks.RICE_CROP.get(), ModRegistrationProperties.itemProperties()
         .component(net.minecraft.core.component.DataComponents.COMPOSTABLE,
            new net.minecraft.world.item.component.Compostable(
               new net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Constant(1))));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
   }
}
