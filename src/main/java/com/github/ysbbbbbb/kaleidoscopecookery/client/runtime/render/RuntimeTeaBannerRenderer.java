package com.github.ysbbbbbb.kaleidoscopecookery.client.runtime.render;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TeaBannerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Runtime block entity renderer for the tea banner.
 *
 * <p>26.x uses the render-state extraction pipeline: {@code extractRenderState} copies the
 * block entity data (color / facing / pattern) into a plain {@code State}, and
 * {@code submit} queues geometry through the {@code SubmitNodeCollector}. The visual logic is
 * ported from the 1.20.1 reference renderer but reduced to a small box-model banner: two flat
 * cloth planes (front + back, double sided) hung from a thin top rod.
 */
public final class RuntimeTeaBannerRenderer
   implements BlockEntityRenderer<TeaBannerBlockEntity, RuntimeTeaBannerRenderer.State> {
   private static final String NAMESPACE = "kaleidoscope_cookery";
   // Fallback pattern shown when the banner has no supported pattern item applied.
   private static final String DEFAULT_PATTERN = "tea";

   private final BannerModel bannerModel;
   private final PatternModel patternModel;
   private final ModelPart rod;

   public RuntimeTeaBannerRenderer(BlockEntityRendererProvider.Context context) {
      ModelPart root = createBodyLayer().bakeRoot();
      this.bannerModel = new BannerModel(root);
      this.patternModel = new PatternModel(createPatternLayer().bakeRoot());
      this.rod = root.getChild("rod");
   }

   @Override
   public State createRenderState() {
      return new State();
   }

   @Override
   public void extractRenderState(
      TeaBannerBlockEntity blockEntity,
      State state,
      float partialTicks,
      Vec3 cameraPosition,
      ModelFeatureRenderer.CrumblingOverlay breakProgress
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      // Color cloth texture: one png per DyeColor name.
      state.colorTexture = Identifier.fromNamespaceAndPath(
         NAMESPACE, "textures/entity/banner/" + blockEntity.getColor().getName() + ".png"
      );
      // Pattern overlay texture (falls back to the woven "tea" pattern).
      String patternName = TeaBannerBlockEntity.getPatternTexture(blockEntity.getPatternItem());
      if (patternName == null) {
         patternName = DEFAULT_PATTERN;
      }
      state.patternTexture = Identifier.fromNamespaceAndPath(
         NAMESPACE, "textures/entity/pattern/" + patternName + ".png"
      );
      state.facing = blockEntity.getFacing();
      // Per-position phase so neighbouring banners do not sway in lockstep.
      long gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0L;
      long phase = blockEntity.getBlockPos().asLong() + gameTime;
      state.waveAngle = 0.05F * Mth.cos((float) (Math.PI * 2.0 * (phase % 100L) / 100.0));
   }

   @Override
   public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
      int facingDegrees = state.facing.get2DDataValue() * 90;

      // Apply the cloth sway once per frame; the model resets its own pose in setupAnim.
      this.rod.getChild("front").zRot = state.waveAngle;
      this.rod.getChild("back").zRot = state.waveAngle;

      poseStack.pushPose();
      // Move the origin to the top of the block and mirror the model like the 1.20.1 renderer
      // so the cloth hangs down (-Y) and faces the stored direction.
      poseStack.translate(0.5F, 1.5F, 0.5F);
      poseStack.mulPose(Axis.ZN.rotationDegrees(180.0F).get(new org.joml.Matrix4f()));
      poseStack.mulPose(Axis.YN.rotationDegrees(180.0F - facingDegrees).get(new org.joml.Matrix4f()));
      collector.submitModel(
         this.bannerModel,
         state,
         poseStack,
         state.colorTexture,
         state.lightCoords,
         OverlayTexture.NO_OVERLAY,
         0
      );
      // Second pass: the pattern overlay is a slightly larger cloth plane drawn on top of the
      // base cloth with its own texture, exactly like the upstream 1.7.0 renderer.
      collector.submitModel(
         this.patternModel,
         state,
         poseStack,
         state.patternTexture,
         state.lightCoords,
         OverlayTexture.NO_OVERLAY,
         0
      );
      poseStack.popPose();
   }

   @Override
   public boolean shouldRenderOffScreen() {
      return true;
   }

   /**
    * Simplified banner geometry: a thin top rod plus two flat cloth planes (front / back)
    * spanning 28 x 41 px inside the 128x128 banner texture. Both planes share the same UV
    * window so the cloth reads consistently from either side (double sided).
    */
   private static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();

      PartDefinition rod = root.addOrReplaceChild(
         "rod",
         CubeListBuilder.create()
            // Hanging rod: the vertical rope plus the horizontal bar (upstream NormalBannerModel).
            .texOffs(64, 0).addBox(-1.0F, -60.0F, -1.0F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 64).addBox(-2.0F, -64.0F, -14.0F, 4.0F, 4.0F, 28.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, -1.5708F, 0.0F)
      );

      rod.addOrReplaceChild(
         "front",
         CubeListBuilder.create()
            .texOffs(0, -28).addBox(0.0F, 0.0F, -14.0F, 0.0F, 41.0F, 28.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, -60.0F, 0.0F)
      );
      rod.addOrReplaceChild(
         "back",
         CubeListBuilder.create()
            .texOffs(0, -28).addBox(0.0F, 0.0F, -14.0F, 0.0F, 41.0F, 28.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, -60.0F, 0.0F)
      );

      return LayerDefinition.create(mesh, 128, 128);
   }

   /**
    * Pattern overlay geometry (upstream {@code PatternModel}): two plane strips placed 2.25px in
    * front of / behind the base cloth, 32x48px inside the 64x96 pattern texture.
    */
   private static LayerDefinition createPatternLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition bone = root.addOrReplaceChild(
         "bone", CubeListBuilder.create(),
         PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, -1.5708F, 0.0F)
      );
      bone.addOrReplaceChild(
         "back",
         CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, 0.0F, -16.0F, 0.0F, 48.0F, 32.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.25F, -65.0F, 0.0F)
      );
      bone.addOrReplaceChild(
         "front",
         CubeListBuilder.create().texOffs(0, -32).addBox(0.0F, 0.0F, -16.0F, 0.0F, 48.0F, 32.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.25F, -65.0F, 0.0F)
      );
      return LayerDefinition.create(mesh, 64, 96);
   }

   /** Minimal box model: only needs the generic render type family, no per-frame state. */
   private static final class BannerModel extends Model<State> {
      private final ModelPart front;
      private final ModelPart back;

      private BannerModel(ModelPart root) {
         super(root, RenderTypes::entityCutout);
         ModelPart rod = root.getChild("rod");
         this.front = rod.getChild("front");
         this.back = rod.getChild("back");
      }

      @Override
      public void setupAnim(State state) {
         super.setupAnim(state);
         this.front.zRot = state.waveAngle;
         this.back.zRot = state.waveAngle;
      }
   }

   /** The pattern overlay model; the wave angle is applied by the same state as the base cloth. */
   private static final class PatternModel extends Model<State> {
      private final ModelPart front;
      private final ModelPart back;

      private PatternModel(ModelPart root) {
         super(root, RenderTypes::entityCutout);
         ModelPart bone = root.getChild("bone");
         this.front = bone.getChild("front");
         this.back = bone.getChild("back");
      }

      @Override
      public void setupAnim(State state) {
         super.setupAnim(state);
         this.front.zRot = state.waveAngle;
         this.back.zRot = state.waveAngle;
      }
   }

   public static final class State extends BlockEntityRenderState {
      private Identifier colorTexture = Identifier.fromNamespaceAndPath(NAMESPACE, "textures/entity/banner/red.png");
      private Identifier patternTexture = Identifier.fromNamespaceAndPath(NAMESPACE, "textures/entity/pattern/tea.png");
      private Direction facing = Direction.NORTH;
      private float waveAngle;
   }
}
