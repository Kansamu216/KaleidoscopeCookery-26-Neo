package com.github.ysbbbbbb.kaleidoscopecookery.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler.MillstoneInputHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;

@EventBusSubscriber(modid = "kaleidoscope_cookery")
public class CapabilitiesRegistry {
   @SubscribeEvent
   public static void registerGenericItemHandlers(RegisterCapabilitiesEvent event) {
      event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlocks.OIL_POT_BE.get(), (b, v) -> b.createHandler());
      event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlocks.MILLSTONE_BE.get(), (b, v) -> new MillstoneInputHandler(b));
      event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlocks.STEAMER_BE.get(), (b, v) -> b.getItemHandler());
      event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlocks.KITCHEN_SHELF_BE.get(), (b, v) -> b.getItemHandler());
   }
}
