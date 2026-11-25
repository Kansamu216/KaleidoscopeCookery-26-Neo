package com.github.ysbbbbbb.kaleidoscopecookery.client.runtime.render;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.BambooTrayBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Bamboo tray renderer: draws the four slot contents on top of the tray.
 *
 * <p>26.x render-state pipeline (extract / submit) ported from the 1.20.1
 * {@code BambooTrayBlockEntityRender}. Slot positions follow the 1.20.1 slot quadrant layout
 * (0.25 / 0.75 on both axes). The multi-copy "pile" effect of the original (drawing several
 * lightly jittered copies for larger stacks) is intentionally dropped here: this renderer draws a
 * single centred item per non-empty slot, which keeps the extract state a plain per-slot list.</p>
 */
public final class RuntimeBambooTrayRenderer
   implements BlockEntityRenderer<BambooTrayBlockEntity, RuntimeBambooTrayRenderer.State> {
   // Slot layout: slot 0..3 map to the four quadrants, matching BambooTrayBlock's click mapping.
   private static final double[][] SLOT_POSITIONS = {
      {0.25, 0.25}, {0.75, 0.25}, {0.25, 0.75}, {0.75, 0.75}
   };

   private final ItemModelResolver itemModelResolver;

   public RuntimeBambooTrayRenderer(BlockEntityRendererProvider.Context context) {
      this.itemModelResolver = context.itemModelResolver();
   }

   @Override
   public State createRenderState() {
      return new State();
   }

   @Override
   public void extractRenderState(
      BambooTrayBlockEntity blockEntity,
      State state,
      float partialTicks,
      Vec3 cameraPosition,
      ModelFeatureRenderer.CrumblingOverlay breakProgress
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      state.items = new ArrayList<>();

      // The "stand" variant only adds legs below the tray; the top surface stays at y=0..2, so the
      // item height does not depend on the stand property (matches the 1.20.1 renderer).
      for (int slot = 0; slot < SLOT_POSITIONS.length; slot++) {
         ItemStack stack = blockEntity.getItem(slot);
         if (stack.isEmpty()) {
            continue;
         }
         ItemStackRenderState itemState = new ItemStackRenderState();
         this.itemModelResolver.updateForTopItem(
            itemState, stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null,
            (int)(blockEntity.getBlockPos().asLong() + slot)
         );
         state.items.add(new TrayItem(itemState, slot));
      }
   }

   @Override
   public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
      if (state.items.isEmpty()) {
         return;
      }

      for (TrayItem trayItem : state.items) {
         int slot = trayItem.slot();
         double[] pos = SLOT_POSITIONS[slot];
         poseStack.pushPose();
         poseStack.translate(pos[0], 0.14F, pos[1]);
         // Lay the item flat on the tray (same orientation as the 1.20.1 tray renderer).
         poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
         poseStack.scale(0.5F, 0.5F, 0.5F);
         trayItem.item().submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
         poseStack.popPose();
      }
   }

   private record TrayItem(ItemStackRenderState item, int slot) {
   }

   public static final class State extends BlockEntityRenderState {
      private List<TrayItem> items = List.of();
   }
}
