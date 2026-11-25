package com.github.ysbbbbbb.kaleidoscopecookery.client.runtime;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenShelfBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = "kaleidoscope_cookery", value = Dist.CLIENT)
public final class RuntimeKitchenShelfOverlay implements GuiLayer {
   private static final Identifier ID = Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "runtime_kitchen_shelf_overlay");
   // Standard vanilla item slot is 18px; a single centred icon uses that width for the anchor.
   private static final int SLOT_SIZE = 18;

   @SubscribeEvent
   public static void register(RegisterGuiLayersEvent event) {
      event.registerAbove(VanillaGuiLayers.CROSSHAIR, ID, new RuntimeKitchenShelfOverlay());
   }

   @Override
   public void render(GuiGraphicsExtractor gui, DeltaTracker deltaTracker) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null || player.isSpectator()
         || !(minecraft.hitResult instanceof BlockHitResult hit)
         || hit.getType() != HitResult.Type.BLOCK) {
         return;
      }

      BlockPos pos = hit.getBlockPos();
      if (!player.level().getBlockState(pos).is(ModBlocks.KITCHEN_SHELF.get())
         || !(player.level().getBlockEntity(pos) instanceof KitchenShelfBlockEntity shelf)) {
         return;
      }

      // The shelf stores a single item type (capacity 256); draw one icon + count.
      ItemStack stored = shelf.getStoredStack();
      if (!stored.isEmpty()) {
         // GUI-scale-adaptive anchor: centre one 18px slot on the crosshair, nudged just below it.
         int x = gui.guiWidth() / 2 - SLOT_SIZE / 2;
         int y = gui.guiHeight() / 2 + SLOT_SIZE / 2;
         gui.fakeItem(stored, x, y);
         gui.itemDecorations(minecraft.font, stored, x, y);
      }
   }
}
