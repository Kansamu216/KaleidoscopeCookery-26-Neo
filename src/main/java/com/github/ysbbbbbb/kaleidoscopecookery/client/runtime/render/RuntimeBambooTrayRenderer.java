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
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Bamboo tray renderer: draws the four slot contents on top of the tray.
 *
 * <p>26.x render-state pipeline (extract / submit) ported from the 1.20.1
 * {@code BambooTrayBlockEntityRender}. The geometry (slot quadrant layout at 0.25 / 0.75, base
 * height 0.14, the light z-jitter, the {@code copy * 0.01 + slot * 0.005} stack lift, the
 * 90° X tilt plus a random Z spin and a 0.5 scale) is a one-to-one port of the official values.
 * The random jitter/spin is evaluated once in {@link #extractRenderState} (using the same seeded
 * {@link RandomSource} as the original) and baked into each {@link TrayCopy} record, so the
 * submit stage stays a pure replay of pre-computed transforms.</p>
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
      state.copies = new ArrayList<>();

      // The "stand" variant only adds legs below the tray; the top surface stays at y=0..2, so the
      // item height does not depend on the stand property (matches the 1.20.1 renderer).
      for (int slot = 0; slot < SLOT_POSITIONS.length; slot++) {
         ItemStack stack = blockEntity.getItem(slot);
         if (stack.isEmpty()) {
            continue;
         }
         // Pile depth: identical formula to the 1.20.1 renderer.
         int copies = stack.getCount() == 1 ? 1 : Math.min(5, (stack.getCount() - 1) / 16 + 2);
         RandomSource random = RandomSource.create(blockEntity.getBlockPos().asLong() + slot * 31L);
         for (int copy = 0; copy < copies; copy++) {
            // Random draws are consumed in the same order as the original so the jitter matches.
            double xOffset = (random.nextDouble() - 0.5) * 0.1;
            double zOffset = (random.nextDouble() - 0.5) * 0.1;
            float zRot = random.nextFloat() * 360.0F;

            ItemStackRenderState itemState = new ItemStackRenderState();
            this.itemModelResolver.updateForTopItem(
               itemState, stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null,
               slot * 7 + copy
            );
            state.copies.add(new TrayCopy(
               itemState,
               SLOT_POSITIONS[slot][0] + xOffset,
               0.14 + copy * 0.01 + slot * 0.005,
               SLOT_POSITIONS[slot][1] + zOffset,
               zRot
            ));
         }
      }
   }

   @Override
   public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
      if (state.copies.isEmpty()) {
         return;
      }

      for (TrayCopy copy : state.copies) {
         poseStack.pushPose();
         poseStack.translate(copy.x(), copy.y(), copy.z());
         // Lay the item flat on the tray (same orientation as the 1.20.1 tray renderer).
         poseStack.mulPose(Axis.XP.rotationDegrees(90.0F).get(new org.joml.Matrix4f()));
         poseStack.mulPose(Axis.ZP.rotationDegrees(copy.zRot()).get(new org.joml.Matrix4f()));
         poseStack.scale(0.5F, 0.5F, 0.5F);
         copy.item().submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
         poseStack.popPose();
      }
   }

   private record TrayCopy(ItemStackRenderState item, double x, double y, double z, float zRot) {
   }

   public static final class State extends BlockEntityRenderState {
      private List<TrayCopy> copies = List.of();
   }
}
