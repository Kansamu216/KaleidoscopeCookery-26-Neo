package com.github.ysbbbbbb.kaleidoscopecookery.client.runtime.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

/**
 * Cooking smoke particle, ported to the Minecraft 26 render-state particle API.
 *
 * <p>26.x replaced {@code TextureSheetParticle#getRenderType()} with
 * {@code SingleQuadParticle#getLayer()} and removed the old
 * {@code ParticleRenderType.PARTICLE_SHEET_OPAQUE}; the animation itself (fade in over
 * the first 32 ticks, then fade out) is unchanged from upstream 1.7.0.
 */
public class RuntimeCookingParticle extends SingleQuadParticle {
   private final SpriteSet sprites;

   protected RuntimeCookingParticle(ClientLevel level, double x, double y, double z,
                                    double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
      super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites.first());
      this.friction = 0.96F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = sprites;
      this.xd *= 0.1F;
      this.yd *= 0.1F;
      this.zd *= 0.1F;
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.quadSize *= 0.75F;
      this.lifetime = 50;
      this.setSpriteFromAge(sprites);
   }

   @Override
   protected SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   @Override
   public float getQuadSize(float partialTick) {
      return this.quadSize * Mth.clamp((this.age + partialTick) / this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   @Override
   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
      @Override
      public Particle createParticle(@NotNull SimpleParticleType options, @NotNull ClientLevel level,
                                     double x, double y, double z,
                                     double xSpeed, double ySpeed, double zSpeed, @NotNull RandomSource random) {
         return new RuntimeCookingParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
      }
   }
}
