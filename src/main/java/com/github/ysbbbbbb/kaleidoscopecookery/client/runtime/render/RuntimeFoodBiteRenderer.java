package com.github.ysbbbbbb.kaleidoscopecookery.client.runtime.render;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.food.FoodBiteThreeByThreeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteThreeByThreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.client.runtime.model.RuntimeColdCutHamSlicesModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Renders the cold cut ham slices platter.
 *
 * <p>The block model of {@code cold_cut_ham_slices} is empty by design, so without this
 * renderer the block is invisible. Ported from the upstream 1.7.0
 * {@code FoodBiteThreeByThreeBlockEntityRender}, including the 180 degree Z flip, the
 * facing rotation and the per-bite part visibility.
 */
public final class RuntimeFoodBiteRenderer
   implements BlockEntityRenderer<FoodBiteThreeByThreeBlockEntity, RuntimeFoodBiteRenderer.State> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
      "kaleidoscope_cookery", "textures/block/cold_cut_ham_slices.png"
   );
   private final RuntimeColdCutHamSlicesModel model;

   public RuntimeFoodBiteRenderer(BlockEntityRendererProvider.Context context) {
      this.model = new RuntimeColdCutHamSlicesModel(context.bakeLayer(RuntimeColdCutHamSlicesModel.LAYER_LOCATION));
   }

   @Override
   public State createRenderState() {
      return new State();
   }

   @Override
   public void extractRenderState(
      FoodBiteThreeByThreeBlockEntity blockEntity,
      State state,
      float partialTicks,
      Vec3 cameraPosition,
      ModelFeatureRenderer.CrumblingOverlay breakProgress
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      BlockState blockState = blockEntity.getBlockState();
      if (!(blockState.getBlock() instanceof FoodBiteThreeByThreeBlock block)) {
         return;
      }
      state.facing = blockState.getValue(FoodBiteBlock.FACING);
      state.bites = blockState.getValue(block.getBites());
   }

   @Override
   public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
      poseStack.pushPose();
      poseStack.translate(0.5F, 1.5F, 0.5F);
      poseStack.mulPose(Axis.ZN.rotationDegrees(180.0F));
      poseStack.mulPose(Axis.YN.rotationDegrees(180.0F - state.facing.get2DDataValue() * 90.0F));
      collector.submitModel(
         this.model, new RuntimeColdCutHamSlicesModel.State(state.bites),
         poseStack, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress
      );
      poseStack.popPose();
   }

   @Override
   public boolean shouldRenderOffScreen() {
      return true;
   }

   public static final class State extends BlockEntityRenderState {
      private Direction facing = Direction.SOUTH;
      private int bites;
   }
}
