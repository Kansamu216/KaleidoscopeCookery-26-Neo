package com.github.ysbbbbbb.kaleidoscopecookery.client.runtime;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
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
public final class RuntimeSteamerOverlay implements GuiLayer {
   private static final Identifier ID = Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "runtime_steamer_overlay");
   // Display at most 9 slots; the steamer holds 8, but the grid is capped anyway.
   private static final int MAX_SLOTS = 9;
   private static final int PER_ROW = 4;

   @SubscribeEvent
   public static void register(RegisterGuiLayersEvent event) {
      event.registerAbove(VanillaGuiLayers.CROSSHAIR, ID, new RuntimeSteamerOverlay());
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
      if (!player.level().getBlockState(pos).is(ModBlocks.STEAMER.get())
         || !(player.level().getBlockEntity(pos) instanceof SteamerBlockEntity steamer)) {
         return;
      }

      int startX = gui.guiWidth() / 2 - 38;
      int startY = gui.guiHeight() / 2 + 4;
      int x = startX;
      int y = startY;
      int count = 0;

      for (ItemStack stack : steamer.getItems()) {
         if (count >= MAX_SLOTS) {
            break;
         }
         if (!stack.isEmpty()) {
            gui.fakeItem(stack, x, y);
            gui.itemDecorations(minecraft.font, stack, x, y);

            count++;
            x += 20;
            if (count % PER_ROW == 0) {
               x = startX;
               y += 20;
            }
         }
      }
   }
}
