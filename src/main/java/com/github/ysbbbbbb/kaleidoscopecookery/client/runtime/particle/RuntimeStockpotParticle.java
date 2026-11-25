package com.github.ysbbbbbb.kaleidoscopecookery.client.runtime.particle;

import com.github.ysbbbbbb.kaleidoscopecookery.client.particle.StockpotParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * Stockpot bubble particle, ported to the Minecraft 26 render-state particle API.
 *
 * <p>The colour and scale still come from {@link StockpotParticleOptions}; 26.x removed
 * the {@code getLightColor} hook in favour of {@code getLightCoords}, so full sky light
 * is requested there instead.
 */
public class RuntimeStockpotParticle extends SingleQuadParticle {
   private final SpriteSet sprites;

   protected RuntimeStockpotParticle(ClientLevel level, double x, double y, double z,
                                     double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
      super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites.first());
      this.friction = 0.96F;
      this.sprites = sprites;
      this.scale(1.0F);
      this.hasPhysics = false;
      this.setSpriteFromAge(sprites);
   }

   @Override
   protected int getLightCoords(float partialTick) {
      return LightCoordsUtil.FULL_SKY;
   }

   @Override
   protected SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   @Override
   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   public record Provider(SpriteSet sprites) implements ParticleProvider<StockpotParticleOptions> {
      @Override
      public Particle createParticle(@NotNull StockpotParticleOptions options, @NotNull ClientLevel level,
                                     double x, double y, double z,
                                     double xSpeed, double ySpeed, double zSpeed, @NotNull RandomSource random) {
         RuntimeStockpotParticle particle = new RuntimeStockpotParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
         Vector3f color = options.getColor();
         float scale = options.getScale() - 0.1F + random.nextFloat() * 0.2F;
         particle.setColor(color.x, color.y, color.z);
         particle.setSize(scale, scale);
         particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
         particle.setLifetime(random.nextInt(4) + 6);
         return particle;
      }
   }
}
