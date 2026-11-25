package com.github.ysbbbbbb.kaleidoscopecookery.client.runtime.render;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenShelfBlockEntity;
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
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

/**
 * Kitchen shelf renderer.
 *
 * <p>26.x render-state pipeline (extract / submit) ported from the 1.20.1
 * {@code KitchenShelfBlockEntityRender}. The per-slot layout (two columns at ±0.25, layers at
 * {@code 0.7 - 0.5 * layer}, rows at {@code 0.3 - 0.2 * odd}, 45° X tilt, 0.5 scale) is a
 * one-to-one port of the official values. The yaw uses the official
 * {@code facing.toYRot() + 180} expression wrapped in {@code Axis.YN}; since {@code -x} equals
 * {@code 360 - x}, the sign difference against the old {@code get2DDataValue() * 90} is exactly
 * the official +180 offset.</p>
 */
public final class RuntimeKitchenShelfRenderer
   implements BlockEntityRenderer<KitchenShelfBlockEntity, RuntimeKitchenShelfRenderer.State> {
   // Show at most this many items on the shelf, matching the official renderer.
   private static final int MAX_DISPLAY = 16;
   private final ItemModelResolver itemModelResolver;

   public RuntimeKitchenShelfRenderer(BlockEntityRendererProvider.Context context) {
      this.itemModelResolver = context.itemModelResolver();
   }

   @Override
   public State createRenderState() {
      return new State();
   }

   @Override
   public void extractRenderState(
      KitchenShelfBlockEntity blockEntity,
      State state,
      float partialTicks,
      Vec3 cameraPosition,
      ModelFeatureRenderer.CrumblingOverlay breakProgress
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      state.facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
      state.items = new ArrayList<>();
      ItemStack stored = blockEntity.getStoredStack();
      if (stored.isEmpty()) {
         return;
      }
      // Each visible entry shows a single item of the stored type.
      ItemStack display = stored.copyWithCount(1);
      int displayCount = Math.min(MAX_DISPLAY, stored.getCount());
      for (int index = 0; index < displayCount; index++) {
         ItemStackRenderState itemState = new ItemStackRenderState();
         this.itemModelResolver.updateForTopItem(
            itemState, display, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0
         );
         state.items.add(new ShelfItem(itemState, index));
      }
   }

   @Override
   public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
      if (state.items.isEmpty()) {
         return;
      }
      poseStack.pushPose();
      poseStack.translate(0.5F, 0.0F, 0.5F);
      // Official: mulPose(Axis.YN.rotationDegrees(facing.toYRot() + 180)).
      poseStack.mulPose(Axis.YN.rotationDegrees(state.facing.toYRot() + 180.0F).get(new org.joml.Matrix4f()));
      // Layout relative to a north-facing shelf: rows of two, from back to front, stacked upward.
      for (ShelfItem shelfItem : state.items) {
         int index = shelfItem.index();
         int layer = index / 8;
         int slot = index % 8;
         int odd = slot / 2;
         poseStack.pushPose();
         poseStack.translate(
            slot % 2 == 0 ? -0.25F : 0.25F,
            0.7F + layer * -0.5F,
            0.3F - odd * 0.20F
         );
         poseStack.mulPose(Axis.XP.rotationDegrees(45.0F).get(new org.joml.Matrix4f()));
         poseStack.scale(0.5F, 0.5F, 0.5F);
         shelfItem.item().submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
         poseStack.popPose();
      }
      poseStack.popPose();
   }

   private record ShelfItem(ItemStackRenderState item, int index) {
   }

   public static final class State extends BlockEntityRenderState {
      private List<ShelfItem> items = List.of();
      private Direction facing = Direction.NORTH;
   }
}
